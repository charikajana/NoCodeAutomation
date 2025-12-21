package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

public class CloseBrowserAction implements BrowserAction {
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        System.out.println("⚠️ Skipping 'Close Browser' - Browser will be closed automatically at test end");
        return true;
    }
}
