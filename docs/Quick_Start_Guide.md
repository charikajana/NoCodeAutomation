# NoCodeAutomation - Quick Start Guide

**Author:** Chari - Automation Architect and Consultant

> **You don't need to understand how the framework works!**  
> Just use these **3 simple methods** and you're done.

---

## 🚀 **3 Methods. That's It.**

```java
agent.isSupported(step)      // Check if step works
agent.getSuggestions(step)   // Get corrections if not
agent.execute(step)          // Run the step
```

**That's literally all you need to know.** ✅

---

## 📦 **Setup (One Time Only)**

### **Step 1: Initialize the Agent**

```java
import agent.integration.SmartAutomationAgent;
import com.microsoft.playwright.Page;

public class MyStepDefinitions {
    private Page page;  // Your Playwright page
    private SmartAutomationAgent agent;
    
    @Before
    public void setup() {
        agent = new SmartAutomationAgent(page);
    }
}
```

**Done!** That's the only setup needed.

---

## 💡 **Usage Pattern (Copy & Paste)**

### **The One Pattern You Need:**

```java
@When("I do {string}")
public void doSomething(String action) {
    String step = "When I " + action;
    
    // Is it supported?
    if (agent.isSupported(step)) {
        // YES - just run it!
        agent.execute(step);
    } else {
        // NO - get suggestions and use the best one
        List<String> suggestions = agent.getSuggestions(step);
        String corrected = extractStep(suggestions.get(0));
        agent.execute(corrected);
    }
}
```

**That's it!** Use this pattern for **every step**.

---

## 📝 **Real Examples**

### **Example 1: Login**

```java
@When("user logs in")
public void userLogsIn() {
    // Just write natural language steps
    String step1 = "When I enter 'john@test.com' in Email";
    String step2 = "When I enter 'password123' in Password";
    String step3 = "When I click Login button";
    
    // Execute them
    agent.execute(step1);
    agent.execute(step2);
    agent.execute(step3);
}
```

**No selectors. No XPath. No locators. Done.**

---

### **Example 2: With Safety Check**

```java
@When("I fill form with {string}, {string}")
public void fillForm(String name, String email) {
    String step = String.format("When I enter '%s' in Name", name);
    
    if (agent.isSupported(step)) {
        agent.execute(step);  // It works!
    } else {
        // Get help
        logger.info("Need to fix: {}", step);
        logger.info("Suggestions: {}", agent.getSuggestions(step));
        throw new RuntimeException("Step needs correction");
    }
}
```

---

### **Example 3: Auto-Fix (Recommended)**

```java
@When("I perform {string}")
public void smartExecute(String stepText) {
    if (agent.isSupported(stepText)) {
        // Perfect - execute as-is
        agent.execute(stepText);
    } else {
        // Not perfect - auto-fix and execute
        logger.warn("Auto-fixing: {}", stepText);
        List<String> suggestions = agent.getSuggestions(stepText);
        
        if (!suggestions.isEmpty()) {
            String fixed = extractStep(suggestions.get(0));
            logger.info("Using: {}", fixed);
            agent.execute(fixed);
        }
    }
}

// Helper method
private String extractStep(String suggestion) {
    // "95% - When I click Submit (Standard)" → "When I click Submit"
    return suggestion.substring(suggestion.indexOf("-") + 2)
                    .split("\\(")[0].trim();
}
```

**Copy this method. Use it everywhere.**

---

## 🎯 **What You Can Do**

### **Actions:**
```java
agent.execute("When I click Submit button");
agent.execute("When I enter 'text' in Field Name");
agent.execute("When I select 'Option' in Dropdown");
agent.execute("When I check Terms checkbox");
```

### **Navigation:**
```java
agent.execute("Given I navigate to 'https://example.com'");
agent.execute("When I click Menu > Submenu");
```

### **Verification:**
```java
agent.execute("Then I should see 'Success message'");
agent.execute("And verify Email contains 'test@example.com'");
```

**Write in plain English. Let the framework figure it out.**

---

## ⚡ **Quick Reference Card**

| When to Use | Method | Example |
|-------------|--------|---------|
| **Run a step** | `execute(step)` | `agent.execute("When I click Login")` |
| **Check if valid** | `isSupported(step)` | `if (agent.isSupported(step)) { ... }` |
| **Get help** | `getSuggestions(step)` | `agent.getSuggestions("Click the button")` |

**That's ALL you need to remember!**

---

## 🔧 **Common Patterns**

### **Pattern 1: Simple Execution**
```java
agent.execute("When I click Submit");
```

### **Pattern 2: With Validation**
```java
if (agent.isSupported(step)) {
    agent.execute(step);
}
```

### **Pattern 3: With Auto-Fix**
```java
if (!agent.isSupported(step)) {
    String fixed = agent.getSuggestions(step).get(0);
    agent.execute(extractStep(fixed));
} else {
    agent.execute(step);
}
```

**Pick one pattern. Use it everywhere.**

---

## 📋 **Complete Example (Registration Form)**

```java
@When("user registers")
public void userRegisters() {
    // Just write steps in natural language
    agent.execute("When I enter 'John' in First Name");
    agent.execute("When I enter 'Doe' in Last Name");
    agent.execute("When I enter 'john@test.com' in Email");
    agent.execute("When I enter 'password123' in Password");
    agent.execute("When I select 'United States' in Country");
    agent.execute("When I check Terms and Conditions");
    agent.execute("When I click Register button");
    
    // Verify
    agent.execute("Then I should see 'Registration successful'");
}
```

**That's a complete test. No locators. No selectors. Just English.**

---

## ❓ **What If Step Doesn't Work?**

### **Option 1: Get Suggestions**
```java
List<String> help = agent.getSuggestions("Click the button");

// Output:
// ["95% - When I click button (Standard pattern)",
//  "90% - And I click button (Alternative pattern)"]
```

**Copy the suggestion. Use it.**

---

### **Option 2: Try Common Patterns**

Your step doesn't work? Try these formats:

**For Clicking:**
```
When I click [Element Name]
When I click [Element] button
And I click on [Element]
```

**For Typing:**
```
When I enter "[value]" in [Field]
And I type "[value]" in [Field]
When I fill [Field] with "[value]"
```

**For Verifying:**
```
Then I should see "[text]"
And verify "[text]" is displayed
Then "[text]" should be visible
```

**One of these WILL work.**

---

## 🎓 **Learning Tips**

### **Tip 1: Use getSuggestions() to Learn**
```java
// Don't know the right syntax?
List<String> suggestions = agent.getSuggestions("your step here");
suggestions.forEach(System.out::println);

// Copy the suggestion that looks good!
```

### **Tip 2: Check Support Before Batch Execution**
```java
String[] steps = { /*... many steps ...*/ };

for (String step : steps) {
    if (!agent.isSupported(step)) {
        System.out.println("FIX: " + step);
        System.out.println("USE: " + agent.getSuggestions(step).get(0));
    }
}
```

### **Tip 3: Build a Helper Method**
```java
public void smartExecute(String step) {
    if (agent.isSupported(step)) {
        agent.execute(step);
    } else {
        String fixed = extractStep(agent.getSuggestions(step).get(0));
        logger.info("Auto-corrected: {} → {}", step, fixed);
        agent.execute(fixed);
    }
}

// Then use it everywhere
smartExecute("any step you write");
```

---

## 🚫 **What You DON'T Need to Know**

❌ Framework architecture  
❌ Pattern registry  
❌ Locator strategies  
❌ Semantic matching  
❌ DOM scanning  
❌ Element scoring  
❌ Intelligence layer  
❌ Action handlers  

**You don't need to understand ANY of that!**

---

## ✅ **What You DO Need to Know**

1. ✅ Write steps in natural language
2. ✅ Use `agent.execute(step)`
3. ✅ If it fails, use `agent.getSuggestions(step)`
4. ✅ Copy the suggestion and try again

**That's it. Seriously.**

---

## 📊 **Decision Flow**

```
Write a natural language step
           ↓
Does agent.isSupported(step) return true?
           ↓
    YES ──────────→ agent.execute(step) → ✓ DONE
           ↓
    NO  → Get suggestions: agent.getSuggestions(step)
           ↓
           Copy first suggestion
           ↓
           agent.execute(correctedStep) → ✓ DONE
```

**Follow this flow. Every time.**

---

## 🎯 **Your Complete Toolkit**

```java
// 1. Initialize (once)
SmartAutomationAgent agent = new SmartAutomationAgent(page);

// 2. Execute steps (always)
agent.execute("When I click Login");

// 3. Check if needed (optional)
if (agent.isSupported(step)) { }

// 4. Get help if stuck (when needed)
List<String> help = agent.getSuggestions(step);
```

**4 lines. That's your entire toolkit.**

---

## 💪 **You're Ready!**

You now know **everything** you need to use NoCodeAutomation:

✅ How to initialize: `new SmartAutomationAgent(page)`  
✅ How to execute: `agent.execute("step")`  
✅ How to check: `agent.isSupported("step")`  
✅ How to fix: `agent.getSuggestions("step")`  

**Start writing tests!**

---

## 📞 **Need More Help?**

### **Still confused about syntax?**
```java
// Just run this:
agent.getSuggestions("your step here");

// It will tell you EXACTLY what to write!
```

### **Want to see more examples?**
Look at these feature files:
- `features/Login.feature`
- `features/Registration.feature`
- `features/Navigation.feature`

**Copy the syntax you see there.**

---

## 🎉 **Summary**

### **The Only 3 Things You Need:**

```java
1. agent.execute("natural language step")      // Run
2. agent.isSupported("step")                   // Check
3. agent.getSuggestions("step")                // Fix
```

**That's it. You're now a NoCodeAutomation expert!** 🚀

---

**Happy Testing!**

> **Remember:** You don't need to understand the framework.  
> You just need to write natural language and use these 3 methods.
