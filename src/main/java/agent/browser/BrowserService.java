package agent.browser;

import agent.planner.ActionPlan;
import agent.browser.actions.*;
import agent.browser.actions.navigation.*;
import agent.browser.actions.click.*;
import agent.browser.actions.input.*;
import agent.browser.actions.select.*;
import agent.browser.actions.verify.*;
import agent.browser.actions.table.*;
import agent.browser.actions.utils.*;
import agent.browser.actions.window.*;
import agent.browser.actions.alert.*;
import com.microsoft.playwright.*;
import java.util.HashMap;
import java.util.Map;

public class BrowserService {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private SmartLocator smartLocator;
    private Map<String, BrowserAction> actionHandlers;

    public BrowserService() {
        actionHandlers = new HashMap<>();
        actionHandlers.put("navigate", new NavigateAction());
        actionHandlers.put("fill", new FillAction());
        actionHandlers.put("click", new ClickAction());
        actionHandlers.put("double_click", new DoubleClickAction());
        actionHandlers.put("right_click", new RightClickAction());
        actionHandlers.put("select", new SelectAction());
        actionHandlers.put("deselect", new DeselectAction());
        actionHandlers.put("check", new CheckAction());
        actionHandlers.put("uncheck", new UncheckAction());
        actionHandlers.put("verify", new VerifyTextAction());
        actionHandlers.put("verify_not", new VerifyNotTextAction());
        actionHandlers.put("verify_enabled", new VerifyEnabledAction());
        actionHandlers.put("verify_disabled", new VerifyDisabledAction());
        actionHandlers.put("screenshot", new ScreenshotAction());
        
        // Wait actions - all variations
        actionHandlers.put("wait", new WaitAction());
        actionHandlers.put("wait_time", new WaitAction());
        actionHandlers.put("wait_page", new WaitAction());
        actionHandlers.put("wait_appear", new WaitAction());
        actionHandlers.put("wait_disappear", new WaitAction());
        
        // Table actions
        actionHandlers.put("row_added_with_value", new VerifyRowAddedAction());
        actionHandlers.put("get_row_values", new GetRowValuesAction());
        actionHandlers.put("click_in_row", new ClickAction());
        actionHandlers.put("click_specific_in_row", new ClickAction());
        actionHandlers.put("verify_row_not_exists", new VerifyRowNotExistsAction());
        
        // Browser lifecycle
        actionHandlers.put("close_browser", new CloseBrowserAction());
        
        // Window/Tab management
        WindowManagementAction windowMgmt = new WindowManagementAction();
        actionHandlers.put("switch_to_new_window", windowMgmt);
        actionHandlers.put("switch_to_main_window", windowMgmt);
        actionHandlers.put("close_current_window", windowMgmt);
        actionHandlers.put("close_window", windowMgmt);
        
        // Alert handling
        actionHandlers.put("accept_alert", new AcceptAlertAction());
        actionHandlers.put("dismiss_alert", new DismissAlertAction());
        actionHandlers.put("prompt_alert", new PromptAlertAction());
    }

    public void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(120000);
        page.setDefaultNavigationTimeout(120000);
        
        // Initialize SmartLocator with the page
        smartLocator = new SmartLocator(page);
    }

    public boolean executeAction(ActionPlan plan) {
        String actionType = plan.getActionType();
        String stepText = plan.getTarget();

        BrowserAction handler = actionHandlers.get(actionType);
        if (handler != null) {
            // Always use the currently active page
            Page activePage = getActivePage();
            boolean success = handler.execute(activePage, smartLocator, plan);
            plan.setExecuted(true);
            return success;
        } else {
            System.err.println("Unknown action: " + actionType + " for step: " + stepText);
            return false;
        }
    }
    
    /**
     * Get the currently active page (handles multiple windows)
     */
    private Page getActivePage() {
        if (page != null && page.context() != null) {
            java.util.List<Page> pages = page.context().pages();
            if (!pages.isEmpty()) {
                // Return the last interacted page or the last one in the list
                return pages.get(pages.size() - 1);
            }
        }
        return page; // Fallback to original page
    }


    public void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
