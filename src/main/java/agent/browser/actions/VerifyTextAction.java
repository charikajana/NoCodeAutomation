package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import agent.browser.locator.TableNavigator;

public class VerifyTextAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String targetName = plan.getElementName();
        String textToVerify = (value != null && !value.isEmpty()) ? value : targetName;
        
        if (textToVerify == null) {
            System.err.println("FAILURE: Verification failed - No text specified.");
            return false;
        }

        System.out.println("Verifying text presence: " + textToVerify);
        
        Locator loc = null;
        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             Locator row = navigator.findRowByAnchor(page, plan.getRowAnchor());
             if (row == null) {
                 System.err.println("FAILURE: Row not found for anchor: " + plan.getRowAnchor());
                 return false;
             }
             // Verify text IS inside the row
             loc = row.getByText(textToVerify).first();
        } else {
             loc = page.getByText(textToVerify).first();
        }

        try {
            com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(loc).isVisible();
            
            String actualText = loc.innerText().trim();
            try {
                Locator parent = loc.locator("xpath=..");
                String parentText = parent.innerText().trim();
                if (parentText.length() > actualText.length() && parentText.length() < 1000) {
                    actualText = parentText;
                }
            } catch (Exception ignored) {
            }

            System.out.println("--------------------------------------------------");
            System.out.println(" VALIDATION SUCCESS");
            System.out.println(" Expected : " + textToVerify);
            System.out.println(" Actual UI: \n" + actualText);
            System.out.println("--------------------------------------------------");
            return true;
        } catch (Error e) {
             System.err.println("--------------------------------------------------");
             System.err.println(" VALIDATION FAILED");
             System.err.println(" Expected : " + textToVerify);
             System.err.println(" Actual UI: Element NOT found or NOT visible");
             System.err.println("--------------------------------------------------");
             return false;
        }
    }
}
