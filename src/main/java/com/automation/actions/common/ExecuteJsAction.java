package com.automation.actions.common;

import com.automation.browser.locator.core.SmartLocator;
import com.automation.browser.actions.BrowserAction;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class ExecuteJsAction implements BrowserAction {
    private static final LoggerUtil logger = LoggerUtil.getLogger(ExecuteJsAction.class);

    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            String script = plan.getValue();
            logger.info("Executing JavaScript: {}", script);
            page.evaluate(script);
            return true;
        } catch (Exception e) {
            logger.failure("JS execution failed: {}", e.getMessage());
            return false;
        }
    }
}
