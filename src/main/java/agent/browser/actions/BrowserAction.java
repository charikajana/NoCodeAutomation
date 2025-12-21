package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

public interface BrowserAction {
    boolean execute(Page page, SmartLocator locator, ActionPlan plan);
}
