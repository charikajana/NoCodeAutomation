package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import agent.browser.locator.TableNavigator;

public class ClickAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator scope = null;

        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             
             // Prefer column-based XPath if we have EnhancedActionPlan with column info
             if (plan instanceof agent.planner.EnhancedActionPlan) {
                 agent.planner.EnhancedActionPlan enhanced = (agent.planner.EnhancedActionPlan) plan;
                 String columnName = enhanced.getRowConditionColumn();
                 String columnValue = enhanced.getRowConditionValue();
                 
                 if (columnName != null && columnValue != null) {
                     scope = navigator.findRowByColumnValue(page, columnName, columnValue);
                 } else {
                     scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
                 }
             } else {
                 scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
             }
             
             if (scope == null) {
                  System.err.println("FAILURE: Row not found for anchor: " + plan.getRowAnchor());
                  return false;
              }
         }
        
        Locator clickable = locator.waitForSmartElement(targetName, "button", scope);
        
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
