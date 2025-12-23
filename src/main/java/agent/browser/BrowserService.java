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
import agent.browser.actions.frame.*;
import agent.browser.actions.modal.*;
import agent.browser.actions.list.*;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.*;
import java.util.HashMap;
import java.util.Map;

public class BrowserService {

    private static final LoggerUtil logger = LoggerUtil.getLogger(BrowserService.class);
    
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private SmartLocator smartLocator;
    private Map<String, BrowserAction> actionHandlers;
    private String currentFrameAnchor = null;

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

        // Iframe handling
        SwitchFrameAction frameMgmt = new SwitchFrameAction();
        actionHandlers.put("switch_to_frame", frameMgmt);
        actionHandlers.put("switch_to_main_frame", frameMgmt);

        // Modal handling
        actionHandlers.put("verify_modal_visible", new VerifyModalAction());
        actionHandlers.put("verify_modal_not_visible", new VerifyModalAction());
        actionHandlers.put("close_modal", new CloseModalAction());
        
        // Multiselect list and individual selection handling
        VerifyCheckedAction checkVerify = new VerifyCheckedAction();
        actionHandlers.put("multiselect_item", new MultiselectAction());
        actionHandlers.put("verify_selected", checkVerify);
        actionHandlers.put("verify_not_selected", checkVerify);
        actionHandlers.put("verify_checked", checkVerify);
        actionHandlers.put("verify_unchecked", checkVerify);
    }

    public void startBrowser() {
        logger.info("Starting browser...");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(120000);
        page.setDefaultNavigationTimeout(120000);
        
        // Initialize SmartLocator with the page
        smartLocator = new SmartLocator(page);
        currentFrameAnchor = null;
        logger.success("Browser started successfully");
    }

    public boolean executeAction(ActionPlan plan) {
        String actionType = plan.getActionType();
        String stepText = plan.getTarget();

        // Handle frame persistence
        if ("switch_to_frame".equals(actionType)) {
            currentFrameAnchor = plan.getElementName();
        } else if ("switch_to_main_frame".equals(actionType)) {
            currentFrameAnchor = null;
        }
        
        // If plan doesn't have an explicit frame anchor but we have a persistent one, use it
        if (plan.getFrameAnchor() == null && currentFrameAnchor != null) {
            plan.setFrameAnchor(currentFrameAnchor);
        }

        BrowserAction handler = actionHandlers.get(actionType);
        if (handler != null) {
            // Always use the currently active page
            Page activePage = getActivePage();
            logger.debug("Executing action: {} for step: {}", actionType, stepText);
            boolean success = handler.execute(activePage, smartLocator, plan);
            plan.setExecuted(true);
            return success;
        } else {
            logger.error("Unknown action: {} for step: {}", actionType, stepText);
            return false;
        }
    }
    
    /**
     * Get the currently active page (handles multiple windows)
     */
    private Page getActivePage() {
        if (page != null && page.context() != null) {
            java.util.List<Page> pages = page.context().pages();
            for (int i = pages.size() - 1; i >= 0; i--) {
                Page p = pages.get(i);
                try {
                    // Simple check to see if page is still alive
                    p.url(); 
                    return p;
                } catch (Exception e) {
                    // Page likely closed
                    logger.debug("Skipping closed page: {}", i);
                }
            }
        }
        return page; // Fallback to original page
    }


    public void closeBrowser() {
        logger.info("Closing browser...");
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        logger.success("Browser closed successfully");
    }
}
