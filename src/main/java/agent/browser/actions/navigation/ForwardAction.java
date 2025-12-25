package agent.browser.actions.navigation;

import agent.browser.SmartLocator;
import agent.browser.actions.BrowserAction;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;

/**
 * Action handler for browser forward navigation
 */
public class ForwardAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(ForwardAction.class);

    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            logger.info("Navigating forward...");
            page.goForward();
            logger.success("Navigated forward successfully");
            return true;
        } catch (Exception e) {
            logger.failure("Failed to navigate forward: {}", e.getMessage());
            return false;
        }
    }
}
