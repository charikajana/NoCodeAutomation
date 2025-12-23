package agent.browser.actions.utils;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class ScreenshotAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(ScreenshotAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String filename = "screenshot-" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(filename)));
        logger.info("📸 Captured screenshot: {}", filename);
        return true;
    }
}
