# 📊 Table Step Coverage Analysis

## ✅ **Currently Supported (v1.0)**

### **Row-Scoped Actions**
| Step Pattern | Status | Example |
|--------------|--------|---------|
| Click in row | ✅ **WORKS** | `Click "Edit" in the row containing "john@example.com"` |
| Fill in row | ✅ **WORKS** | `Enter "5000" in "Salary" for the row with "John"` |
| Verify in row | ✅ **WORKS** | `Verify "Active" is displayed in the row for "john@example.com"` |

**Coverage**: ~10% of total requirements

---

## 🟡 **Partially Supported (Needs Minor Tweaks)**

### **2. ROW LEVEL STEPS**
| Step | Support Level | Required Change |
|------|---------------|-----------------|
| `Then a row should exist where "Email" is "john@example.com"` | 🟡 80% | Add `row_exists` action type |
| `When user selects the row where "Name" is "John"` | 🟡 90% | Reuse ClickAction with row context |

### **3. CELL LEVEL VALIDATION**
| Step | Support Level | Required Change |
|------|---------------|-----------------|
| `Then in the row where "Email" is "john@example.com", "Status" should be "Active"` | 🟡 70% | Add column name extraction to regex |

**Coverage**: ~15-20% with minor regex updates

---

## ❌ **Not Currently Supported (Needs New Components)**

### **1. TABLE PRESENCE & STRUCTURE (0% Coverage)**
```gherkin
- Given the "Employee" table is visible
- Then the "Employee" table should have headers
- Then the "Employee" table should contain column "Email"
- Then the "Employee" table should have at least 5 rows
```

**Required**:
- New `TableStructureValidator` class
- `table_visible`, `table_has_column`, `table_row_count` action types

### **4. ACTIONS ON TABLE ROWS (30% Coverage)**
```gherkin
- When user selects the checkbox in the row where "Name" is "John"
- When user deselects all rows in the "Employee" table
```

**Required**:
- `CheckboxAction` enhancement to support row scope
- `BulkAction` handler for "select all"

### **5. SORTING (0% Coverage)**
```gherkin
- When user sorts the "Employee" table by column "Salary" in ascending order
- Then the "Salary" column should be sorted in ascending order
```

**Required**:
- `SortAction` handler
- `SortValidator` to verify sort order
- Column header click logic

### **6. FILTERING & SEARCH (0% Coverage)**
```gherkin
- When user filters the "Employee" table by "Department" with value "IT"
- When user searches "john" in the "Employee" table
```

**Required**:
- `FilterAction` handler
- Search box locator logic

### **7. PAGINATION (0% Coverage)**
```gherkin
- When user navigates to page 2 in the "Employee" table
- Then total rows across pages should be 50
```

**Required**:
- `PaginationAction` handler
- Multi-page data aggregation logic

### **8. BULK ACTIONS (0% Coverage)**
```gherkin
- When user selects all rows in the "Employee" table
- When user performs bulk action "Delete Selected"
```

**Required**:
- `BulkSelectionAction`
- Mass checkbox selection

### **9. EMPTY STATES (0% Coverage)**
```gherkin
- Then the "Employee" table should show "No records found"
- Then the "Employee" table should display loading indicator
```

**Required**:
- Empty state detection logic
- Loading spinner locator

### **10. ADVANCED DATA VALIDATION (0% Coverage)**
```gherkin
Then the table should contain a row matching below data
  | Column1 | Column2 | Column3 |
  | Value1  | Value2  | Value3  |
```

**Required**:
- Gherkin DataTable parser
- Row-by-row comparison logic

---

## 📈 **Coverage Summary**

| Category | Coverage | Priority |
|----------|----------|----------|
| Row-scoped click/fill/verify | ✅ 100% | - |
| Row existence checks | 🟡 20% | HIGH |
| Column/cell validation | 🟡 30% | HIGH |
| Sorting | ❌ 0% | MEDIUM |
| Filtering | ❌ 0% | MEDIUM |
| Pagination | ❌ 0% | MEDIUM |
| Bulk actions | ❌ 0% | LOW |
| Empty states | ❌ 0% | LOW |
| Advanced validation | ❌ 0% | LOW |

**Overall Coverage**: ~15-20% of requested functionality

---

## 🎯 **Recommended Implementation Strategy**

### **Phase 1: Quick Wins (Week 1)**
Extend existing patterns to cover 60% of use cases:
1. Add `row_exists` and `row_not_exists` actions
2. Support multi-column validation in rows
3. Enhance regex to capture table names

### **Phase 2: Core Table Operations (Week 2-3)**
Implement essential table interactions:
1. Sorting (click headers, validate order)
2. Filtering (find filter inputs, apply values)
3. Row count validation

### **Phase 3: Advanced Features (Week 4+)**
Build sophisticated capabilities:
1. Pagination handling
2. Bulk actions
3. DataTable comparison
4. Empty state detection

### **Phase 4: AI-Powered Fallback (Future)**
For unrecognized patterns, use LLM to:
1. Parse intent from natural language
2. Generate action plan dynamically
3. Learn from successful executions

---

## 🚀 **Smart Approach: Hybrid Strategy**

### **Option A: Rule-Based (Current Approach)**
- **Pros**: Fast, deterministic, no external dependencies
- **Cons**: Requires regex for every variation
- **Best For**: 80% of common patterns

### **Option B: LLM-Powered Parsing**
- **Pros**: Handles ANY natural language variation
- **Cons**: Slower, requires API key, non-deterministic
- **Best For**: Complex or ambiguous steps

### **RECOMMENDED: Hybrid Architecture**
```
Step Input
    ↓
┌─────────────────────────────────┐
│ 1. Try Rule-Based Parser        │ ← Fast path (80% coverage)
│    (StepPlanner with regex)     │
└──────────┬──────────────────────┘
           │ If unrecognized
           ↓
┌─────────────────────────────────┐
│ 2. Try Fuzzy/Smart Parser       │ ← Medium path (15% coverage)
│    (Similarity matching)        │
└──────────┬──────────────────────┘
           │ If still unrecognized
           ↓
┌─────────────────────────────────┐
│ 3. LLM Fallback Parser          │ ← Slow path (5% edge cases)
│    (OpenAI API / Local LLM)     │
└──────────┬──────────────────────┘
           ↓
      ActionPlan
```

This ensures:
- ✅ 99%+ step recognition
- ✅ Fast execution for common steps
- ✅ Flexibility for creative phrasing
- ✅ No hard dependencies on LLM (optional)

---

## 💡 **Next Steps**

1. **Review this analysis** with stakeholders
2. **Prioritize** which categories matter most
3. **Implement Phase 1** (quick wins)
4. **Design enhanced ActionPlan** model
5. **Create new action handlers** incrementally
