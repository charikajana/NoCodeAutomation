package agent.browser.actions.mouse;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Action to focus on or hover over a specific element.
 */
public class HoverAction implements BrowserAction {
    private static final LoggerUtil logger = LoggerUtil.getLogger(HoverAction.class);

    @Override
    public boolean execute(Page page, SmartLocator smartLocator, ActionPlan plan) {
        String elementName = plan.getElementName();
        logger.info("Hovering over: {}", elementName);

        Locator element = smartLocator.waitForSmartElement(elementName, null);
        if (element != null) {
            element.hover();
            logger.success("Hovered over: {}", elementName);
            return true;
        }

        logger.failure("Could not find element to hover: {}", elementName);
        return false;
    }
}
