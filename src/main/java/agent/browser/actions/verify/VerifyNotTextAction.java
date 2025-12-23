package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyNotTextAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyNotTextAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String targetName = plan.getElementName();
        String textToNotSee = (value != null && !value.isEmpty()) ? value : targetName;
        
        if (textToNotSee == null) {
            logger.failure("Verification failed - No text specified");
            return false;
        }

        logger.debug("Verifying text ABSENCE: {}", textToNotSee);
        Locator locNot = page.getByText(textToNotSee).first();

        try {
            // Assert HIDDEN (or not visible)
            com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(locNot).isHidden();

            logger.section("✅ VALIDATION SUCCESS (NEGATIVE)");
            logger.info(" Expected NOT Visible: {}", textToNotSee);
            logger.info(" Actual UI: Element is hidden/absent ✓");
            logger.info("--------------------------------------------------");
            return true;
        } catch (Error e) {
            // Capture text if it mistakenly exists
            String actualText = "";
            try { actualText = locNot.innerText().trim(); } catch (Exception ignored) {}

            logger.section("❌ VALIDATION FAILED (NEGATIVE)");
            logger.error(" ERROR: Element is VISIBLE when it should NOT be!");
            logger.error(" Expected: Element '{}' should NOT be present", textToNotSee);
            logger.error(" Actual: Element IS VISIBLE -> \"{}\"", actualText);
            logger.error(" Action Required: Check your test logic or page state");
            logger.info("--------------------------------------------------");
            return false;
        }
    }
}
