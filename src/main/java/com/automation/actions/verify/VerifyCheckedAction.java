package com.automation.actions.verify;

import com.automation.browser.actions.BrowserAction;
import com.automation.browser.locator.core.SmartLocator;
import com.automation.browser.locator.table.TableNavigator;
import com.automation.planner.ActionPlan;
import com.automation.planner.EnhancedActionPlan;
import com.automation.utils.LoggerUtil;
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

                    // Use generic, framework-agnostic detection methods
                    if (isMultiValueComponent(element)) {
                        // Multi-value components (tags/chips) - check visibility
                        isChecked = element.isVisible();
                        logger.debug("Multi-value component detected - checking visibility: {}", isChecked);
                    } else if (isSelectableListItem(element)) {
                        // Selectable list items - check for active/selected state
                        isChecked = hasActiveOrSelectedState(element);
                        logger.debug("Selectable list item detected - active/selected state: {}", isChecked);
                    } else {
                   // REGULAR CASE: Checkboxes, radios, and other interactive elements
                        try {
                            String tagName = (String) element.evaluate("el => el.tagName");
                            
                            if ("INPUT".equalsIgnoreCase(tagName)) {
                                // For INPUT elements, use Playwright's built-in isChecked()
                                isChecked = element.isChecked();
                                logger.debug("INPUT element - using isChecked(): {}", isChecked);
                            } else if ("LABEL".equalsIgnoreCase(tagName) || "SPAN".equalsIgnoreCase(tagName)) {
                                // ENHANCEMENT: Handle custom checkbox/radio implementations
                                // Many frameworks (React, Bootstrap) hide the actual input and style a label/span
                                // Strategy: Find the associated hidden input element and check its state
                                
                                logger.debug("Found {} element - searching for associated INPUT", tagName);
                                
                                // Try to find associated input via 'for' attribute (for LABEL)
                                String forAttr = (String) element.evaluate("el => el.getAttribute('for') || ''");
                                if (forAttr != null && !forAttr.isEmpty()) {
                                    try {
                                        Locator input = page.locator("#" + forAttr).first();
                                        if (input != null) {
                                            isChecked = input.isChecked();
                                            logger.debug("Found associated input via 'for' attribute - isChecked(): {}", isChecked);
                                        }
                                    } catch (Exception e) {
                                        logger.debug("Could not find input by 'for' attribute: {}", forAttr);
                                    }
                                } else {
                                    // Try to find input within the same parent or as a sibling
                                    try {
                                        // First try: look for input inside the label/span
                                        Locator inputInside = element.locator("input[type='checkbox'], input[type='radio']").first();
                                        if (inputInside.count() > 0) {
                                            isChecked = inputInside.isChecked();
                                            logger.debug("Found input inside element - isChecked(): {}", isChecked);
                                        } else {
                                            // Second try: look for sibling input in parent
                                            Locator inputSibling = element.locator("xpath=./parent::*/input[(@type='checkbox' or @type='radio')]").first();
                                            if (inputSibling.count() > 0) {
                                                isChecked = inputSibling.isChecked();
                                                logger.debug("Found sibling input in parent - isChecked(): {}", isChecked);
                                            } else {
                                                logger.warn("Could not find associated checkbox/radio input for {} element", tagName);
                                                // Fallback to checking visual state
                                                String className = (String) element.evaluate("el => el.className || ''");
                                                isChecked = className != null && (className.toLowerCase().contains("checked") ||
                                                                                 className.toLowerCase().contains("selected"));
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.debug("Error searching for associated input: {}", e.getMessage());
                                    }
                                }
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
    
    /**
     * Detects multi-value components (tags, chips, tokens) using generic patterns.
     * Works with: React-Select, Angular Material Chips, Vue Tags Input, vanilla JS implementations
     * 
     * Detection Strategy:
     * 1. Check ARIA role (listbox, combobox) - WCAG standard
     * 2. Check for common class patterns (tag, chip, token)
     * 3. Framework-specific patterns as fallback only
     */
    private boolean isMultiValueComponent(Locator element) {
        try {
            // Strategy 1: Check ARIA role (highest priority - framework-agnostic)
            String role = (String) element.evaluate("el => el.getAttribute('role')");
            if ("listbox".equals(role) || "combobox".equals(role)) {
                return true;
            }
            
            // Strategy 2: Check for generic tag/chip/token patterns
            String classList = (String) element.evaluate("el => el.className || ''");
            if (classList != null && !classList.isEmpty()) {
                String lowerClass = classList.toLowerCase();
                // Generic patterns that work across frameworks
                if (lowerClass.contains("tag") && !lowerClass.contains("stage")) {  // Avoid "stage"
                    return true;
                }
                if (lowerClass.contains("chip")) {
                    return true;
                }
                if (lowerClass.contains("token")) {
                    return true;
                }
                if (lowerClass.contains("badge") && lowerClass.contains("dismiss")) {
                    return true;
                }
                
                // Framework-specific patterns as fallback
                if (lowerClass.contains("multi-value")) {  // React-Select
                    return true;
                }
                if (lowerClass.contains("mat-chip")) {  // Angular Material
                    return true;
                }
                if (lowerClass.contains("auto-complete")) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Detects selectable list items using generic patterns.
     * Works with: Bootstrap, Material-UI, Ant Design, Chakra UI, vanilla HTML
     * 
     * Detection Strategy:
     * 1. Check if element is an LI tag
     * 2. Verify it's part of a selectable list context
     */
    private boolean isSelectableListItem(Locator element) {
        try {
            String tagName = (String) element.evaluate("el => el.tagName");
            if (!"LI".equalsIgnoreCase(tagName)) {
                return false;
            }
            
            // Check if this LI has selectable characteristics
            String classList = (String) element.evaluate("el => el.className || ''");
            if (classList != null && !classList.isEmpty()) {
                String lowerClass = classList.toLowerCase();
                // Generic patterns
                if (lowerClass.contains("selectable")) {
                    return true;
                }
                if (lowerClass.contains("clickable")) {
                    return true;
                }
                
                // Common UI framework patterns (as additional indicators)
                if (lowerClass.contains("list-group-item")) {  // Bootstrap
                    return true;
                }
                if (lowerClass.contains("list-item")) {  // Generic pattern
                    return true;
                }
                if (lowerClass.contains("menu-item")) {
                    return true;
                }
            }
            
            // Check if parent UL/OL has selectable attributes
            String parentRole = (String) element.evaluate("el => el.parentElement?.getAttribute('role') || ''");
            if ("listbox".equals(parentRole) || "menu".equals(parentRole)) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if an element has active or selected state using generic patterns.
     * Works with all frameworks by checking both ARIA attributes and class names.
     * 
     * Priority: ARIA > Class patterns
     */
    private boolean hasActiveOrSelectedState(Locator element) {
        try {
            // Priority 1: Check ARIA attributes (framework-agnostic standard)
            String ariaSelected = (String) element.evaluate("el => el.getAttribute('aria-selected')");
            if ("true".equals(ariaSelected)) {
                return true;
            }
            
            String ariaChecked = (String) element.evaluate("el => el.getAttribute('aria-checked')");
            if ("true".equals(ariaChecked)) {
                return true;
            }
            
            // Priority 2: Check generic class patterns
            String classList = (String) element.evaluate("el => el.className || ''");
            if (classList != null && !classList.isEmpty()) {
                String lowerClass = classList.toLowerCase();
                return lowerClass.contains("active") ||
                       lowerClass.contains("selected") ||
                       lowerClass.contains("is-active") ||
                       lowerClass.contains("is-selected");
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
