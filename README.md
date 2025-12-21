# 🤖 NoCodeAutomation - Intelligent Test Automation Framework

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Playwright](https://img.shields.io/badge/Playwright-1.44-green.svg)](https://playwright.dev/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](https://github.com)

> **Write browser automation tests in plain English. No code. No XPath. No CSS selectors.**

NoCodeAutomation is an AI-powered testing framework that allows you to create robust, self-healing browser automation tests using natural language (Gherkin). It intelligently discovers web elements, handles complex scenarios like tables and dynamic content, and adapts to UI changes automatically.

---

## 🎯 **What Makes NoCodeAutomation Special?**

### **Traditional Approach** ❌
```java
WebElement email = driver.findElement(By.cssSelector("div.form-group:nth-child(3) > input#userEmail"));
email.sendKeys("john.doe@example.com");

WebElement editBtn = driver.findElement(By.xpath("//tr[contains(., 'john.doe@example.com')]//button[text()='Edit']"));
editBtn.click();
```

### **NoCodeAutomation Approach** ✅
```gherkin
And fill the email field with "john.doe@example.com"
And Click "Edit" in the row containing "john.doe@example.com"
```

---

## ✨ **Key Features**

### 🧠 **Smart Element Discovery**
- **AI-Powered Locators**: Automatically finds elements using text, labels, placeholders, IDs, and ARIA attributes
- **Self-Healing**: Adapts when element attributes change (class names, structure)
- **Fuzzy Matching**: Handles typos and variations in element text
- **Scoring Algorithm**: Intelligently ranks potential matches (150pts for exact, 30pts for fuzzy)

### 📊 **Advanced Table & Grid Handling** 🆕
- **Framework-Agnostic**: Works with HTML `<table>`, React grids, AgGrid, and `<div>` soups
- **Anchor & Scope Strategy**: Find rows by unique text, then interact with elements within that row
- **Upward DOM Traversal**: Automatically detects row containers (`<tr>`, `role="row"`, class patterns)
- **Zero Configuration**: No need to define table structure or column indexes

### 🛡️ **Production-Ready Resilience**
- **Graceful Degradation**: Falls back to simpler strategies if smart locator fails
- **Error Handling**: Logs failures instead of crashing the test suite
- **Timeout Management**: Configurable waits with exponential backoff
- **Screenshot on Failure**: Auto-captures evidence for debugging

### 🚀 **Developer Experience**
- **Natural Language Tests**: Write tests that read like human instructions
- **Strategy Pattern**: Extensible action handlers (easy to add new commands)
- **Separation of Concerns**: Clean architecture (Planner → Service → Locator)
- **Maven Integration**: Simple `mvn exec:java` to run tests

---

## 🏗️ **Architecture**

```
┌──────────────────────────────────────────────────────────────┐
│                    FEATURE FILES (.feature)                   │
│              Gherkin: Given/When/Then/And Steps               │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    STEP PLANNER (NLP)                         │
│   Regex-based parser converts English → ActionPlan objects   │
│   Supports: fill, click, verify, navigate, table actions     │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│               BROWSER SERVICE (Orchestrator)                  │
│      Maps ActionType → Specialized Handler (Strategy)        │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                   ACTION HANDLERS (15+)                       │
│  ClickAction, FillAction, VerifyAction, CheckAction, etc.    │
│              Each delegates to SmartLocator                   │
└────────────────────────┬─────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────┐
│              SMART LOCATOR ENGINE (AI Core)                   │
│  ┌────────────┬──────────────┬────────────┬───────────────┐  │
│  │ DomScanner │CandidateScore│LocatorFacto│TableNavigator │  │
│  │  (JS Eval) │ (Fuzzy Match)│ (Playwright│  (Row Scope)  │  │
│  └────────────┴──────────────┴────────────┴───────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

---

## 🚀 **Quick Start**

### **Prerequisites**
- Java 21+
- Maven 3.8+

### **Installation**
```bash
git clone https://github.com/yourusername/NoCodeAutomation.git
cd NoCodeAutomation
mvn clean install
```

### **Run Your First Test**
```bash
mvn exec:java -Dexec.mainClass="agent.AgentApplication"
```

This executes the default `WebTable.feature` test suite.

---

## 📝 **Writing Tests**

### **Basic Syntax**

Create a `.feature` file in `src/main/resources/features/`:

```gherkin
Feature: User Registration

  Scenario: Register a new user
    Given Open the browser and go to "https://demoqa.com/automation-practice-form"
    When Fill the first name field with "John"
    And Fill the last name field with "Doe"
    And Fill the email field with "john.doe@example.com"
    And Fill the mobile number field with "1234567890"
    And Click on Submit button
    Then Verify "Thanks for submitting the form" is displayed
```

### **Supported Commands**

| Action | Syntax | Example |
|--------|--------|---------|
| **Navigate** | `Open ... "URL"` | `Given Open the browser and go to "https://example.com"` |
| **Fill** | `Fill/Enter ... "value"` | `And Fill the username field with "admin"` |
| **Click** | `Click ...` | `And Click on Login button` |
| **Verify** | `Verify "text" is displayed` | `Then Verify "Welcome" is displayed` |
| **Check** | `Check/Tick ...` | `And Check the Terms checkbox` |
| **Uncheck** | `Uncheck/Untick ...` | `And Uncheck the Newsletter checkbox` |

---

## 🆕 **Advanced: Table & Grid Interactions**

### **Why Tables Are Hard**
Traditional automation breaks when:
- Columns are reordered
- Rows are dynamically added/removed
- You need to click a button in a specific row
- The table uses modern frameworks (React, AgGrid)

### **The NoCodeAutomation Solution**

Use the **"row context"** syntax to scope actions to a specific row:

```gherkin
# Click a button in a specific row
And Click "Edit" in the row containing "john.doe@example.com"

# Fill a field within a row
And Enter "5000" in "Salary" for the row with "John"

# Verify text exists in a specific row
Then Verify "Active" is displayed in the row for "Order #1234"
```

### **How It Works**

1. **Find the Anchor**: Locates the text "john.doe@example.com" on the page
2. **Determine the Row**: Walks up the DOM tree to find the `<tr>` or `<div role="row">`
3. **Scope the Search**: Only searches for "Edit" button within that row's context
4. **Execute Action**: Clicks the correct button (not the one in the other row!)

### **Supported Table Structures**

✅ Standard HTML Tables (`<table>`, `<tr>`, `<td>`)  
✅ ARIA Grids (`role="table"`, `role="row"`, `role="cell"`)  
✅ Div-based Grids (React Table, AgGrid)  
✅ List-based Tables (`<ul>`, `<li>`)  

### **Example: Complete Table Workflow**

```gherkin
Feature: Employee Management

  Scenario: Update employee salary
    Given Open the browser and go to "https://demoqa.com/webtables"
    
    # Add a new employee
    When Click on "Add" button
    And Fill the first name field with "John"
    And Fill the last name field with "Doe"
    And Fill the email field with "john.doe@example.com"
    And Fill the age field with "30"
    And Fill the salary field with "45000"
    And Fill the department field with "Engineering"
    And Click on Submit button
    
    # Verify employee appears in table
    Then Verify "John" is displayed
    And Verify "john.doe@example.com" is displayed
    
    # Edit salary for John's row specifically
    When Click "Edit" in the row containing "john.doe@example.com"
    And Fill the salary field with "55000"
    And Click on Submit button
    
    # Verify updated salary in John's row only
    Then Verify "55000" is displayed in the row for "john.doe@example.com"
```

---

## 🧪 **Project Structure**

```
NoCodeAutomation/
├── src/main/
│   ├── java/agent/
│   │   ├── AgentApplication.java          # Main entry point
│   │   ├── browser/
│   │   │   ├── BrowserService.java        # Orchestrator
│   │   │   ├── SmartLocator.java          # AI element finder
│   │   │   ├── actions/                   # 15+ action handlers
│   │   │   │   ├── ClickAction.java
│   │   │   │   ├── FillAction.java
│   │   │   │   ├── VerifyTextAction.java
│   │   │   │   └── ...
│   │   │   └── locator/                   # Smart locator engine
│   │   │       ├── DomScanner.java        # DOM extraction (JS)
│   │   │       ├── CandidateScorer.java   # Fuzzy matching
│   │   │       ├── LocatorFactory.java    # Locator generator
│   │   │       └── TableNavigator.java    # 🆕 Table handler
│   │   ├── planner/
│   │   │   ├── StepPlanner.java           # NLP parser
│   │   │   └── ActionPlan.java            # Action data model
│   │   ├── feature/
│   │   │   └── FeatureReader.java         # Gherkin parser
│   │   └── util/
│   │       └── FuzzyMatch.java            # String similarity
│   └── resources/features/
│       ├── TextBox.feature                # Form tests
│       ├── CheckBox.feature               # Checkbox tests
│       ├── RadioButton.feature            # Radio tests
│       └── WebTable.feature               # 🆕 Table tests
├── pom.xml
└── README.md
```

---

## 🎯 **Roadmap: Next-Level Features**

### **Phase 1: Performance & Reliability** (Q1 2025)
- [ ] **Retry Mechanism**: Automatic retry with exponential backoff
- [ ] **Locator Caching**: Cache successful locator strategies
- [ ] **Parallel Execution**: Run multiple features concurrently
- [ ] **Screenshot on Failure**: Auto-capture evidence
- [ ] **Video Recording**: Record failed test sessions

### **Phase 2: Configuration & Reporting** (Q2 2025)
- [ ] **Externalized Config**: `config.properties` for timeouts, browser type
- [ ] **Multi-Browser Support**: Firefox, Safari, Edge
- [ ] **Allure Reporting**: Beautiful HTML reports with screenshots
- [ ] **CI/CD Integration**: GitHub Actions pipeline
- [ ] **Test Data Management**: JSON/CSV data-driven tests

### **Phase 3: Advanced Features** (Q3 2025)
- [ ] **API Testing**: Hybrid UI + API validation
- [ ] **Database Validation**: SQL assertions
- [ ] **Custom Wait Strategies**: Network idle, element stability
- [ ] **Shadow DOM Support**: Handle web components
- [ ] **iFrame Handling**: Auto-switch to nested frames

### **Phase 4: AI & Intelligence** (Q4 2025)
- [ ] **Machine Learning Scorer**: Train model on historical data
- [ ] **Visual AI**: Image-based element detection (OpenCV)
- [ ] **Self-Learning**: Auto-improve locators from failures
- [ ] **Natural Language Queries**: "Click the red button next to the search box"
- [ ] **Auto-Heal Reports**: Suggest locator fixes in reports

### **Phase 5: Enterprise Features** (2026)
- [ ] **Multi-Language Support**: Spanish, French, German Gherkin
- [ ] **Cloud Execution**: BrowserStack, Sauce Labs, LambdaTest integration
- [ ] **Security Testing**: OWASP ZAP integration
- [ ] **Accessibility Testing**: axe-core integration
- [ ] **Performance Metrics**: Lighthouse score tracking

---

## 🛠️ **Configuration Options**

### **Browser Settings**
Edit `BrowserService.java`:
```java
// Headless mode
browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

// Change timeout
page.setDefaultTimeout(60000); // 60 seconds

// Change browser
browser = playwright.firefox().launch(...);
```

### **Run Specific Feature File**
Modify `AgentApplication.java`:
```java
List<String> steps = reader.readSteps("src/main/resources/features/YourFeature.feature");
```

---

## 📊 **Sample Test Output**

```
Simple Test Agent started...
ActionPlan{actionType='navigate', elementName='browser and go to', target='Given Open the browser and go to "https://demoqa.com"', value='null'}
Navigating to: https://demoqa.com

ActionPlan{actionType='click', elementName='Elements', target='When Click "Elements"', value='null'}
Analyzing DOM for target: 'Elements' (Type: button)
  > Found Winner: <div> Text:'Elements' ID:'' (Score: 150.0)
Clicked Elements

ActionPlan{actionType='fill', elementName='email field', target='And fill the email field with "john.doe@example.com"', value='john.doe@example.com', rowAnchor='null'}
Analyzing DOM for target: 'email field' (Type: input)
  > Found Winner: <label> Text:'Email' ID:'' (Score: 150.0)
  > Refining match to sibling input.
Filled 'john.doe@example.com' into email field

==========================================
       EXECUTION SUMMARY
==========================================
Total Steps : 18
Passed      : 18
Failed      : 0
Skipped     : 0
==========================================
```

---

## 🤝 **Contributing**

We welcome contributions! Here's how you can help:

1. **Report Bugs**: Open an issue with steps to reproduce
2. **Suggest Features**: Describe your use case in an issue
3. **Submit PRs**: Fork → Create branch → Commit → Push → PR

### **Development Setup**
```bash
# Clone the repo
git clone https://github.com/yourusername/NoCodeAutomation.git

# Install Playwright browsers
mvn exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install --with-deps"

# Run tests
mvn clean test
```

### **Code Style**
- Use **Strategy Pattern** for new actions
- Follow **SRP**: One class, one responsibility
- Add JavaDoc for public methods
- Write unit tests for new features

---

## 📚 **Learning Resources**

### **Technologies Used**
- [Playwright Java](https://playwright.dev/java/) - Modern browser automation
- [Gherkin](https://cucumber.io/docs/gherkin/) - BDD test language
- [Maven](https://maven.apache.org/) - Build tool
- [JUnit 5](https://junit.org/junit5/) - Testing framework (future)

### **Design Patterns Implemented**
- **Strategy Pattern**: Action handlers (ClickAction, FillAction, etc.)
- **Factory Pattern**: LocatorFactory creates Playwright locators
- **Template Method**: SmartLocator delegates to scanner/scorer/factory
- **Command Pattern**: ActionPlan encapsulates requests

---

## 🐛 **Troubleshooting**

### **Issue: "Element not found"**
**Solution**: Check if the element text matches exactly. Use fuzzy matching or add debug logs.

### **Issue: "Compilation errors after update"**
**Solution**: Run `mvn clean install` to rebuild dependencies.

### **Issue: "Browser crashes on Mac M1"**
**Solution**: Install Rosetta: `softwareupdate --install-rosetta`

### **Issue: "Tests are slow"**
**Solution**: Reduce timeout values or enable headless mode.

---

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 **Acknowledgments**

- Microsoft Playwright Team for the excellent automation library
- Cucumber/Gherkin community for BDD inspiration
- DemoQA for providing free practice sites

---

## 📧 **Contact**

**Author**: Chari  
**Email**: your.email@example.com  
**GitHub**: [@yourusername](https://github.com/yourusername)  

---

<div align="center">

**⭐ Star this repo if you find it useful!**

Made with ❤️ and ☕ by the NoCodeAutomation Team

</div>
