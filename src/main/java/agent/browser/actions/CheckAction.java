package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator checkbox = locator.waitForSmartElement(targetName, "checkbox");
        if (checkbox != null) {
            try {
                checkbox.check();
            } catch (Exception e) {
                 checkbox.click();
            }
            System.out.println("Checked: " + targetName);
            return true;
        } else {
            System.err.println("FAILURE: Checkbox not found: " + targetName);
            return false;
        }
    }
}
