package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyNotTextAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String targetName = plan.getElementName();
        String textToNotSee = (value != null && !value.isEmpty()) ? value : targetName;
        
        if (textToNotSee == null) {
            System.err.println("FAILURE: Verification failed - No text specified.");
            return false;
        }

        System.out.println("Verifying text ABSENCE: " + textToNotSee);
        Locator locNot = page.getByText(textToNotSee).first();

        try {
            // Assert HIDDEN (or not visible)
            com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(locNot).isHidden();

            System.out.println("--------------------------------------------------");
            System.out.println(" ✅ VALIDATION SUCCESS (NEGATIVE)");
            System.out.println(" Expected NOT Visible: " + textToNotSee);
            System.out.println(" Actual UI: Element is hidden/absent ✓");
            System.out.println("--------------------------------------------------");
            return true;
        } catch (Error e) {
            // Capture text if it mistakenly exists
            String actualText = "";
            try { actualText = locNot.innerText().trim(); } catch (Exception ignored) {}

            System.err.println("--------------------------------------------------");
            System.err.println(" ❌ VALIDATION FAILED (NEGATIVE)");
            System.err.println(" ERROR: Element is VISIBLE when it should NOT be!");
            System.err.println(" Expected: Element '" + textToNotSee + "' should NOT be present");
            System.err.println(" Actual: Element IS VISIBLE -> \"" + actualText + "\"");
            System.err.println(" Action Required: Check your test logic or page state");
            System.err.println("--------------------------------------------------");
            return false;
        }
    }
}
