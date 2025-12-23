package agent.browser.actions.alert;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
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
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(DismissAlertAction.class);
    
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
                    
                    logger.alert(type, message);
                    logger.info(" Action: DISMISSING (Cancel)");
                    logger.info("--------------------------------------------------");
                    
                    // Dismiss the dialog (click Cancel)
                    dialog.dismiss();
                    dialogHandled.set(true);
                    
                } catch (Exception e) {
                    logger.error("Error handling dialog: {}", e.getMessage());
                }
            });
            
            // Wait a moment for dialog to appear and be handled
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (dialogHandled.get()) {
                logger.success("Dialog dismissed successfully");
                return true;
            } else {
                logger.warning("No confirm dialog appeared");
                return true; // Don't fail if already handled
            }
            
        } catch (Exception e) {
            logger.error("Error in DismissAlertAction: {}", e.getMessage(), e);
            return false;
        }
    }
}
