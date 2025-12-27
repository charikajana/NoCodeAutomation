package com.automation.browser.pipeline;

import com.automation.browser.context.ContextManager;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;

/**
 * Automatically manages the context stack based on the ActionPlan.
 * If a plan specifies a frameAnchor, it pushes it before action and pops it after.
 */
public class ContextInterceptor implements InteractionInterceptor {
    private static final LoggerUtil logger = LoggerUtil.getLogger(ContextInterceptor.class);

    @Override
    public void beforeAction(Page page, ContextManager context, ActionPlan plan) {
        String actionType = plan.getActionType();
        
        // 1. Handle Persistent Switches
        if ("switch_to_frame".equals(actionType)) {
            String frameName = plan.getElementName() != null ? plan.getElementName() : plan.getValue();
            if (frameName != null) {
                logger.debug("ContextInterceptor: Persistent PUSH Frame '{}'", frameName);
                context.pushFrame(frameName);
            }
        } else if ("switch_to_main_frame".equals(actionType)) {
            logger.debug("ContextInterceptor: RESET to main frame");
            context.reset();
        }
        
        // 2. Handle Temporary Scoping (for steps like "In iframe 'f1', Verify 'x' is displayed")
        // We only push if it's NOT the switch action itself (to avoid double pushing)
        if (plan.getFrameAnchor() != null && !"switch_to_frame".equals(actionType)) {
            logger.debug("ContextInterceptor: Temporary PUSH Frame '{}'", plan.getFrameAnchor());
            context.pushFrame(plan.getFrameAnchor());
        }
    }

    @Override
    public void afterAction(Page page, ContextManager context, ActionPlan plan, boolean success) {
        // Only pop if it was a temporary scoping anchor
        if (plan.getFrameAnchor() != null && !"switch_to_frame".equals(plan.getActionType())) {
            logger.debug("ContextInterceptor: Action finished. Popping temporary frame context.");
            context.pop();
        }
    }
}
