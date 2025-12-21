package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class FillAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        String value = plan.getValue();
        
        Locator input = locator.waitForSmartElement(targetName, "input");
        if (input != null) {
            input.fill(value != null ? value : "");
            System.out.println("Filled '" + value + "' into " + targetName);
            return true;
        } else {
            System.err.println("FAILURE: Element not found for filling: " + targetName);
            return false;
        }
    }
}
