# 📖 Complete Step Pattern Reference

## 🎯 **Quick Lookup: All Supported Steps**

> ✅ = Fully Implemented | 🟡 = Partially Implemented | ⬜ = Planned

---

## 1️⃣ BASIC ACTIONS (✅ Implemented)

### Navigation
```gherkin
Given Open the browser and go to "https://example.com"
When Navigate to "https://example.com"
```

### Clicks
```gherkin
When Click "Submit"
And Click on Login button
And Click the "Continue" link
```

### Fill/Type
```gherkin
When Fill the email field with "test@example.com"
And Enter "John" in Full Name
And Type "password123" into Password
```

### Checkboxes
```gherkin
When Check the "Terms and Conditions" checkbox
And Tick "Remember me"
And Uncheck "Newsletter subscription"
```

### Verification
```gherkin
Then Verify "Welcome back" is displayed
And Validate "Error: Invalid credentials" is present
Then Check "Success" message is visible
```

### Negative Verification
```gherkin
Then Verify "Error" is not displayed
And "Processing" should not be visible
```

---

## 1️⃣B WAIT COMMANDS (✅ Implemented)

### Time-Based Waits
```gherkin
And wait for 20 seconds
And wait for 20 sec
And wait 20s
And pause for 5 seconds
And pause 3 sec
```

### Page Load Waits
```gherkin
And wait for page load
And wait for page to load
And wait for page to be loaded
And wait for page load completed
And wait for network idle
And wait for page ready
```

### Element Appearance Waits
```gherkin
And wait for "Submit" to appear
And wait for "Success message" to be visible
And wait until "Login button" is visible
And wait for "Processing" to be displayed
```

### Element Disappearance Waits
```gherkin
And wait for "Loading" to disappear
And wait for "Spinner" to hide
And wait until "Processing" is gone
And wait for "Error message" to vanish
And wait for "Loading indicator" to be hidden
```


---

## 2️⃣ TABLE STRUCTURE (✅ Implemented via SmartStepParser)

### Table Existence
```gherkin
Given the "Employee" table is visible
Then "Users" table should be displayed
And the "Orders" table is present
```

### Column Validation
```gherkin
Then the "Employee" table should have headers
And the "Users" table should contain column "Email"
And "Orders" table has column "Order Date"
```

### Row Count
```gherkin
Then the "Employee" table should have at least 5 rows
And "Users" table should have exactly 10 rows
And the "Products" table should have at most 100 rows
```

---

## 3️⃣ ROW OPERATIONS (✅ Implemented)

### Row Existence
```gherkin
Then a row should exist where "Email" is "john@example.com"
And a row should not exist where "Status" is "Deleted"
And row with "Name" equals "John Doe" exists
```

### Multi-Column Row Validation
```gherkin
Then in the row where "Email" is "john@example.com", "Status" should be "Active"
And in row where "ID" is "12345", "Amount" should be "$1,000"
```

### Select Row
```gherkin
When user selects the row where "Name" is "John"
And select row with "Email" containing "admin"
```

---

## 4️⃣ CELL OPERATIONS (✅ Implemented)

### Cell Value by Position
```gherkin
Then the cell at row 2 and column "Email" should be "test@example.com"
And cell at row 5, column "Status" equals "Active"
```

### Cell Value by Row Key
```gherkin
Then the value in column "Salary" for row "John" should be "$50,000"
And value in "Department" for "john@example.com" is "Engineering"
```

### Column-Wide Validation
```gherkin
Then column "Status" should not contain value "Error"
And all values in "Active" column are "Yes"
```

---

## 5️⃣ ROW-SCOPED ACTIONS (✅ Implemented)

### Click in Row
```gherkin
When Click "Edit" in the row containing "john@example.com"
And click "Delete" in row where "Name" is "John"
And press "View Details" for row with "ID" = "12345"
```

### Fill in Row
```gherkin
When Enter "55000" in "Salary" for the row with "John"
And fill "New York" into "Location" in row where "Email" is "john@example.com"
```

### Verify in Row
```gherkin
Then Verify "Active" is displayed in the row for "john@example.com"
And "Premium" should be visible in row where "User" is "John"
```

### Checkbox Selection in Row
```gherkin
When user selects the checkbox in the row where "Name" is "John"
And check the box in row where "ID" is "12345"
And deselect checkbox for "Inactive" users
```

---

## 6️⃣ SORTING (✅ Implemented)

### Perform Sort
```gherkin
When user sorts the "Employee" table by column "Salary" in ascending order
And sort "Orders" by "Order Date" descending
And order "Products" table by "Price" (low to high)
```

### Verify Sort Order
```gherkin
Then the "Salary" column should be sorted in ascending order
And "Order Date" is sorted descending
And column "Name" is alphabetically sorted
```

---

## 7️⃣ FILTERING & SEARCH (✅ Implemented)

### Filter Table
```gherkin
When user filters the "Employee" table by "Department" with value "IT"
And filter "Orders" where "Status" = "Shipped"
And apply filter on "City" with "New York"
```

### Search
```gherkin
When user searches "john" in the "Employee" table
And search for "invoice-123" in "Orders" table
```

### Verify Filtered Results
```gherkin
Then only rows with "Department" containing "IT" should be displayed
And filtered results show "Status" = "Active"
```

---

## 8️⃣ PAGINATION (✅ Implemented)

### Navigate Pages
```gherkin
When user navigates to page 2 in the "Employee" table
And go to page 5 of "Orders"
And open next page in table
```

### Verify Pagination
```gherkin
Then the "Employee" table should display page 2
And showing page 3 of 10
And total rows across pages should be 150
```

---

## 9️⃣ BULK ACTIONS (✅ Implemented)

### Select/Deselect All
```gherkin
When user selects all rows in the "Employee" table
And deselect all rows in "Orders" table
And clear all selections
```

### Conditional Bulk Selection
```gherkin
When user selects rows where "Status" is "Pending"
And select all "Active" users
```

### Bulk Operations
```gherkin
When user performs bulk action "Delete Selected"
And execute "Export to CSV" on selected rows
```

---

## 🔟 EMPTY & ERROR STATES (✅ Implemented)

### Verify Empty Table
```gherkin
Then the "Employee" table should show empty state message
And "Orders" table displays "No records found"
And table shows "No data available"
```

### Loading States
```gherkin
Then the "Employee" table should display loading indicator
And table shows spinner
And "Processing..." is visible in table
```

---

## 🌟 ADVANCED PATTERNS (🟡 Partial / ⬜ Planned)

### Data Table Matching (⬜ Future)
```gherkin
Then the table should contain a row matching below data
  | First Name | Last Name | Email          |
  | John       | Doe       | john@test.com  |
```

### Complete Table Validation (⬜ Future)
```gherkin
Then the table should match expected data set
  | Name  | Age | Department |
  | John  | 30  | IT         |
  | Jane  | 25  | HR         |
```

### Conditional Verification (🟡 Limited)
```gherkin
Then if "Status" is "Active", "Action" button should be enabled
And when row contains "Premium", verify "Discount" is "20%"
```

### Dynamic Wait Conditions (🟡 Basic)
```gherkin
When wait for table to load
And wait until "Loading..." disappears from table
Then table should be stable (no animations)
```

---

## 🔧 SMART PARSER FEATURES

### Natural Language Variations
The parser handles multiple phrasings:

✅ **Works**: `Then the "Employee" table should have at least 5 rows`  
✅ **Works**: `Then Employee table has minimum 5 rows`  
✅ **Works**: `And table "Employee" contains >= 5 entries`

### Case Insensitivity
✅ All keywords are case-insensitive  
✅ Column/table names match fuzzy

### Optional Words
✅ `user`, `the`, `should`, `is` are optional  
✅ `Click "Submit"` = `User clicks the "Submit" button`

---

## 🎯 BEST PRACTICES

### ✅ DO
```gherkin
# Be specific
When Click "Edit" in the row containing "john@example.com"

# Use exact text matching
Then Verify "Order #12345" is displayed

# Group related assertions
Then the "Users" table should have headers
And the "Users" table should contain column "Email"
And the "Users" table should have at least 10 rows
```

### ❌ DON'T
```gherkin
# Too vague
When Click the button

# Generic row reference (which row?)
Then "Active" should be displayed

# Mixing concerns
When Click Edit and fill Name with "John" and submit
```

---

## 🐛 TROUBLESHOOTING

### "Could not parse step"
**Cause**: Step doesn't match any pattern  
**Fix**: Check spelling, use quotes for values

### "Element not found"
**Cause**: Text/label doesn't match exactly  
**Fix**: Inspect page, use exact visible text

### "Row not found for anchor"
**Cause**: Row identifier text not present  
**Fix**: Verify the anchor value exists on page

---

## 📚 PATTERN EXTENSION

### Adding Custom Patterns

In `SmartStepParser.java`:
```java
addTablePattern("custom_action",
    "^your regex pattern here with (captured groups)",
    Map.of("field1", 1, "field2", 2));
```

Then create a handler:
```java
actionHandlers.put("custom_action", new CustomActionHandler());
```

---

## 🎓 LEARNING PATH

1. **Start**: Basic actions (click, fill, verify)
2. **Next**: Row-scoped operations
3. **Then**: Table structure validation
4. **Advanced**: Sorting, filtering, pagination
5. **Expert**: Bulk actions, custom patterns

---

<div align="center">

**💡 Can't find the pattern you need?**

1. Check STEP_COVERAGE_ANALYSIS.md for supported features  
2. Propose new pattern in GitHub Issues  
3. Use LLM fallback (future feature)

</div>
