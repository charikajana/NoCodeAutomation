# TestGeni - Java to C# Conversion Plan

**Author:** Chari - Automation Architect and Consultant  
**Date:** December 25, 2024  
**Objective:** Convert TestGeni Framework from Java to C#

---

## 📊 **Project Overview**

### **Total Scope:**
- **Total Classes:** 81 Java classes
- **Estimated Effort:** 25-36 hours (AI + Human)
- **Timeline:** 7-10 calendar days
- **Target:** Feature-complete C# port with .NET best practices

---

## 🎯 **PHASE 1: Core MVP - Basic Steps Working**

### **Goal:** Execute basic test steps (navigate, click, fill, verify)

### **Classes to Convert: 15**

#### **Priority 1A: Foundation (5 classes) - Day 1 Morning**

| # | Java Class | C# Class | Purpose | Complexity |
|---|------------|----------|---------|------------|
| 1 | `ActionPlan.java` | `ActionPlan.cs` | Core data structure for actions | Low |
| 2 | `StepExecutionReport.java` | `StepExecutionReport.cs` | Execution reporting | Low |
| 3 | `SmartLocator.java` | `SmartLocator.cs` | Intelligent element finding | Medium |
| 4 | `BrowserService.java` | `BrowserService.cs` | Action orchestration | Medium |
| 5 | `SmartAutomationAgent.java` | `TestGeniAgent.cs` | Public API | Low |

**Estimated Time:** 2-3 hours  
**Deliverable:** Basic framework structure compiles

---

#### **Priority 1B: Intelligence Layer (5 classes) - Day 1 Afternoon**

| # | Java Class | C# Class | Purpose | Complexity |
|---|------------|----------|---------|------------|
| 6 | `IntentAnalyzer.java` | `IntentAnalyzer.cs` | Natural language understanding | Medium |
| 7 | `StepIntent.java` | `StepIntent.cs` | Intent data structure | Low |
| 8 | `IntelligentStepProcessor.java` | `IntelligentStepProcessor.cs` | Step processing logic | Medium |
| 9 | `SmartStepParser.java` | `SmartStepParser.cs` | Step parsing | Medium |
| 10 | `PatternRegistry.java` | `PatternRegistry.cs` | Regex pattern registry | Medium |

**Estimated Time:** 2-3 hours  
**Deliverable:** Natural language parsing works

---

#### **Priority 1C: Essential Actions (5 classes) - Day 2 Morning**

| # | Java Class | C# Class | Purpose | Complexity |
|---|------------|----------|---------|------------|
| 11 | `NavigateAction.java` | `NavigateAction.cs` | Navigate to URLs | Low |
| 12 | `ClickAction.java` | `ClickAction.cs` | Click elements | Low |
| 13 | `FillAction.java` | `FillAction.cs` | Enter text in fields | Low |
| 14 | `SelectAction.java` | `SelectAction.cs` | Select from dropdown | Low |
| 15 | `VerifyTextAction.java` | `VerifyTextAction.cs` | Verify displayed text | Low |

**Estimated Time:** 1-2 hours  
**Deliverable:** **BASIC STEPS EXECUTABLE! ✅**

---

### **Phase 1 Test Scenario:**

```csharp
// After Phase 1, this should WORK:
var testGeni = new TestGeniAgent(page);

testGeni.Execute("Given I navigate to 'https://example.com'");
testGeni.Execute("When I enter 'john@test.com' in Email");
testGeni.Execute("And I click Login button");
testGeni.Execute("Then I should see 'Welcome'");

// ✅ Success!
```

**Phase 1 Total:** 15 classes | 6-8 hours | Days 1-2

---

## 🚀 **PHASE 2: Enhanced Actions - 80% Coverage**

### **Goal:** Support most common test scenarios

### **Classes to Convert: 25**

#### **Priority 2A: Form Actions (8 classes) - Day 3 Morning**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 16 | `CheckAction.java` | `CheckAction.cs` | Check checkboxes |
| 17 | `UncheckAction.java` | `UncheckAction.cs` | Uncheck checkboxes |
| 18 | `SetDateAction.java` | `SetDateAction.cs` | Handle date pickers |
| 19 | `DeselectAction.java` | `DeselectAction.cs` | Deselect from dropdown |
| 20 | `MultiselectAction.java` | `MultiselectAction.cs` | Multi-select dropdowns |
| 21 | `FillAutocompleteAction.java` | `FillAutocompleteAction.cs` | Autocomplete fields |
| 22 | `FillFormSectionAction.java` | `FillFormSectionAction.cs` | Fill entire form sections |
| 23 | `SelectRelativeDateAction.java` | `SelectRelativeDateAction.cs` | Relative dates (today, tomorrow) |

**Estimated Time:** 2-3 hours

---

#### **Priority 2B: Verification Actions (7 classes) - Day 3 Afternoon**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 24 | `VerifyValueAction.java` | `VerifyValueAction.cs` | Verify field values |
| 25 | `VerifyElementAction.java` | `VerifyElementAction.cs` | Verify element exists |
| 26 | `VerifyUrlAction.java` | `VerifyUrlAction.cs` | Verify current URL |
| 27 | `VerifyTitleAction.java` | `VerifyTitleAction.cs` | Verify page title |
| 28 | `VerifyEnabledAction.java` | `VerifyEnabledAction.cs` | Verify element enabled |
| 29 | `VerifyVisibleAction.java` | `VerifyVisibleAction.cs` | Verify element visible |
| 30 | `VerifySelectionAction.java` | `VerifySelectionAction.cs` | Verify dropdown selection |

**Estimated Time:** 2 hours

---

#### **Priority 2C: Table Actions (10 classes) - Day 4**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 31 | `DirectRowActionHandler.java` | `DirectRowActionHandler.cs` | Row-based actions |
| 32 | `ClickInRowPositionAction.java` | `ClickInRowPositionAction.cs` | Click in specific row |
| 33 | `GetRowValuesAction.java` | `GetRowValuesAction.cs` | Extract row data |
| 34 | `SelectCheckboxInRowAction.java` | `SelectCheckboxInRowAction.cs` | Select row checkbox |
| 35 | `VerifyRowAction.java` | `VerifyRowAction.cs` | Verify row exists |
| 36 | `VerifyCellAction.java` | `VerifyCellAction.cs` | Verify cell exists |
| 37 | `VerifyCellValueAction.java` | `VerifyCellValueAction.cs` | Verify cell value |
| 38 | `VerifyColumnAction.java` | `VerifyColumnAction.cs` | Verify column exists |
| 39 | `VerifyTableAction.java` | `VerifyTableAction.cs` | Verify table exists |
| 40 | `VerifyTableRowCount.java` | `VerifyTableRowCount.cs` | Verify row count |

**Estimated Time:** 3-4 hours

---

### **Phase 2 Test Scenario:**

```csharp
// Advanced scenarios work:
testGeni.Execute("When I check Remember Me");
testGeni.Execute("And I select 'United States' in Country");
testGeni.Execute("And I set '01/15/2025' in Start Date");
testGeni.Execute("And I click Edit in row where Name is 'John'");
testGeni.Execute("Then Email should contain 'test@example.com'");
testGeni.Execute("And verify table has 10 rows");

// ✅ 80% of test scenarios covered!
```

**Phase 2 Total:** 25 classes | 7-10 hours | Days 3-4

---

## ⚡ **PHASE 3: Advanced Features - 95% Coverage**

### **Goal:** Advanced interactions and self-healing capabilities

### **Classes to Convert: 20**

#### **Priority 3A: Semantic Matchers (5 classes) - Day 5 Morning**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 41 | `ClickSemanticMatcher.java` | `ClickSemanticMatcher.cs` | Intelligent click matching |
| 42 | `FillSemanticMatcher.java` | `FillSemanticMatcher.cs` | Intelligent fill matching |
| 43 | `SelectSemanticMatcher.java` | `SelectSemanticMatcher.cs` | Intelligent select matching |
| 44 | `VerifySemanticMatcher.java` | `VerifySemanticMatcher.cs` | Intelligent verify matching |
| 45 | `BaseSemanticMatcher.java` | `BaseSemanticMatcher.cs` | Base matcher class |

**Estimated Time:** 2-3 hours

---

#### **Priority 3B: Advanced Interactions (10 classes) - Day 5-6**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 46 | `DoubleClickAction.java` | `DoubleClickAction.cs` | Double-click elements |
| 47 | `RightClickAction.java` | `RightClickAction.cs` | Right-click (context menu) |
| 48 | `HoverAction.java` | `HoverAction.cs` | Hover over elements |
| 49 | `PressKeyAction.java` | `PressKeyAction.cs` | Keyboard actions |
| 50 | `ScrollAction.java` | `ScrollAction.cs` | Scroll operations |
| 51 | `SetSliderAction.java` | `SetSliderAction.cs` | Slider controls |
| 52 | `SwitchFrameAction.java` | `SwitchFrameAction.cs` | iFrame handling |
| 53 | `CloseModalAction.java` | `CloseModalAction.cs` | Close modals/dialogs |
| 54 | `VerifyModalAction.java` | `VerifyModalAction.cs` | Verify modal displayed |
| 55 | `WaitForProgressAction.java` | `WaitForProgressAction.cs` | Wait for loaders |

**Estimated Time:** 3-4 hours

---

#### **Priority 3C: Browser Management (5 classes) - Day 6**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 56 | `BackAction.java` | `BackAction.cs` | Navigate back |
| 57 | `ForwardAction.java` | `ForwardAction.cs` | Navigate forward |
| 58 | `RefreshPageAction.java` | `RefreshPageAction.cs` | Refresh page |
| 59 | `CloseBrowserAction.java` | `CloseBrowserAction.cs` | Close browser |
| 60 | `NavigateToAppAction.java` | `NavigateToAppAction.cs` | Navigate to app |

**Estimated Time:** 1-2 hours

---

**Phase 3 Total:** 20 classes | 6-9 hours | Days 5-6

---

## 🎨 **PHASE 4: Polish & Production Ready**

### **Goal:** Complete port + utilities + examples

### **Classes to Convert: 21**

#### **Priority 4A: Remaining Actions (10 classes) - Day 7 Morning**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 61 | `AcceptAlertAction.java` | `AcceptAlertAction.cs` | Accept alerts |
| 62 | `DismissAlertAction.java` | `DismissAlertAction.cs` | Dismiss alerts |
| 63 | `PromptAlertAction.java` | `PromptAlertAction.cs` | Handle prompts |
| 64 | `SetupAcceptAlertAction.java` | `SetupAcceptAlertAction.cs` | Auto-accept setup |
| 65 | `SelectMenuAction.java` | `SelectMenuAction.cs` | Menu navigation |
| 66 | `EnterStoredReferenceAction.java` | `EnterStoredReferenceAction.cs` | Use stored values |
| 67 | `FillCredentialsAction.java` | `FillCredentialsAction.cs` | Fill credentials |
| 68 | `SelectWithCriteriaAction.java` | `SelectWithCriteriaAction.cs` | Conditional select |
| 69 | `ClickInRowPositionAction.java` | `ClickInRowPositionAction.cs` | Click by position |
| 70 | `BrowserAction.java` | `IBrowserAction.cs` | Interface |

**Estimated Time:** 2-3 hours

---

#### **Priority 4B: Utilities & Helpers (6 classes) - Day 7 Afternoon**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 71 | `LoggerUtil.java` | `LoggerUtil.cs` | Logging utility |
| 72 | `MatchingHistory.java` | `MatchingHistory.cs` | Learning cache |
| 73 | `DomScanner.java` | `DomScanner.cs` | DOM scanning |
| 74 | `LocatorFactory.java` | `LocatorFactory.cs` | Locator creation |
| 75 | `ActionHandlerRegistry.java` | `ActionHandlerRegistry.cs` | Action registry |
| 76 | `StepSuggestionEngine.java` | `StepSuggestionEngine.cs` | Suggestion engine |

**Estimated Time:** 2-3 hours

---

#### **Priority 4C: Examples & Integration (5 classes) - Day 7 Evening**

| # | Java Class | C# Class | Purpose |
|---|------------|----------|---------|
| 77 | `HybridStepDefinitions.java` | `HybridStepDefinitions.cs` | SpecFlow examples |
| 78 | `AdvancedValidationStepDefs.java` | `AdvancedValidationStepDefs.cs` | Advanced examples |
| 79 | `SmartDecisionStepDefs.java` | `SmartDecisionStepDefs.cs` | Decision examples |
| 80 | `SuggestionExamples.java` | `SuggestionExamples.cs` | Suggestion examples |
| 81 | `AgentApplication.java` | `Program.cs` | Entry point |

**Estimated Time:** 2-3 hours

---

**Phase 4 Total:** 21 classes | 6-9 hours | Day 7

---

## 📅 **Complete Timeline Summary**

| Phase | Classes | Hours | Days | Milestone |
|-------|---------|-------|------|-----------|
| **Phase 1** | 15 | 6-8 | 1-2 | ✅ Execute basic steps |
| **Phase 2** | 25 | 7-10 | 3-4 | ✅ 80% scenarios work |
| **Phase 3** | 20 | 6-9 | 5-6 | ✅ 95% coverage |
| **Phase 4** | 21 | 6-9 | 7 | ✅ Production ready |
| **TOTAL** | **81** | **25-36** | **7-10** | ✅ Complete C# port |

---

## 🎯 **Recommended Strategy**

### **Option 1: MVP First (Recommended)**
```
Week 1: Phase 1 only (15 classes)
→ Get working code in 2 days
→ Test, validate, gather feedback
→ Decide on Phase 2-4 based on results

Advantage: Quick validation, low risk
```

### **Option 2: Aggressive Full Port**
```
Days 1-2: Phase 1 (15 classes) - Basic steps
Days 3-4: Phase 2 (25 classes) - Enhanced
Days 5-6: Phase 3 (20 classes) - Advanced
Day 7: Phase 4 (21 classes) - Polish

Advantage: Complete in 1 week
```

### **Option 3: Balanced Approach**
```
Week 1: Phase 1 + 2 (40 classes) - Core functionality
Week 2: Phase 3 + 4 (41 classes) - Advanced + Polish

Advantage: Thorough testing between phases
```

---

## 🛠️ **Required Tools & Setup**

### **Development Environment:**
- ✅ Visual Studio 2022 or VS Code
- ✅ .NET 8.0 SDK
- ✅ NuGet Package Manager

### **NuGet Packages:**
```xml
<PackageReference Include="Microsoft.Playwright" Version="1.40.0" />
<PackageReference Include="Newtonsoft.Json" Version="13.0.3" />
<PackageReference Include="SpecFlow" Version="3.9.74" />
<PackageReference Include="Microsoft.Extensions.Logging" Version="8.0.0" />
```

### **Project Structure:**
```
TestGeni.NET/
├── TestGeni.Core/              (Core framework)
├── TestGeni.Browser/           (Browser actions)
├── TestGeni.Intelligence/      (NLP & Intelligence)
├── TestGeni.SpecFlow/          (BDD integration)
├── TestGeni.Examples/          (Sample projects)
└── TestGeni.Tests/             (Unit tests)
```

---

## 📝 **Conversion Guidelines**

### **Java → C# Mappings:**

| Java | C# |
|------|-----|
| `public class SmartLocator` | `public class SmartLocator` |
| `private Page page;` | `private readonly IPage _page;` |
| `public void execute()` | `public void Execute()` |
| `List<String>` | `List<string>` |
| `HashMap<K,V>` | `Dictionary<K,V>` |
| `Pattern.compile()` | `new Regex()` |
| `.stream().filter()` | `.Where()` (LINQ) |
| `@Getter/@Setter` | `{ get; set; }` |

### **Naming Conventions:**
- **Methods:** PascalCase (`Execute`, `IsSupported`)
- **Properties:** PascalCase (`Name`, `Value`)
- **Fields:** `_camelCase` with underscore (`_page`, `_locator`)
- **Interfaces:** `IInterfaceName` (`IPage`, `ILocator`)
- **Async:** `MethodNameAsync` (`ExecuteAsync`)

---

## ✅ **Quality Checklist Per Class**

For each converted class, ensure:

- [ ] Follows C# naming conventions
- [ ] Uses C# idioms (LINQ, properties, etc.)
- [ ] Proper async/await where needed
- [ ] XML documentation comments
- [ ] Nullable reference types handled
- [ ] Dependency injection ready
- [ ] Unit tests (if applicable)
- [ ] Compiles without warnings

---

## 📊 **Success Metrics**

### **Phase 1 Success:**
```csharp
// This test passes:
testGeni.Execute("Given I navigate to 'https://example.com'");
testGeni.Execute("When I click Login button");
// ✅ No errors
```

### **Phase 2 Success:**
```csharp
// Complete login flow works:
testGeni.Execute("When I enter 'user@test.com' in Email");
testGeni.Execute("And I check Remember Me");
testGeni.Execute("And I select 'English' in Language");
// ✅ All execute successfully
```

### **Final Success:**
```csharp
// All features work:
bool supported = testGeni.IsSupported("When I click Login");
var suggestions = testGeni.GetSuggestions("Click the button");
testGeni.Execute("Complex natural language step");
// ✅ Complete feature parity with Java
```

---

## 🚀 **Next Steps**

### **Tomorrow (Day 1):**

1. ✅ Create C# solution structure
2. ✅ Install NuGet packages
3. ✅ Convert Priority 1A (5 classes) - Morning
4. ✅ Convert Priority 1B (5 classes) - Afternoon
5. ✅ Test & validate foundation

### **Day 2:**
1. ✅ Convert Priority 1C (5 actions)
2. ✅ Create basic test scenario
3. ✅ **Execute first working test!**
4. ✅ Review & decide on Phase 2

---

## 📞 **Support & Contact**

**Created by:** Chari - Automation Architect and Consultant

**Questions?** Review this plan and we'll start conversion tomorrow!

---

**Ready to build TestGeni for .NET! 🚀**
