package com.automation.actions.navigation;

import com.automation.browser.actions.BrowserAction;
import com.automation.browser.locator.core.SmartLocator;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class CloseBrowserAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(CloseBrowserAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        logger.warning("Skipping 'Close Browser' - Browser will be closed automatically at test end");
        return true;
    }
}
