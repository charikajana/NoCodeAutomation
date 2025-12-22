package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Manages window/tab operations using Playwright's event-driven API
 */
public class WindowManagementAction implements BrowserAction {
    
    private static String mainWindowHandle = null;
    private static List<String> allWindowHandles = new ArrayList<>();
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String actionType = plan.getActionType();
        
        // Initialize main window on first action
        if (mainWindowHandle == null) {
            mainWindowHandle = getWindowHandle(page);
            System.out.println("📌 Main window initialized: " + mainWindowHandle);
        }
        
        switch (actionType) {
            case "switch_to_new_window":
                return switchToNewWindow(page);
            
            case "switch_to_main_window":
                return switchToMainWindow(page);
                
            case "close_current_window":
                return closeCurrentWindow(page);
                
            case "close_window":
                return closeNewWindow(page);
                
            default:
                System.err.println("Unknown window action: " + actionType);
                return false;
        }
    }
    
    /**
     * Switch to the newest/latest opened window using event listener
     */
    private boolean switchToNewWindow(Page page) {
        try {
            List<Page> pages = page.context().pages();
            
            // If already 2+ windows, just switch
            if (pages.size() >= 2) {
                Page newPage = pages.get(pages.size() - 1);
                newPage.bringToFront();
                System.out.println("✅ Switched to new window");
                System.out.println("   Current URL: " + newPage.url());
                System.out.println("   Total windows: " + pages.size());
                return true;
            }
            
            // Otherwise wait for new page event (up to 10 seconds)
            System.out.println("⏳ Waiting for new window to open...");
            CompletableFuture<Page> newPageFuture = new CompletableFuture<>();
            
            page.context().onPage(newPageFuture::complete);
            
            try {
                Page newPage = newPageFuture.get(10, TimeUnit.SECONDS);
                newPage.waitForLoadState();
                newPage.bringToFront();
                
                System.out.println("✅ Switched to new window");
                System.out.println("   Current URL: " + newPage.url());
                System.out.println("   Total windows: " + page.context().pages().size());
                return true;
            } catch (Exception e) {
                System.err.println("❌ No new window opened within 10 seconds");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error switching to new window: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Switch back to the main/first window
     */
    private boolean switchToMainWindow(Page page) {
        try {
            List<Page> pages = page.context().pages();
            
            if (pages.isEmpty()) {
                System.err.println("❌ FAILED: No windows open");
                return false;
            }
            
            // Switch to first page (main window)
            Page mainPage = pages.get(0);
            mainPage.bringToFront();
            
            System.out.println("✅ Switched to main window");
            System.out.println("   Current URL: " + mainPage.url());
            System.out.println("   Total windows: " + pages.size());
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error switching to main window: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the currently active window/tab
     */
    private boolean closeCurrentWindow(Page page) {
        try {
            List<Page> pages = page.context().pages();
            int totalBefore = pages.size();
            
            if (totalBefore == 1) {
                System.err.println("⚠️ WARNING: Cannot close the last remaining window");
                return false;
            }
            
            // Find current active page and close it
            Page currentPage = getCurrentPage(page.context());
            currentPage.close();
            
            System.out.println("✅ Closed current window");
            System.out.println("   Windows before: " + totalBefore);
            System.out.println("   Windows after: " + (totalBefore - 1));
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error closing current window: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the newest/second window using event-driven approach
     */
    private boolean closeNewWindow(Page page) {
        try {
            List<Page> pages = page.context().pages();
            
            // If already 2+ windows, close the newest
            if (pages.size() >= 2) {
                Page newPage = pages.get(pages.size() - 1);
                newPage.close();
                
                System.out.println("✅ Closed new window");
                System.out.println("   Remaining windows: " + (pages.size() - 1));
                
                // Switch back to main window
                pages.get(0).bringToFront();
                return true;
            }
            
            // Otherwise wait for new page (up to 10 seconds)
            System.out.println("⏳ Waiting for new window to open before closing...");
            CompletableFuture<Page> newPageFuture = new CompletableFuture<>();
            
            page.context().onPage(newPageFuture::complete);
            
            try {
                Page newPage = newPageFuture.get(10, TimeUnit.SECONDS);
                newPage.waitForLoadState();
                newPage.close();
                
                System.out.println("✅ Closed new window");
                System.out.println("   Remaining windows: " + page.context().pages().size());
                
                // Switch back to main
                page.context().pages().get(0).bringToFront();
                return true;
            } catch (Exception e) {
                System.err.println("❌ No new window opened within 10 seconds");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error closing new window: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current window handle
     */
    private String getWindowHandle(Page page) {
        try {
            return (String) page.evaluate("() => window.name || 'main-window'");
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * Get currently active page
     */
    private Page getCurrentPage(com.microsoft.playwright.BrowserContext context) {
        List<Page> pages = context.pages();
        for (Page p : pages) {
            try {
                if (p.url() != null) {
                    return p;
                }
            } catch (Exception ignored) {
            }
        }
        return pages.isEmpty() ? null : pages.get(pages.size() - 1);
    }
    
    /**
     * Reset window tracking (for testing)
     */
    public static void reset() {
        mainWindowHandle = null;
        allWindowHandles.clear();
    }
}
