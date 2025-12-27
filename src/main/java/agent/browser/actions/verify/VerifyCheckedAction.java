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

            Locator element = locator.waitForSmartElement(target, "checkbox", scope, plan.getFrameAnchor(), true);
            
            if (element != null) {
                // Small wait to allow state change if it was just clicked
                page.waitForTimeout(300);

                boolean isChecked = false;
                String debugInfo = "N/A";
                try {
                    // Try to get some debug info first
                    try {
                        debugInfo = (String) element.evaluate("el => el.tagName + ' id=' + el.id + ' class=' + el.className");
                    } catch (Exception ignored) {}

                    // SPECIAL CASE: Autocomplete multi-value tags (React-Select, etc.)
                    // and Selectable list items
                    // For these, "selected" means visible tag or active class, not checkbox state
                    boolean isAutocompleteTag = debugInfo.toLowerCase().contains("multi-value") || 
                                               debugInfo.toLowerCase().contains("auto-complete");
                    boolean isSelectableListItem = debugInfo.toLowerCase().contains("list-group-item");
                    
                    if (isAutocompleteTag) {
                        // For autocomplete, just check if the element is visible
                        isChecked = element.isVisible();
                        logger.debug("Autocomplete tag detected - checking visibility instead of checked state: {}", isChecked);
                    } else if (isSelectableListItem) {
                        // For selectable list items, check if they have the 'active' class
                        String classList = (String) element.evaluate("el => el.className");
                        isChecked = classList != null && classList.toLowerCase().contains("active");
                        logger.debug("Selectable list item detected - checking for 'active' class: {}", isChecked);
                    } else {
                        // REGULAR CASE: Checkboxes, radios, and other interactive elements
                        // Use a simpler approach to avoid JavaScript syntax errors
                        try {
                            String tagName = (String) element.evaluate("el => el.tagName");
                            
                            if ("INPUT".equalsIgnoreCase(tagName)) {
                                // For INPUT elements, use Playwright's built-in isChecked()
                                isChecked = element.isChecked();
                                logger.debug("INPUT element - using isChecked(): {}", isChecked);
                            } else {
                                // For other elements, check for selection indicators
                                String className = (String) element.evaluate("el => el.className || ''");
                                String ariaChecked = (String) element.evaluate("el => el.getAttribute('aria-checked') || ''");
                                String ariaSelected = (String) element.evaluate("el => el.getAttribute('aria-selected') || ''");
                                
                                isChecked = "true".equals(ariaChecked) || 
                                           "true".equals(ariaSelected) ||
                                           (className != null && (className.toLowerCase().contains("active") ||
                                                                 className.toLowerCase().contains("selected") ||
                                                                 className.toLowerCase().contains("checked")));
                                logger.debug("Non-INPUT element - className: {}, aria-checked: {}, aria-selected: {}, isChecked: {}", 
                                            className, ariaChecked, ariaSelected, isChecked);
                            }
                        } catch (Exception evalEx) {
                            logger.warn("Error evaluating element state: {}", evalEx.getMessage());
                            // Fallback to checking visibility for non-checkbox elements
                            isChecked = element.isVisible();
                        }
                    }
                } catch (Exception e) {
                    if (!expectChecked) {
                        logger.success(" Element '{}' is not checkable - correctly NOT SELECTED", target);
                        continue;
                    } else {
                        logger.failure("Element '{}' check failed. Info: {}. Error: {}", target, debugInfo, e.getMessage());
                        allMatched = false;
                        continue;
                    }
                }

                if (isChecked == expectChecked) {
                    logger.success(" Element '{}' matches expected state: {}", target, expectChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED");
                } else {
                    logger.error(" Element '{}' state mismatch. Info: {}. Expected: {}, Actual: {}", 
                        target, 
                        debugInfo,
                        expectChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED",
                        isChecked ? "CHECKED/SELECTED" : "UNCHECKED/NOT SELECTED");
                    allMatched = false;
                }
            } else {
                 if (!expectChecked) {
                     logger.success(" Element '{}' not found - correctly NOT SELECTED", target);
                 } else {
                     logger.failure("Element not found for state check: {}", target);
                     allMatched = false;
                 }
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
