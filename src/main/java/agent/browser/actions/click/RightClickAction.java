package agent.browser.actions.click;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class RightClickAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(RightClickAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        
        Locator clickable = locator.waitForSmartElement(targetName, "button", null);
        
        if (clickable != null) {
            try {
                clickable.click(new Locator.ClickOptions().setButton(com.microsoft.playwright.options.MouseButton.RIGHT));
                logger.browserAction("Right-click", targetName);
                return true;
            } catch (Exception e) {
                logger.failure("Right-click failed: {}", e.getMessage());
                return false;
            }
        } else {
            logger.failure("Element not found for right-clicking: {}", targetName);
            return false;
        }
    }
}
