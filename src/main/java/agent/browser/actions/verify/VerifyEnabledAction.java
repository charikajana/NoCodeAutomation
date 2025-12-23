package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyEnabledAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyEnabledAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator elEnabled = locator.waitForSmartElement(targetName, "radio", null, plan.getFrameAnchor());
        
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
                logger.section("VALIDATION SUCCESS");
                logger.info(" Element '{}' matches expected state: ENABLED", targetName);
                logger.info("--------------------------------------------------");
                return true;
            } else {
                logger.section("VALIDATION FAILED");
                logger.error(" Element '{}' is DISABLED", targetName);
                logger.info("--------------------------------------------------");
                return false;
            }
        } else {
             logger.failure("Element not found for enablement check: {}", targetName);
             return false;
        }
    }
}
