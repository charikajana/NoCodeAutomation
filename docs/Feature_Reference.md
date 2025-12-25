# NoCodeAutomation - Complete Feature Reference

**Author:** Chari - Automation Architect and Consultant

> **Quick Reference**: All supported features with example steps  
> Copy any example and adapt it to your needs!

---

## 📚 **Table of Contents**

- [Form Input Actions](#-form-input-actions)
- [Click Actions](#-click-actions)
- [Selection Actions](#-selection-actions)
- [Verification Actions](#-verification-actions)
- [Navigation Actions](#-navigation-actions)
- [Checkbox & Radio Actions](#-checkbox--radio-actions)
- [Date & Time Actions](#-date--time-actions)
- [Table Actions](#-table-actions)
- [Dropdown Actions](#-dropdown-actions)
- [Modal & Dialog Actions](#-modal--dialog-actions)
- [Frame & Window Actions](#-frame--window-actions)
- [File Upload Actions](#-file-upload-actions)
- [Hover & Mouse Actions](#-hover--mouse-actions)
- [Keyboard Actions](#-keyboard-actions)
- [Wait & Synchronization](#-wait--synchronization)
- [Scroll Actions](#-scroll-actions)
- [Screenshot & Capture](#-screenshot--capture)
- [Browser Controls](#-browser-controls)
- [Advanced Actions](#-advanced-actions)

---

## 📝 Form Input Actions

| Feature | Example Step |
|---------|--------------|
| **Enter text in field** | `When I enter "john@test.com" in Email` |
| **Type in field** | `When I type "password123" in Password` |
| **Fill field** | `When I fill Username with "johndoe"` |
| **Clear field** | `When I clear Email field` |
| **Enter number** | `When I enter "12345" in Zip Code` |
| **Enter phone** | `When I enter "+1-555-1234" in Phone Number` |
| **Enter URL** | `When I enter "https://example.com" in Website` |
| **Fill textarea** | `When I enter "Long message here" in Comments` |
| **Set field value** | `When I set Email to "test@example.com"` |

---

## 🖱️ Click Actions

| Feature | Example Step |
|---------|--------------|
| **Click button** | `When I click Submit button` |
| **Click link** | `When I click Home link` |
| **Click element** | `When I click User Profile` |
| **Click on element** | `When I click on Menu Icon` |
| **Double click** | `When I double click File Name` |
| **Right click** | `When I right click Image` |
| **Click at position** | `When I click at coordinates 100, 200` |
| **Click if exists** | `When I click Skip button if it exists` |
| **Force click** | `When I force click Hidden Button` |

---

## ✅ Selection Actions

| Feature | Example Step |
|---------|--------------|
| **Select dropdown option** | `When I select "United States" in Country` |
| **Choose from dropdown** | `When I choose "Option 1" from Dropdown` |
| **Select by value** | `When I select value "US" in Country Code` |
| **Select by index** | `When I select option 3 in Languages` |
| **Select multiple** | `When I select "Red" and "Blue" in Colors` |
| **Deselect option** | `When I deselect "Option 1" in Multi Select` |

---

## 🔍 Verification Actions

| Feature | Example Step |
|---------|--------------|
| **Verify text displayed** | `Then I should see "Welcome back"` |
| **Verify element exists** | `Then User Menu should be displayed` |
| **Verify field value** | `Then Email should contain "test@example.com"` |
| **Verify not displayed** | `Then Error Message should not be visible` |
| **Verify enabled** | `Then Submit button should be enabled` |
| **Verify disabled** | `Then Save button should be disabled` |
| **Verify checked** | `Then Terms checkbox should be checked` |
| **Verify placeholder** | `Then Email should have placeholder "Enter email"` |
| **Verify URL** | `Then page URL should contain "/dashboard"` |
| **Verify title** | `Then page title should be "Home Page"` |
| **Verify attribute** | `Then Logo should have attribute "alt" with value "Logo"` |
| **Verify color** | `Then Warning should have color "red"` |
| **Verify font** | `Then Heading should have font size "24px"` |

---

## 🌐 Navigation Actions

| Feature | Example Step |
|---------|--------------|
| **Navigate to URL** | `Given I navigate to "https://example.com"` |
| **Open URL** | `When I open "https://example.com/login"` |
| **Visit page** | `When I visit "https://example.com/about"` |
| **Go to URL** | `When I go to "https://example.com"` |
| **Browser back** | `When I go back` |
| **Browser forward** | `When I go forward` |
| **Refresh page** | `When I refresh the page` |
| **Reload page** | `When I reload` |
| **Navigate via menu** | `When I navigate to Dashboard > Settings` |
| **Click breadcrumb** | `When I click Home in breadcrumb` |

---

## ☑️ Checkbox & Radio Actions

| Feature | Example Step |
|---------|--------------|
| **Check checkbox** | `When I check Terms and Conditions` |
| **Uncheck checkbox** | `When I uncheck Newsletter Subscription` |
| **Toggle checkbox** | `When I toggle Remember Me` |
| **Select radio button** | `When I select Male in Gender` |
| **Check if checked** | `Then Remember Me checkbox should be checked` |
| **Verify unchecked** | `Then Newsletter should be unchecked` |

---

## 📅 Date & Time Actions

| Feature | Example Step |
|---------|--------------|
| **Set date** | `When I set "01/15/2025" in Start Date` |
| **Choose date** | `When I choose "December 25, 2025" in Event Date` |
| **Set today** | `When I set today in Appointment Date` |
| **Set tomorrow** | `When I set tomorrow in Delivery Date` |
| **Set date (relative)** | `When I set "30 days from today" in Due Date` |
| **Set past date** | `When I set "7 days ago" in Last Login` |
| **Set time** | `When I set "14:30" in Meeting Time` |
| **Set datetime** | `When I set "2025-01-15 14:30" in Scheduled Time` |

---

## 📊 Table Actions

| Feature | Example Step |
|---------|--------------|
| **Click in row** | `When I click Edit in row where Name is "John"` |
| **Click cell** | `When I click cell in row 3 column "Action"` |
| **Verify row exists** | `Then row where Email is "test@example.com" should exist` |
| **Verify cell value** | `Then cell in row 2 column "Status" should be "Active"` |
| **Get row count** | `Then table should have 10 rows` |
| **Select row** | `When I select row where ID is "12345"` |
| **Click header** | `When I click Name column header` |
| **Sort table** | `When I sort by Price in ascending order` |
| **Filter table** | `When I filter table by Status equals "Active"` |
| **Select checkbox in row** | `When I check checkbox in row where Name is "John"` |

---

## 📋 Dropdown Actions

| Feature | Example Step |
|---------|--------------|
| **Open dropdown** | `When I open Country dropdown` |
| **Close dropdown** | `When I close Language dropdown` |
| **Select option** | `When I select "Option 1" in Dropdown` |
| **Search in dropdown** | `When I search for "United" in Country dropdown` |
| **Multi-select** | `When I select "Red", "Blue", "Green" in Colors` |
| **Verify option exists** | `Then "United States" should be in Country dropdown` |
| **Get selected value** | `Then Country dropdown should show "USA"` |

---

## 🔔 Modal & Dialog Actions

| Feature | Example Step |
|---------|--------------|
| **Wait for modal** | `When I wait for Confirmation modal` |
| **Close modal** | `When I close popup` |
| **Click in modal** | `When I click OK in confirmation dialog` |
| **Verify modal visible** | `Then Warning modal should be displayed` |
| **Dismiss modal** | `When I dismiss alert` |
| **Accept alert** | `When I accept browser alert` |
| **Dismiss alert** | `When I dismiss browser alert` |
| **Enter in prompt** | `When I enter "John Doe" in prompt` |
| **Verify alert text** | `Then alert should say "Are you sure?"` |

---

## 🖼️ Frame & Window Actions

| Feature | Example Step |
|---------|--------------|
| **Switch to frame** | `When I switch to frame "payment-iframe"` |
| **Switch to parent** | `When I switch to parent frame` |
| **Switch to window** | `When I switch to window "New Tab"` |
| **Close window** | `When I close current window` |
| **Open new tab** | `When I open new tab` |
| **Switch to tab** | `When I switch to tab 2` |
| **Verify frame exists** | `Then frame "content-frame" should exist` |

---

## 📤 File Upload Actions

| Feature | Example Step |
|---------|--------------|
| **Upload file** | `When I upload "document.pdf" to File Input` |
| **Upload multiple** | `When I upload "file1.jpg", "file2.png" to Gallery` |
| **Upload from path** | `When I upload file from "C:/files/test.doc"` |
| **Verify file uploaded** | `Then file "document.pdf" should be uploaded` |
| **Remove uploaded file** | `When I remove uploaded file "image.jpg"` |

---

## 🖱️ Hover & Mouse Actions

| Feature | Example Step |
|---------|--------------|
| **Hover over element** | `When I hover over User Menu` |
| **Move mouse to** | `When I move mouse to Settings Icon` |
| **Drag and drop** | `When I drag Item A to Container B` |
| **Drag by offset** | `When I drag Slider 100 pixels right` |
| **Mouse down** | `When I mouse down on Draggable` |
| **Mouse up** | `When I mouse up on Drop Zone` |

---

## ⌨️ Keyboard Actions

| Feature | Example Step |
|---------|--------------|
| **Press key** | `When I press Enter` |
| **Press combination** | `When I press Ctrl+S` |
| **Type slowly** | `When I slowly type "password" in Password` |
| **Press special key** | `When I press Escape` |
| **Press Tab** | `When I press Tab` |
| **Press Backspace** | `When I press Backspace 5 times` |
| **Press arrow** | `When I press ArrowDown` |

---

## ⏱️ Wait & Synchronization

| Feature | Example Step |
|---------|--------------|
| **Wait for element** | `When I wait for Submit button` |
| **Wait for seconds** | `When I wait for 3 seconds` |
| **Wait for text** | `When I wait for "Loading complete"` |
| **Wait for visible** | `When I wait for Success Message to be visible` |
| **Wait for hidden** | `When I wait for Loading Spinner to disappear` |
| **Wait for enabled** | `When I wait for Submit button to be enabled` |
| **Wait for value** | `When I wait for Status to be "Complete"` |

---

## 📜 Scroll Actions

| Feature | Example Step |
|---------|--------------|
| **Scroll to element** | `When I scroll to Footer` |
| **Scroll to top** | `When I scroll to top of page` |
| **Scroll to bottom** | `When I scroll to bottom of page` |
| **Scroll by pixels** | `When I scroll down 500 pixels` |
| **Scroll into view** | `When I scroll Hidden Element into view` |
| **Scroll in container** | `When I scroll to bottom in Chat Container` |

---

## 📸 Screenshot & Capture

| Feature | Example Step |
|---------|--------------|
| **Take screenshot** | `When I take screenshot` |
| **Screenshot element** | `When I take screenshot of Dashboard` |
| **Save screenshot** | `When I save screenshot as "error-page.png"` |
| **Screenshot on failure** | `And capture screenshot on test failure` |

---

## 🌐 Browser Controls

| Feature | Example Step |
|---------|--------------|
| **Maximize window** | `When I maximize browser window` |
| **Set window size** | `When I set window size to 1920x1080` |
| **Get current URL** | `Then current URL should be "https://example.com"` |
| **Get page title** | `Then page title should contain "Dashboard"` |
| **Clear cookies** | `When I clear all cookies` |
| **Clear cache** | `When I clear browser cache` |
| **Set cookie** | `When I set cookie "session" with value "12345"` |

---

## 🚀 Advanced Actions

| Feature | Example Step |
|---------|--------------|
| **Execute JavaScript** | `When I execute script "window.scrollTo(0, 500)"` |
| **Get element text** | `Then get text from Price Label` |
| **Get attribute** | `Then get attribute "href" from Homepage Link` |
| **Set attribute** | `When I set attribute "disabled" to "false" on Submit` |
| **Wait for AJAX** | `When I wait for AJAX requests to complete` |
| **Wait for page load** | `When I wait for page to load` |
| **Accept cookie banner** | `When I accept cookies` |
| **Dismiss banner** | `When I dismiss notification banner` |

---

## 🎯 **Quick Pattern Reference**

### **Most Common Patterns:**

```gherkin
# Fill form fields
When I enter "[value]" in [Field Name]

# Click elements
When I click [Element Name]

# Select from dropdown
When I select "[option]" in [Dropdown Name]

# Verify content
Then I should see "[text]"

# Navigate
Given I navigate to "[URL]"

# Check/Uncheck
When I check [Checkbox Name]

# Wait for element
When I wait for [Element Name]
```

---

## 💡 **Usage Tips**

### **Tip 1: Copy & Adapt**
Find the feature you need, copy the example, and change the values:
```gherkin
Example: When I enter "john@test.com" in Email
Your use: When I enter "your-value" in Your Field Name
```

### **Tip 2: Check Support**
Not sure if your step will work?
```java
if (agent.isSupported("your step")) {
    // It works!
}
```

### **Tip 3: Get Suggestions**
Wrong syntax?
```java
List<String> help = agent.getSuggestions("your step");
// Copy the first suggestion
```

---

## 📊 **Feature Coverage**

**Total Supported Features:** 100+

**Categories:**
- ✅ Form Inputs: 9 actions
- ✅ Clicks: 9 actions
- ✅ Selections: 6 actions
- ✅ Verifications: 13 actions
- ✅ Navigation: 10 actions
- ✅ Checkboxes/Radio: 6 actions
- ✅ Date/Time: 8 actions
- ✅ Tables: 10 actions
- ✅ Dropdowns: 7 actions
- ✅ Modals/Dialogs: 9 actions
- ✅ Frames/Windows: 7 actions
- ✅ File Upload: 5 actions
- ✅ Hover/Mouse: 6 actions
- ✅ Keyboard: 7 actions
- ✅ Wait: 7 actions
- ✅ Scroll: 6 actions
- ✅ Screenshots: 4 actions
- ✅ Browser: 7 actions
- ✅ Advanced: 9 actions

**If you need something not listed here:**
1. Try similar patterns
2. Use `agent.getSuggestions("your step")`
3. Check if it's supported: `agent.isSupported("your step")`

---

## 🆘 **Need Help?**

### **Step not working?**
```java
// Get immediate suggestions
agent.getSuggestions("your step");
```

### **Want to validate before running?**
```java
// Check if supported
agent.isSupported("your step");
```

### **Still stuck?**
- Review examples above
- Try similar pattern from same category
- Use natural language variations

---

## 📞 **Support**

**Created by:** Chari - Automation Architect and Consultant

**Documentation:**
- [Quick Start Guide](./Quick_Start_Guide.md) - 3 methods to get started
- [Framework Architecture](./Framework_Architecture_Diagrams.md) - Technical details

---

**Happy Automating! 🚀**

> **Remember:** This is a reference guide. Copy any example and adapt it to your needs!
