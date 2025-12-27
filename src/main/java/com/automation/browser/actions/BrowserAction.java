package com.automation.browser.actions;

import com.automation.browser.locator.core.SmartLocator;
import com.automation.planner.ActionPlan;
import com.microsoft.playwright.Page;

public interface BrowserAction {
    boolean execute(Page page, SmartLocator locator, ActionPlan plan);
}
