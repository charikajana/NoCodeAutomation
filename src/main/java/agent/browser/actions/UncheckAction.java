package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UncheckAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator uncheckbox = locator.waitForSmartElement(targetName, "checkbox");
        if (uncheckbox != null) {
            try {
                uncheckbox.uncheck(new Locator.UncheckOptions().setForce(true));
            } catch (Exception e) {
                System.out.println("Uncheck intercepted, trying click on label/div...");
                try {
                     page.locator("label").filter(new Locator.FilterOptions().setHasText(targetName)).first().click();
                } catch (Exception ex2) {
                     uncheckbox.click();
                }
            }
            System.out.println("Unchecked: " + targetName);
            return true;
        } else {
            System.err.println("FAILURE: Checkbox not found: " + targetName);
            return false;
        }
    }
}
