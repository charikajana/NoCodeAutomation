package agent.browser.actions.input;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CheckAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(CheckAction.class);
    
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
            logger.browserAction("Check", targetName);
            return true;
        } else {
            logger.failure("Checkbox not found: {}", targetName);
            return false;
        }
    }
}
