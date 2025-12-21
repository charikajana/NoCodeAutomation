package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ClickAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator clickable = locator.waitForSmartElement(targetName, "button");
        
        if (clickable != null) {
            try {
                String tagName = (String) clickable.evaluate("el => el.tagName.toLowerCase()");
                String type = (String) clickable.evaluate("el => el.type");

                if ("input".equals(tagName) && ("radio".equals(type) || "checkbox".equals(type))) {
                        System.out.println("Target is input[type=" + type + "], using force click.");
                        clickable.click(new Locator.ClickOptions().setForce(true));
                } else {
                        clickable.click();
                }
            } catch (Exception e) {
                System.out.println("Standard click failed, trying force click...");
                clickable.click(new Locator.ClickOptions().setForce(true));
            }
            System.out.println("Clicked " + targetName);
            return true;
        } else {
            System.err.println("FAILURE: Element not found for clicking: " + targetName);
            return false;
        }
    }
}
