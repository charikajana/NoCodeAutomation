package agent.browser.actions.click;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import agent.browser.locator.table.TableNavigator;

public class ClickAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(ClickAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        
        // 1. Try to use intelligent locator if already found during planning
        if (plan.hasMetadata("intelligent_locator")) {
            Locator intelligentLocator = (Locator) plan.getMetadataValue("intelligent_locator");
            if (intelligentLocator != null) {
                logger.debug("Using pre-resolved intelligent locator for: {}", targetName);
                return performClick(intelligentLocator, targetName);
            }
        }

        // 2. Handle row-based scope
        Locator scope = null;
        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             
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
                  logger.failure("Row not found for anchor: {}", plan.getRowAnchor());
                  return false;
              }
         }
        
        // 3. Find element using SmartLocator
        Locator clickable = locator.waitForSmartElement(targetName, "button", scope, plan.getFrameAnchor());
        
        if (clickable != null) {
            return performClick(clickable, targetName);
        } else {
            logger.failure("Element not found for clicking: {}", targetName);
            return false;
        }
    }

    /**
     * Internal helper to perform the click with robust fallback
     */
    private boolean performClick(Locator clickable, String targetName) {
        try {
            String tagName = (String) clickable.evaluate("el => el.tagName.toLowerCase()");
            String type = (String) clickable.evaluate("el => el.type");

            if ("input".equals(tagName) && ("radio".equals(type) || "checkbox".equals(type))) {
                logger.debug("Target is input[type={}], using force click", type);
                clickable.click(new Locator.ClickOptions().setForce(true));
            } else {
                clickable.click();
            }
            logger.browserAction("Click", targetName);
            return true;
        } catch (Exception e) {
            try {
                logger.debug("Standard click failed, trying force click: {}", e.getMessage());
                clickable.click(new Locator.ClickOptions().setForce(true));
                logger.browserAction("Force Click", targetName);
                return true;
            } catch (Exception e2) {
                logger.failure("Failed to click element: {}. Error: {}", targetName, e2.getMessage());
                return false;
            }
        }
    }
}
