package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

public class WaitAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        System.out.println("Waiting for page load...");
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        System.out.println("Page loaded.");
        return true;
    }
}
