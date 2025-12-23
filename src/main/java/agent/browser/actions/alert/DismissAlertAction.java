package agent.browser.actions.alert;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles dismissing JavaScript confirm dialogs.
 * 
 * Supports:
 * - confirm() - Clicks Cancel button
 */
public class DismissAlertAction implements BrowserAction {
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            AtomicBoolean dialogHandled = new AtomicBoolean(false);
            AtomicReference<String> dialogMessage = new AtomicReference<>("");
            
            // Set up one-time dialog handler
            page.onceDialog(dialog -> {
                try {
                    String message = dialog.message();
                    String type = dialog.type();
                    
                    dialogMessage.set(message);
                    
                    System.out.println("--------------------------------------------------");
                    System.out.println(" 🔔 CONFIRM DIALOG DETECTED");
                    System.out.println(" Type: " + type);
                    System.out.println(" Message: " + message);
                    System.out.println(" Action: DISMISSING (Cancel)");
                    System.out.println("--------------------------------------------------");
                    
                    // Dismiss the dialog (click Cancel)
                    dialog.dismiss();
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
                System.out.println("✅ Dialog dismissed successfully");
                return true;
            } else {
                System.err.println("❌ No confirm dialog appeared");
                return true; // Don't fail if already handled
            }
            
        } catch (Exception e) {
            System.err.println("Error in DismissAlertAction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
