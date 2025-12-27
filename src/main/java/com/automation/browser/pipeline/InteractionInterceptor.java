package com.automation.browser.pipeline;

import com.automation.browser.context.ContextManager;
import com.automation.planner.ActionPlan;
import com.microsoft.playwright.Page;

/**
 * Interface for intercepting browser actions at various stages of their lifecycle.
 */
public interface InteractionInterceptor {
    
    /**
     * Executed before the action handler is invoked.
     * Useful for preparing context, logging, or waiting for stability.
     */
    void beforeAction(Page page, ContextManager context, ActionPlan plan);
    
    /**
     * Executed after the action handler finishes.
     * Useful for validation, cleaning up context, or taking screenshots on failure.
     */
    void afterAction(Page page, ContextManager context, ActionPlan plan, boolean success);
}
