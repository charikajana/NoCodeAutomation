package agent.browser.actions.alert;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles JavaScript prompt dialogs.
 * 
 * Supports:
 * - prompt() - Enter text and accept
 * - prompt() - Dismiss without entering text
 */
public class PromptAlertAction implements BrowserAction {
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        try {
            AtomicBoolean dialogHandled = new AtomicBoolean(false);
            String promptText = plan.getValue(); // The text to enter in prompt
            boolean shouldDismiss = "dismiss".equalsIgnoreCase(plan.getElementName());
            
            // Set up one-time dialog handler
            page.onceDialog(dialog -> {
                try {
                    String message = dialog.message();
                    String type = dialog.type();
                    String defaultValue = dialog.defaultValue();
                    
                    System.out.println("--------------------------------------------------");
                    System.out.println(" 💬 PROMPT DIALOG DETECTED");
                    System.out.println(" Type: " + type);
                    System.out.println(" Message: " + message);
                    System.out.println(" Default Value: " + defaultValue);
                    
                    if (shouldDismiss) {
                        System.out.println(" Action: DISMISSING");
                        dialog.dismiss();
                    } else {
                        System.out.println(" Action: ENTERING TEXT '" + promptText + "'");
                        dialog.accept(promptText != null ? promptText : "");
                    }
                    
                    System.out.println("--------------------------------------------------");
                    dialogHandled.set(true);
                    
                } catch (Exception e) {
                    System.err.println("Error handling prompt: " + e.getMessage());
                }
            });
            
            // Wait a moment for dialog to appear and be handled
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (dialogHandled.get()) {
                if (shouldDismiss) {
                    System.out.println("✅ Prompt dismissed successfully");
                } else {
                    System.out.println("✅ Prompt accepted with text: " + promptText);
                }
                return true;
            } else {
                System.err.println("❌ No prompt dialog appeared");
                return true; // Don't fail if already handled
            }
            
        } catch (Exception e) {
            System.err.println("Error in PromptAlertAction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
