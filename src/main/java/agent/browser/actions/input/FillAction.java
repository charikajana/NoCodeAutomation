package agent.browser.actions.input;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import agent.browser.locator.table.TableNavigator;

public class FillAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(FillAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        String value = plan.getValue();

        // 1. Try to use intelligent locator if already found during planning
        if (plan.hasMetadata("intelligent_locator")) {
            Locator intelligentLocator = (Locator) plan.getMetadataValue("intelligent_locator");
            if (intelligentLocator != null) {
                logger.debug("Using pre-resolved intelligent locator for: {}", targetName);
                return performFill(intelligentLocator, targetName, value);
            }
        }

        // 2. Handle row-based scope
        Locator scope = null;
        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
             if (scope == null) {
                 logger.failure("Row not found for anchor: {}", plan.getRowAnchor());
                 return false;
             }
        }
        
        // 3. Find element using SmartLocator
        Locator input = locator.waitForSmartElement(targetName, "input", scope, plan.getFrameAnchor());
        if (input != null) {
            return performFill(input, targetName, value);
        } else {
            logger.failure("Element not found for filling: {}", targetName);
            return false;
        }
    }

    /**
     * Internal helper to perform the fill
     */
    private boolean performFill(Locator input, String targetName, String value) {
        try {
            input.fill(value != null ? value : "");
            logger.browserAction("Fill", targetName + " = '" + value + "'");
            return true;
        } catch (com.microsoft.playwright.PlaywrightException e) {
            logger.failure("Element found for '{}' but could not be filled: {}", targetName, e.getMessage().split("\n")[0]);
            return false;
        }
    }
}
