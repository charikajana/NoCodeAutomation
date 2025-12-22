package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.browser.locator.DynamicTableXPathBuilder;
import agent.planner.ActionPlan;
import agent.planner.EnhancedActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyRowNotExistsAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        if (!(plan instanceof EnhancedActionPlan)) {
            System.err.println("FAILURE: VerifyRowNotExistsAction requires EnhancedActionPlan");
            return false;
        }
        
        EnhancedActionPlan enhancedPlan = (EnhancedActionPlan) plan;
        String columnName = enhancedPlan.getRowConditionColumn();
        String columnValue = enhancedPlan.getRowConditionValue();
        
        if (columnName == null || columnValue == null) {
            System.err.println("FAILURE: Column name and value required for row validation");
            return false;
        }
        
        System.out.println("🔍 Verifying row does NOT exist where '" + columnName + "' = '" + columnValue + "'");
        
        // Use XPath builder to check if row exists
        DynamicTableXPathBuilder builder = new DynamicTableXPathBuilder(page);
        String xpath = builder.buildRowXPath(columnName, columnValue);
        
        if (xpath == null) {
            System.err.println("FAILURE: Could not build XPath for row validation");
            return false;
        }
        
        Locator row = page.locator(xpath);
        int rowCount = row.count();
        
        if (rowCount == 0) {
            // Row does NOT exist - SUCCESS!
            System.out.println("--------------------------------------------------");
            System.out.println(" ✅ VALIDATION SUCCESS");
            System.out.println(" Expected: Row with '" + columnName + "' = '" + columnValue + "' should NOT exist");
            System.out.println(" Actual  : Row NOT found (deleted successfully)");
            System.out.println("--------------------------------------------------");
            return true;
        } else {
            // Row still exists - FAILURE!
            String rowText = row.first().innerText().replaceAll("\\n", " | ");
            System.out.println("--------------------------------------------------");
            System.out.println(" ❌ VALIDATION FAILED");
            System.out.println(" Expected: Row with '" + columnName + "' = '" + columnValue + "' should NOT exist");
            System.out.println(" Actual  : Row STILL EXISTS");
            System.out.println(" Row Content: " + rowText);
            System.out.println("--------------------------------------------------");
            return false;
        }
    }
}
