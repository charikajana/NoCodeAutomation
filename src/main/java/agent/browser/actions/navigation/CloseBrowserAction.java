package agent.browser.actions.navigation;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class CloseBrowserAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(CloseBrowserAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        logger.warning("Skipping 'Close Browser' - Browser will be closed automatically at test end");
        return true;
    }
}
