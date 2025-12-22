package agent.browser.actions.click;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DoubleClickAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        
        Locator clickable = locator.waitForSmartElement(targetName, "button", null);
        
        if (clickable != null) {
            try {
                clickable.dblclick();
                System.out.println("✅ Double-clicked " + targetName);
                return true;
            } catch (Exception e) {
                System.err.println("❌ Double-click failed: " + e.getMessage());
                return false;
            }
        } else {
            System.err.println("FAILURE: Element not found for double-clicking: " + targetName);
            return false;
        }
    }
}
