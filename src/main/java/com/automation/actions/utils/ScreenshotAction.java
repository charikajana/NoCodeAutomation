package com.automation.actions.utils;

import com.automation.browser.actions.BrowserAction;
import com.automation.browser.locator.core.SmartLocator;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class ScreenshotAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(ScreenshotAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String filename = "screenshot-" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(filename)));
        logger.info("Captured screenshot: {}", filename);
        return true;
    }
}
