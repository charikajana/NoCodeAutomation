package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyEnabledAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator elEnabled = locator.waitForSmartElement(targetName, "radio");
        
        if (elEnabled != null) {
            boolean isEnabled = elEnabled.isEnabled();
            // If it's a label, check the linked input
            if ("label".equals(elEnabled.evaluate("el => el.tagName.toLowerCase()"))) {
                String forAttr = (String) elEnabled.getAttribute("for");
                if (forAttr != null) {
                     isEnabled = page.locator("#" + forAttr).isEnabled();
                }
            }

            if (isEnabled) {
                System.out.println("--------------------------------------------------");
                System.out.println(" VALIDATION SUCCESS");
                System.out.println(" Element '" + targetName + "' matches expected state: ENABLED");
                System.out.println("--------------------------------------------------");
                return true;
            } else {
                System.err.println("--------------------------------------------------");
                System.err.println(" VALIDATION FAILED");
                System.err.println(" Element '" + targetName + "' is DISABLED");
                System.err.println("--------------------------------------------------");
                return false;
            }
        } else {
             System.err.println("FAILURE: Element not found for enablement check: " + targetName);
             return false;
        }
    }
}
