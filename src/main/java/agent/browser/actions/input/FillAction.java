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
        Locator scope = null;

        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
             if (scope == null) {
                 logger.failure("Row not found for anchor: {}", plan.getRowAnchor());
                 return false;
             }
        }
        
        Locator input = locator.waitForSmartElement(targetName, "input", scope, plan.getFrameAnchor());
        if (input != null) {
            try {
                input.fill(value != null ? value : "");
                logger.browserAction("Fill", targetName + " = '" + value + "'");
                return true;
            } catch (com.microsoft.playwright.PlaywrightException e) {
                logger.failure("Element found for '{}' but could not be filled: {}", targetName, e.getMessage().split("\n")[0]);
                return false;
            }
        } else {
            logger.failure("Element not found for filling: {}", targetName);
            return false;
        }
    }
}
