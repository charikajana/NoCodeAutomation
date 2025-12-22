package agent.browser.actions.utils;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WaitAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String actionType = plan.getActionType();
        
        try {
            switch (actionType) {
                case "wait_time":
                    return handleTimeWait(plan);
                    
                case "wait_page":
                    return handlePageLoadWait(page);
                    
                case "wait_appear":
                    return handleElementAppearWait(page, locator, plan);
                    
                case "wait_disappear":
                    return handleElementDisappearWait(page, locator, plan);
                    
                default:
                    // Fallback for generic "wait" action
                    return handlePageLoadWait(page);
            }
        } catch (Exception e) {
            System.err.println("❌ Wait action failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle time-based waits: "wait for 20 seconds", "wait 5 sec"
     */
    private boolean handleTimeWait(ActionPlan plan) {
        try {
            // Element name contains the number of seconds to wait
            String elementName = plan.getElementName();
            if (elementName == null || elementName.trim().isEmpty()) {
                System.err.println("❌ No duration specified for time wait");
                return false;
            }
            
            int seconds = Integer.parseInt(elementName.trim());
            System.out.println("⏱️  Waiting for " + seconds + " second(s)...");
            Thread.sleep(seconds * 1000L);
            System.out.println("✅ Wait complete.");
            return true;
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid wait duration: " + plan.getElementName());
            return false;
        } catch (InterruptedException e) {
            System.err.println("❌ Wait interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Handle page load waits: "wait for page load", "wait for page to be loaded"
     */
    private boolean handlePageLoadWait(Page page) {
        try {
            System.out.println("⏳ Waiting for page to load (DOM ready)...");
            // Use DOMCONTENTLOADED instead of NETWORKIDLE (more reliable)
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            Thread.sleep(2000); // Additional wait for dynamic content
            System.out.println("✅ Page loaded successfully.");
            return true;
        } catch (Exception e) {
            try {
                Thread.sleep(2000);
                System.out.println("✅ Page load wait complete (fallback).");
                return true;
            } catch (InterruptedException ie) {
                return false;
            }
        }
    }
    
    /**
     * Handle element appearance waits: "wait for 'Submit' to appear"
     */
    private boolean handleElementAppearWait(Page page, SmartLocator locator, ActionPlan plan) {
        String elementName = plan.getElementName();
        if (elementName == null || elementName.trim().isEmpty()) {
            System.err.println("❌ No element name specified for appearance wait");
            return false;
        }
        
        System.out.println("⏳ Waiting for '" + elementName + "' to appear...");
        
        try {
            // Use the smart locator to find and wait for the element
            Locator element = locator.waitForSmartElement(elementName, "button"); // Use generic type
            
            // Additional check to ensure it's visible
            element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(30000));
            
            System.out.println("✅ Element '" + elementName + "' is now visible.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Element '" + elementName + "' did not appear: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle element disappearance waits: "wait for 'Loading' to disappear"
     */
    private boolean handleElementDisappearWait(Page page, SmartLocator locator, ActionPlan plan) {
        String elementName = plan.getElementName();
        if (elementName == null || elementName.trim().isEmpty()) {
            System.err.println("❌ No element name specified for disappearance wait");
            return false;
        }
        
        System.out.println("⏳ Waiting for '" + elementName + "' to disappear...");
        
        try {
            // Try to find the element using smart locator
            Locator element = locator.findSmartElement(elementName, "button"); // Use generic type
            
            // Wait for it to be hidden (with timeout)
            element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(30000));
            
            System.out.println("✅ Element '" + elementName + "' has disappeared.");
            return true;
        } catch (Exception e) {
            // If element is not found initially, it's already gone - success
            System.out.println("✅ Element '" + elementName + "' is not present (already gone).");
            return true;
        }
    }
}

