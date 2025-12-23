package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Verifies if an element (checkbox, radio) is checked/selected or not.
 */
public class VerifyCheckedAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyCheckedAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String targetName = plan.getElementName();
        // If element name is null, check plan.getValue() as a fallback for some legacy patterns
        if (targetName == null || targetName.isEmpty()) {
            targetName = plan.getValue();
        }
        
        String actionType = plan.getActionType();
        boolean expectChecked = !"verify_unchecked".equals(actionType) && !"verify_not_selected".equals(actionType);
        
        Locator scope = null;
        if (plan.getRowAnchor() != null) {
            agent.browser.locator.table.TableNavigator navigator = new agent.browser.locator.table.TableNavigator();
            if (plan instanceof agent.planner.EnhancedActionPlan) {
                agent.planner.EnhancedActionPlan enhanced = (agent.planner.EnhancedActionPlan) plan;
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

        java.util.List<String> items = new java.util.ArrayList<>();
        if (targetName != null && targetName.contains(";")) {
            items.addAll(java.util.Arrays.asList(targetName.split(";")));
        } else {
            items.add(targetName);
        }

        boolean allMatched = true;
        for (String itemText : items) {
            String target = itemText.trim();
            if (target.isEmpty()) continue;

            Locator element = locator.waitForSmartElement(target, null, scope, plan.getFrameAnchor());
            
            if (element != null) {
                // Small wait to allow state change if it was just clicked
                page.waitForTimeout(300);

                boolean isChecked = false;
                String tagName = (String) element.evaluate("el => el.tagName.toLowerCase()");
                
                if ("input".equals(tagName)) {
                    isChecked = element.isChecked();
                } else {
                    // If it's a label, check its associated input
                    if ("label".equals(tagName)) {
                        String forId = (String) element.getAttribute("for");
                        if (forId != null && !forId.isEmpty()) {
                            Locator associatedInput = page.locator("input#" + forId);
                            if (associatedInput.count() > 0) {
                                isChecked = associatedInput.isChecked();
                            }
                        }
                    }
                    
                    // If still not checked, check for nested input
                    if (!isChecked) {
                        Locator nestedInput = element.locator("input");
                        if (nestedInput.count() > 0) {
                            isChecked = nestedInput.first().isChecked();
                        }
                    }
                    
                    // Finally check common "active/selected" attributes or classes
                    if (!isChecked) {
                        String currentClasses = (String) element.getAttribute("class");
                        logger.debug("Checking selection state for <{}> with classes: [{}]", tagName, currentClasses);

                        isChecked = (boolean) element.evaluate("el => {" +
                            "  const cl = (el.getAttribute('class') || '').toLowerCase().split(/\\s+/);" +
                            "  const ac = el.getAttribute('aria-checked');" +
                            "  const as = el.getAttribute('aria-selected');" +
                            "  return ac === 'true' || as === 'true' || " +
                            "         cl.includes('active') || " +
                            "         cl.includes('selected') || " +
                            "         cl.includes('checked');" +
                            "}");
                    }
                }

                if (isChecked == expectChecked) {
                    logger.success(" Element '{}' matches expected state: {}", target, expectChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED");
                } else {
                    logger.error(" Element '{}' state mismatch. Expected: {}, Actual: {}", 
                        target, 
                        expectChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED",
                        isChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED");
                    allMatched = false;
                }
            } else {
                 logger.failure("Element not found for state check: {}", target);
                 allMatched = false;
            }
        }
        
        if (allMatched) {
            logger.section("VALIDATION SUCCESS");
            logger.info("--------------------------------------------------");
            return true;
        } else {
            logger.section("VALIDATION FAILED");
            logger.info("--------------------------------------------------");
            return false;
        }
    }
}
