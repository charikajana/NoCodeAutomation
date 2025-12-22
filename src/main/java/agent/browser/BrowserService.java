package agent.browser;

import agent.planner.ActionPlan;
import agent.browser.actions.*;
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
            boolean success = handler.execute(page, smartLocator, plan);
            plan.setExecuted(true);
            return success;
        } else {
             System.out.println("Unknown action: " + stepText);
             plan.setExecuted(true);
             return true; 
        }
    }

    public void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
