# ⏱️ Wait Command Pattern Reference

## Overview
This framework supports **4 types of wait commands** to handle different synchronization scenarios:

1. **⏰ Time-based Waits** - Fixed duration waits
2. **📄 Page Load Waits** - Wait for page/network to stabilize  
3. **👁️ Element Appearance Waits** - Wait for elements to become visible
4. **🚫 Element Disappearance Waits** - Wait for elements to hide/disappear

---

## 1️⃣ Time-Based Waits (Fixed Duration)

### Pattern
```regex
^(?:wait|pause)(?:\s+for)?\s+(\d+)\s*(?:second|sec|s)(?:s)?
```

### Supported Variations

✅ **All these work:**
```gherkin
And wait for 20 seconds
And wait for 20 sec
And wait 20 seconds
And wait 20 sec
And wait 20s
And pause for 5 seconds
And pause 3 sec
```

### Implementation Details
- **Action Type**: `wait_time`
- **Extracted Value**: Number of seconds (captured in elementName field)
- **Behavior**: Thread sleeps for specified duration
- **Timeout**: None (fixed duration)

### Example Usage
```gherkin
Given Open the browser and go to "https://example.com"
When Click "Submit"
And wait for 3 seconds
Then Verify "Success" is displayed
```

---

## 2️⃣ Page Load Waits (Network Stability)

### Pattern
```regex
^(?:wait|pause)(?:\s+for)?\s+(?:page|network)(?:\s+to)?(?:\s+be)?(?:\s+)?(?:load(?:ed)?(?:\s+completed)?|idle|ready)
```

### Supported Variations

✅ **All these work:**
```gherkin
And wait for page load
And wait for page to load
And wait for page to be loaded
And wait for page load completed
And wait for network idle
And wait for page ready
And pause for page load
And wait page loaded
```

### Implementation Details
- **Action Type**: `wait_page`
- **Extracted Value**: None needed
- **Behavior**: Waits for Playwright's `NETWORKIDLE` state
- **Timeout**: Default Playwright timeout (30s typically)

### Example Usage
```gherkin
Given Open the browser and go to "https://slow-website.com"
And wait for page load
When Click "Load More"
And wait for page to be loaded
Then Verify "Items" table is visible
```

---

## 3️⃣ Element Appearance Waits (Visibility)

### Pattern
```regex
^(?:wait|pause)(?:\s+for|\s+until)?\s+["']?([^"']+)["']?\s+(?:to\s+)?(?:appear|show|be\s+visible|is\s+visible|display|be\s+displayed)
```

### Supported Variations

✅ **All these work:**
```gherkin
And wait for "Submit" to appear
And wait for "Success message" to be visible
And wait until "Login button" is visible
And wait for 'Processing' to be displayed
And pause for "Notification" to appear
And wait "Submit" appear
And wait for Submit button to show
```

### Implementation Details
- **Action Type**: `wait_appear`
- **Extracted Value**: Element name/text (captured in elementName field)
- **Behavior**: Uses SmartLocator to find element, then waits for VISIBLE state
- **Timeout**: 30 seconds
- **Failure**: Returns false if element doesn't appear in time

### Example Usage
```gherkin
When Click "Process Payment"
And wait for "Processing..." to appear
And wait for "Payment Successful" to be visible
Then Verify "Order Confirmation" is displayed
```

---

## 4️⃣ Element Disappearance Waits (Invisibility)

### Pattern
```regex
^(?:wait|pause)(?:\s+for|\s+until)?\s+["']?([^"']+)["']?\s+(?:to\s+)?(?:disappear|hide|be\s+hidden|is\s+gone|vanish|not\s+visible)
```

### Supported Variations

✅ **All these work:**
```gherkin
And wait for "Loading" to disappear
And wait for "Spinner" to hide
And wait until "Processing" is gone
And wait for 'Error message' to vanish
And pause for "Loading indicator" to be hidden
And wait "Loading..." not visible
And wait for Spinner to disappear
```

### Implementation Details
- **Action Type**: `wait_disappear`
- **Extracted Value**: Element name/text (captured in elementName field)
- **Behavior**: Uses SmartLocator to find element, then waits for HIDDEN state
- **Timeout**: 30 seconds
- **Special Case**: If element is already absent, returns success immediately
- **Failure**: Returns false if element doesn't disappear in time

### Example Usage
```gherkin
When Click "Refresh Data"
And wait for "Loading spinner" to appear
And wait for "Loading spinner" to disappear
Then Verify "Updated Data" is displayed
```

---

## 🎯 Complete Example Scenarios

### Scenario 1: Multi-Step Form with Loading
```gherkin
Scenario: Complete user registration
  Given Open the browser and go to "https://example.com/register"
  When Fill the email field with "test@example.com"
  And Click "Next"
  And wait for page load
  And Fill "Full Name" with "John Doe"
  And Click "Submit"
  And wait for "Saving..." to appear
  And wait for "Saving..." to disappear
  Then Verify "Registration successful" is displayed
```

### Scenario 2: Dynamic Content Loading
```gherkin
Scenario: Load and verify dynamic content
  Given Open the browser and go to "https://example.com/dashboard"
  And wait for page to be loaded
  When Click "Load Reports"
  And wait for "Loading reports..." to appear
  And wait 2 seconds
  And wait for "Loading reports..." to disappear
  Then Verify "Sales Report" is displayed
```

### Scenario 3: Complex Workflow
```gherkin
Scenario: Process with multiple wait conditions
  Given Open the browser and go to "https://example.com/process"
  When Click "Start Process"
  And wait for "Step 1" to appear
  And Click "Continue"
  And wait for page load
  And wait for "Step 2" to be visible
  And wait 5 sec
  And Click "Finish"
  And wait until "Processing" is gone
  Then Verify "Completed" is displayed
```

---

## 🔧 Technical Implementation

### Pattern Priority (Registration Order)
1. `wait_time` - Most specific (has number)
2. `wait_disappear` - Before `wait_appear` (more specific keywords)
3. `wait_appear` - Before generic wait
4. `wait_page` - Page-specific keywords

### Code Architecture

**StepPlanner.java** - Pattern Matching
```java
addPattern("wait_time", "^(?:wait|pause)(?:\\s+for)?\\s+(\\d+)\\s*...", 1, -1, -1);
addPattern("wait_disappear", "^(?:wait|pause)(?:\\s+for|\\s+until)?...", 1, -1, -1);
addPattern("wait_appear", "^(?:wait|pause)(?:\\s+for|\\s+until)?...", 1, -1, -1);
addPattern("wait_page", "^(?:wait|pause)(?:\\s+for)?\\s+(?:page|network)...", -1, -1, -1);
```

**WaitAction.java** - Execution Logic
```java
switch (actionType) {
    case "wait_time": return handleTimeWait(plan);
    case "wait_page": return handlePageLoadWait(page);
    case "wait_appear": return handleElementAppearWait(page, locator, plan);
    case "wait_disappear": return handleElementDisappearWait(page, locator, plan);
}
```

**BrowserService.java** - Registration
```java
actionHandlers.put("wait_time", new WaitAction());
actionHandlers.put("wait_page", new WaitAction());
actionHandlers.put("wait_appear", new WaitAction());
actionHandlers.put("wait_disappear", new WaitAction());
```

---

## 🐛 Troubleshooting

### Issue: "No duration specified for time wait"
**Cause**: Number not extracted from time-based wait  
**Fix**: Ensure number is present: `wait for 5 seconds`

### Issue: "Element did not appear"
**Cause**: Element name doesn't match or timeout exceeded  
**Fix**: 
- Verify exact element text/label
- Increase timeout in WaitAction (default 30s)
- Check if element actually appears

### Issue: "Element not present (already gone)"
**Cause**: Element to wait for disappearance already absent  
**Fix**: This is actually SUCCESS - element is already gone

---

## 📊 Pattern Matching Examples

| Step Text | Matched Pattern | Action Type | Extracted Values |
|-----------|----------------|-------------|------------------|
| `wait for 20 seconds` | `wait_time` | `wait_time` | elementName: "20" |
| `wait for page load` | `wait_page` | `wait_page` | None |
| `wait for "Submit" to appear` | `wait_appear` | `wait_appear` | elementName: "Submit" |
| `wait for "Loading" to disappear` | `wait_disappear` | `wait_disappear` | elementName: "Loading" |
| `pause 5 sec` | `wait_time` | `wait_time` | elementName: "5" |

---

## ✅ Best Practices

### DO ✅
```gherkin
# Clear and specific
And wait for 3 seconds
And wait for page load
And wait for "Success" to appear
And wait for "Loading" to disappear

# Chain waits logically
When Click "Submit"
And wait for "Processing" to appear
And wait for "Processing" to disappear
Then Verify "Complete" is displayed
```

### DON'T ❌
```gherkin
# Ambiguous - will match wait_page, not wait_time
And wait for seconds

# No quotes around element name
And wait for Submit to appear  # May work but inconsistent

# Excessive fixed waits
And wait 10 seconds  # Use element-based waits instead
```

---

## 🚀 Future Enhancements

- [ ] Custom timeout configuration per step
- [ ] Conditional waits (wait while condition is true)
- [ ] Wait for element count/state changes
- [ ] Wait for text content changes
- [ ] Soft wait (try without failing)

---

<div align="center">

**Need more wait patterns?**  
File an issue or extend the patterns in `StepPlanner.java`

</div>
