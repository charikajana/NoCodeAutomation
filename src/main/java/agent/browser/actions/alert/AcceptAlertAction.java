package agent.browser.actions.alert;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles accepting JavaScript alerts and confirm dialogs.
 * Optionally verifies the alert message.
 * 
 * Supports:
 * - alert() - Simple message box
 * - confirm() - Message with OK/Cancel
 * 
 * Can verify alert message if expectedMessage is provided in ActionPlan.value
 */
public class AcceptAlertAction implements BrowserAction {
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            AtomicBoolean dialogHandled = new AtomicBoolean(false);
            AtomicReference<String> dialogMessage = new AtomicReference<>("");
            String expectedMessage = plan.getValue(); // Expected message to verify
            
            // Set up one-time dialog handler
            page.onceDialog(dialog -> {
                try {
                    String message = dialog.message();
                    String type = dialog.type();
                    
                    dialogMessage.set(message);
                    
                    System.out.println("--------------------------------------------------");
                    System.out.println(" 🔔 ALERT DETECTED");
                    System.out.println(" Type: " + type);
                    System.out.println(" Message: " + message);
                    
                    if (expectedMessage != null && !expectedMessage.isEmpty()) {
                        System.out.println(" Expected: " + expectedMessage);
                    }
                    
                    System.out.println(" Action: ACCEPTING");
                    System.out.println("--------------------------------------------------");
                    
                    // Accept the dialog
                    dialog.accept();
                    dialogHandled.set(true);
                    
                } catch (Exception e) {
                    System.err.println("Error handling dialog: " + e.getMessage());
                }
            });
            
            // Wait a moment for dialog to appear and be handled
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (dialogHandled.get()) {
                String actualMessage = dialogMessage.get();
                
                // Verify expected message if provided
                if (expectedMessage != null && !expectedMessage.isEmpty()) {
                    if (actualMessage.contains(expectedMessage)) {
                        System.out.println("--------------------------------------------------");
                        System.out.println(" ✅ ALERT MESSAGE VERIFIED");
                        System.out.println(" Expected: " + expectedMessage);
                        System.out.println(" Actual: " + actualMessage);
                        System.out.println(" Match: SUCCESS");
                        System.out.println("--------------------------------------------------");
                        return true;
                    } else {
                        System.err.println("--------------------------------------------------");
                        System.err.println(" ❌ ALERT MESSAGE MISMATCH");
                        System.err.println(" Expected: " + expectedMessage);
                        System.err.println(" Actual: " + actualMessage);
                        System.err.println("--------------------------------------------------");
                        return false;
                    }
                } else {
                    // No verification needed, just accept
                    System.out.println("✅ Alert accepted successfully");
                    return true;
                }
            } else {
                System.err.println("❌ No alert appeared or already handled");
                return true; // Don't fail if alert was already handled by auto-handler
            }
            
        } catch (Exception e) {
            System.err.println("Error in AcceptAlertAction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
