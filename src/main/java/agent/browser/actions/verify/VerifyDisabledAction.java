package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyDisabledAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyDisabledAction.class);
    
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
                logger.section("VALIDATION SUCCESS");
                logger.info(" Element '{}' matches expected state: DISABLED", targetName);
                logger.info("--------------------------------------------------");
                return true;
            } else {
                logger.section("VALIDATION FAILED");
                logger.error(" Element '{}' is ENABLED", targetName);
                logger.info("--------------------------------------------------");
                return false;
            }
        } else {
             logger.failure("Element not found for disablement check: {}", targetName);
             return false;
        }
    }
}
