package agent.browser.actions.verify;

import agent.browser.SmartLocator;
import agent.browser.actions.BrowserAction;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Action handler for verifying the value of an input field
 */
public class VerifyValueAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyValueAction.class);

    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String expectedValue = plan.getValue();
        String targetName = plan.getElementName();
        
        if (expectedValue == null || targetName == null) {
            logger.failure("Verification failed - Missing element name or expected value");
            return false;
        }

        logger.debug("Verifying value of '{}' is '{}'", targetName, expectedValue);
        
        // Find the element
        Locator element = locator.waitForSmartElement(targetName, "field", null, plan.getFrameAnchor());
        
        if (element == null) {
            logger.failure("Field not found for value verification: {}", targetName);
            return false;
        }

        try {
            // Get the value using JS to be safe (handles properties correctly)
            String actualValue = (String) element.evaluate("el => el.value || el.innerText || ''");
            actualValue = actualValue.trim();
            
            if (actualValue.equals(expectedValue.trim())) {
                logger.success("Verification successful: Field '{}' contains expected value '{}'", targetName, expectedValue);
                return true;
            } else {
                logger.failure("Verification failed: Field '{}' expected value '{}' but found '{}'", targetName, expectedValue, actualValue);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error verifying value for {}: {}", targetName, e.getMessage());
            return false;
        }
    }
}
