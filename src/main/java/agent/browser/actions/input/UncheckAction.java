package agent.browser.actions.input;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UncheckAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(UncheckAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator uncheckbox = locator.waitForSmartElement(targetName, "checkbox", null, plan.getFrameAnchor());
        if (uncheckbox != null) {
            try {
                uncheckbox.uncheck(new Locator.UncheckOptions().setForce(true));
            } catch (Exception e) {
                logger.debug("Uncheck intercepted, trying click on label/div");
                try {
                     page.locator("label").filter(new Locator.FilterOptions().setHasText(targetName)).first().click();
                } catch (Exception ex2) {
                     uncheckbox.click();
                }
            }
            logger.browserAction("Uncheck", targetName);
            return true;
        } else {
            logger.failure("Checkbox not found: {}", targetName);
            return false;
        }
    }
}
