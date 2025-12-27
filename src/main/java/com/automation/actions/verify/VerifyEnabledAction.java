package com.automation.actions.verify;

import com.automation.browser.actions.BrowserAction;
import com.automation.browser.locator.core.SmartLocator;
import com.automation.browser.locator.table.TableNavigator;
import com.automation.planner.ActionPlan;
import com.automation.planner.EnhancedActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VerifyEnabledAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyEnabledAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        Locator scope = null;

        if (plan.getRowAnchor() != null) {
            TableNavigator navigator = new TableNavigator();
            if (plan instanceof EnhancedActionPlan) {
                EnhancedActionPlan enhanced = (EnhancedActionPlan) plan;
                String columnName = enhanced.getRowConditionColumn();
                String columnValue = enhanced.getRowConditionValue();
                if (columnName != null && columnValue != null) {
                    scope = navigator.findRowByColumnValue(page, columnName, columnValue);
                } else {
                    scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
                }
            } else {
                scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
            }
            if (scope == null) {
                logger.failure("Row not found for anchor: {}", plan.getRowAnchor());
                return false;
            }
        }

        String actionType = plan.getActionType();
        boolean expectEnabled = !"verify_disabled".equals(actionType);

        Locator element = locator.waitForSmartElement(targetName, null, scope, plan.getFrameAnchor(), true);
        
        if (element != null) {
            boolean isEnabled = element.isEnabled();
            String tagName = (String) element.evaluate("el => el.tagName.toLowerCase()");
            
            // If it's a label, check the linked input
            if ("label".equals(tagName)) {
                String forAttr = (String) element.getAttribute("for");
                if (forAttr != null && !forAttr.isEmpty()) {
                     isEnabled = page.locator("#" + forAttr).isEnabled();
                } else {
                    // Try to find a nested input
                    Locator nestedInput = element.locator("input");
                    if (nestedInput.count() > 0) {
                        isEnabled = nestedInput.first().isEnabled();
                    }
                }
            }

            if (isEnabled == expectEnabled) {
                logger.section("VALIDATION SUCCESS");
                logger.info(" Element '{}' matches expected state: {}", targetName, expectEnabled ? "ENABLED" : "DISABLED");
                logger.info("--------------------------------------------------");
                return true;
            } else {
                logger.section("VALIDATION FAILED");
                logger.error(" Element '{}' state mismatch. Expected: {}, Actual: {}", 
                    targetName, 
                    expectEnabled ? "ENABLED" : "DISABLED",
                    isEnabled ? "ENABLED" : "DISABLED");
                logger.info("--------------------------------------------------");
                return false;
            }
        } else {
             logger.failure("Element not found for enablement check: {}", targetName);
             return false;
        }
    }
}
