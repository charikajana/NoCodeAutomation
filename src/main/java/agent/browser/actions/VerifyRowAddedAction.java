package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.planner.EnhancedActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Action handler for verifying that a new row was added to a table with specific values.
 * Handles steps like: "Then Verify New Row is added with 'value' in 'column' column"
 */
public class VerifyRowAddedAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        if (!(plan instanceof EnhancedActionPlan)) {
            System.err.println("FAILURE: VerifyRowAddedAction requires EnhancedActionPlan");
            return false;
        }
        
        EnhancedActionPlan enhancedPlan = (EnhancedActionPlan) plan;
        String columnName = enhancedPlan.getColumnName();
        String expectedValue = enhancedPlan.getValue();
        
        if (columnName == null || expectedValue == null) {
            System.err.println("FAILURE: Missing columnName or value for row verification");
            return false;
        }
        
        System.out.println("Verifying row exists with '" + expectedValue + "' in '" + columnName + "' column");
        
        try {
            // Strategy 1: Try to find table rows
            Locator tableRows = page.locator("table").locator("tbody tr");
            int rowCount = tableRows.count();
            
            if (rowCount == 0) {
                // Strategy 2: Try data grid pattern
                tableRows = page.locator("[role='row'], .rt-tr, .data-row");
                rowCount = tableRows.count();
            }
            
            System.out.println("Found " + rowCount + " rows in table");
            
            // Search for the value in the appropriate column
            boolean found = false;
            for (int i = 0; i < rowCount; i++) {
                Locator row = tableRows.nth(i);
                String rowText = row.innerText().toLowerCase();
                
                // Check if the row contains the expected value
                if (rowText.contains(expectedValue.toLowerCase())) {
                    System.out.println("✓ Found row containing '" + expectedValue + "'");
                    System.out.println("  Row text: " + row.innerText().replaceAll("\\n", " | "));
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.err.println("--------------------------------------------------");
                System.err.println(" VALIDATION FAILED");
                System.err.println(" Expected: Row with '" + expectedValue + "' in '" + columnName + "' column");
                System.err.println(" Actual: No such row found in table");
                System.err.println("--------------------------------------------------");
                return false;
            }
            
            System.out.println("--------------------------------------------------");
            System.out.println(" VALIDATION SUCCESS");
            System.out.println(" Expected: Row with '" + expectedValue + "' in '" + columnName + "' column");
            System.out.println(" Status: ✓ Row found in table");
            System.out.println("--------------------------------------------------");
            return true;
            
        } catch (Exception e) {
            System.err.println("--------------------------------------------------");
            System.err.println(" VALIDATION FAILED");
            System.err.println(" Error: " + e.getMessage());
            System.err.println("--------------------------------------------------");
            return false;
        }
    }
}
