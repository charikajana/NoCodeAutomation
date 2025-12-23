package agent.browser.actions.alert;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

/**
 * Sets up a global alert handler that will accept the next dialog that appears.
 * This handler remains active until a dialog is triggered.
 * 
 * Usage: Set this action BEFORE clicking a button that will trigger an alert.
 */
public class SetupAcceptAlertAction implements BrowserAction {
    
    private static volatile boolean alertHandled = false;
    private static volatile String lastAlertMessage = "";
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            alertHandled = false;
            lastAlertMessage = "";
            
            System.out.println("🔔 Setting up alert handler (will auto-accept next dialog)");
            
            // Set up persistent dialog handler
            page.onDialog(dialog -> {
                try {
                    String message = dialog.message();
                    String type = dialog.type();
                    
                    lastAlertMessage = message;
                    
                    System.out.println("--------------------------------------------------");
                    System.out.println(" 🔔 ALERT DETECTED & AUTO-ACCEPTED");
                    System.out.println(" Type: " + type);
                    System.out.println(" Message: " + message);
                    System.out.println("--------------------------------------------------");
                    
                    dialog.accept();
                    alertHandled = true;
                    
                } catch (Exception e) {
                    System.err.println("Error in dialog handler: " + e.getMessage());
                }
            });
            
            System.out.println("✅ Alert handler ready - next dialog will be auto-accepted");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error setting up alert handler: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean wasAlertHandled() {
        return alertHandled;
    }
    
    public static String getLastAlertMessage() {
        return lastAlertMessage;
    }
}
