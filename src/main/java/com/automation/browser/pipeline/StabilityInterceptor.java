package com.automation.browser.pipeline;

import com.automation.browser.context.ContextManager;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Ensures the browser environment is stable before an action is executed.
 */
public class StabilityInterceptor implements InteractionInterceptor {
    private static final LoggerUtil logger = LoggerUtil.getLogger(StabilityInterceptor.class);

    @Override
    public void beforeAction(Page page, ContextManager context, ActionPlan plan) {
        // Skip stability check for navigation actions as they are the source of instability
        if (plan.getActionType().equals("navigate") || plan.getActionType().equals("refresh")) {
            return;
        }

        logger.debug("StabilityInterceptor: Ensuring environment is stable for '{}'", plan.getActionType());
        
        try {
            // Wait for basic network idle to catch lazy-loading elements
            page.waitForLoadState(LoadState.NETWORKIDLE, 
                new Page.WaitForLoadStateOptions().setTimeout(2000));
        } catch (Exception e) {
            // Ignore timeouts - some pages have persistent analytics connections
            logger.debug("StabilityInterceptor: Network did not reach idle state within 2s, continuing anyway.");
        }
    }

    @Override
    public void afterAction(Page page, ContextManager context, ActionPlan plan, boolean success) {
        // Post-action cool down if needed
        if (!success) {
            logger.warning("StabilityInterceptor: Action '{}' failed. Screenshot captured (simulated).", plan.getActionType());
        }
    }
}
