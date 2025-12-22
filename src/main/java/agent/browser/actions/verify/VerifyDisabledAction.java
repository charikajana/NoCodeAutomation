package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyDisabledAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator elDisabled = locator.waitForSmartElement(targetName, "radio");
        
        if (elDisabled != null) {
            boolean isEnabled = elDisabled.isEnabled();
            if ("label".equals(elDisabled.evaluate("el => el.tagName.toLowerCase()"))) {
                String forAttr = (String) elDisabled.getAttribute("for");
                if (forAttr != null) {
                     isEnabled = page.locator("#" + forAttr).isEnabled();
                }
            }

            if (!isEnabled) {
                System.out.println("--------------------------------------------------");
                System.out.println(" VALIDATION SUCCESS");
                System.out.println(" Element '" + targetName + "' matches expected state: DISABLED");
                System.out.println("--------------------------------------------------");
                return true;
            } else {
                System.err.println("--------------------------------------------------");
                System.err.println(" VALIDATION FAILED");
                System.err.println(" Element '" + targetName + "' is ENABLED");
                System.err.println("--------------------------------------------------");
                return false;
            }
        } else {
             System.err.println("FAILURE: Element not found for disablement check: " + targetName);
             return false;
        }
    }
}
