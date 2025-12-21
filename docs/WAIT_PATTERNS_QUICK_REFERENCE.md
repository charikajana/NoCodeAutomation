# ⏱️ Wait Patterns - Quick Reference

## 📝 All Supported Wait Variations

### ⏰ Time-Based Waits
```gherkin
And wait for 20 seconds  ✅
And wait for 20 sec      ✅
And wait 20s             ✅
And pause for 5 seconds  ✅
And pause 3 sec          ✅
```

### 📄 Page Load Waits
```gherkin
And wait for page load           ✅
And wait for page to load        ✅
And wait for page to be loaded   ✅
And wait for page load completed ✅
And wait for network idle        ✅
And wait for page ready          ✅
```

### 👁️ Element Appearance Waits
```gherkin
And wait for "Submit" to appear              ✅
And wait for "Success message" to be visible ✅
And wait until "Login button" is visible     ✅
And wait for "Processing" to be displayed    ✅
And wait for Submit button to show           ✅
```

### 🚫 Element Disappearance Waits
```gherkin
And wait for "Loading" to disappear           ✅
And wait for "Spinner" to hide                ✅
And wait until "Processing" is gone           ✅
And wait for "Error message" to vanish        ✅
And wait for "Loading indicator" to be hidden ✅
And wait "Spinner" not visible                ✅
```

---

## 🎯 Your Original Question - All Handled!

| Your Example | Handled | Action Type |
|--------------|---------|-------------|
| "wait for 20 seconds" | ✅ | `wait_time` |
| "wait for 20 sec" | ✅ | `wait_time` |
| "wait for PageLoad" | ✅ | `wait_page` |
| "wait for Page to be loaded" | ✅ | `wait_page` |
| "wait for page load completed" | ✅ | `wait_page` |

---

## 💡 Common Usage Patterns

### Pattern 1: Form Submission
```gherkin
When Fill "Email" with "test@example.com"
And Click "Submit"
And wait for page load
Then Verify "Success" is displayed
```

### Pattern 2: Loading Indicator
```gherkin
When Click "Refresh"
And wait for "Loading..." to appear
And wait for "Loading..." to disappear
Then Verify "Updated Data" is displayed
```

### Pattern 3: Slow Elements
```gherkin
When Click "Process"
And wait for "Processing" to appear
And wait 5 seconds
And wait for "Complete" to be visible
Then Verify "Success" is displayed
```

---

## ⚡ Implementation Details

**File: StepPlanner.java**
- `wait_time` - Extracts number, sleeps for N seconds
- `wait_page` - Waits for Playwright NETWORKIDLE state
- `wait_appear` - Waits for element to be VISIBLE (30s timeout)
- `wait_disappear` - Waits for element to be HIDDEN (30s timeout)

**File: WaitAction.java**
- Single class handles all 4 wait types via switch statement
- Smart element detection via SmartLocator
- Graceful handling of already-gone elements

---

See **WAIT_PATTERNS_GUIDE.md** for detailed documentation!
