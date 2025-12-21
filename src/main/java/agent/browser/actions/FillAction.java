package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import agent.browser.locator.TableNavigator;

public class FillAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        String value = plan.getValue();
        Locator scope = null;

        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
             if (scope == null) {
                 System.err.println("FAILURE: Row not found for anchor: " + plan.getRowAnchor());
                 return false;
             }
        }
        
        Locator input = locator.waitForSmartElement(targetName, "input", scope);
        if (input != null) {
            try {
                input.fill(value != null ? value : "");
                System.out.println("Filled '" + value + "' into " + targetName);
                return true;
            } catch (com.microsoft.playwright.PlaywrightException e) {
                System.err.println("FAILURE: Element found for '" + targetName + "' but could not be filled (Errors: " + e.getMessage().split("\n")[0] + ")");
                return false;
            }
        } else {
            System.err.println("FAILURE: Element not found for filling: " + targetName);
            return false;
        }
    }
}
