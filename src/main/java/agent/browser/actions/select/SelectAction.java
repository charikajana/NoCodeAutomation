package agent.browser.actions.select;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.browser.locator.table.TableNavigator;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * Universal Dropdown Handler - Works with ANY UI framework
 * Tested with: Native HTML select, React-Select, Material-UI, Ant Design, etc.
 */
public class SelectAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(SelectAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String dropdownLabel = plan.getElementName();
        String optionText = plan.getValue();
        Locator scope = null;

        // Handle row scoping if present
        if (plan.getRowAnchor() != null) {
            TableNavigator navigator = new TableNavigator();
            scope = navigator.findRowByAnchor(page, plan.getRowAnchor());
            if (scope == null) {
                logger.failure("Row not found for anchor: {}", plan.getRowAnchor());
                return false;
            }
        }

        logger.info("Finding element: '{}'", dropdownLabel);
        
        // Step 1: Find the element using smart locator
        Locator dropdownWrapper = locator.waitForSmartElement(dropdownLabel, "select", scope, plan.getFrameAnchor());
        
        if (dropdownWrapper == null) {
            logger.failure("Element not found: {}", dropdownLabel);
            return false;
        }
        
        // Step 1.5: Check if this is an AUTOCOMPLETE field (not a dropdown)
        if (isAutocompleteField(dropdownWrapper)) {
            logger.info("Detected AUTOCOMPLETE field, using autocomplete logic");
            return handleAutocomplete(page, dropdownWrapper, optionText, dropdownLabel);
        }

        // Step 2: Determine if it's a native <select> or custom dropdown
        String tagName = (String) dropdownWrapper.evaluate("el => el.tagName.toLowerCase()");
        
        // Step 3: Check if we have multiple values to select (semicolon-separated)
        if (optionText != null && optionText.contains(";")) {
            String[] values = optionText.split(";");
            logger.info("  Multi-value selection detected: {} options", values.length);
            
            // SMART BEHAVIOR: Check if current selections match desired selections
            if (!"select".equals(tagName)) {
                // For custom multiselect (React-Select), check current selected chips
                java.util.List<String> currentSelections = getCurrentlySelectedOptions(dropdownWrapper);
                java.util.Set<String> desiredSet = new java.util.HashSet<>(java.util.Arrays.asList(values));
                desiredSet = desiredSet.stream().map(String::trim).collect(java.util.stream.Collectors.toSet());
                
                boolean exactMatch = currentSelections.size() == desiredSet.size() && 
                                    currentSelections.stream().allMatch(desiredSet::contains);
                
                if (exactMatch) {
                    logger.debug("Current selections already match desired selections. Skipping");
                    return true;
                }
                
                // Clear existing selections if they don't match
                if (!currentSelections.isEmpty()) {
                    logger.info("  Clearing {} existing selection(s) before selecting new ones", currentSelections.size());
                    for (String existing : currentSelections) {
                        clearSingleSelection(dropdownWrapper, existing);
                    }
                    // Wait for dropdown to stabilize after clearing
                    try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
            
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                logger.info("  Selecting option {} of {}: '{}'", (i + 1), values.length, value);
                
                boolean success;
                if ("select".equals(tagName)) {
                    success = handleNativeSelect(dropdownWrapper, value, dropdownLabel);
                } else {
                    // For multiselect, only open dropdown on first selection
                    // Subsequent selections should reuse the already-open menu
                    boolean isFirstSelection = (i == 0);
                    success = handleCustomDropdownMultiselect(page, dropdownWrapper, value, dropdownLabel, isFirstSelection);
                }
                
                if (!success) {
                    logger.failure("Could not select '{}' from multi-value list", value);
                    return false;
                }
                
                // Small delay between selections for multiselect dropdowns
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            logger.success("Successfully selected all {} values from '{}'", values.length, dropdownLabel);
            return true;
        }
        
        // Single value selection (original behavior)
        if ("select".equals(tagName)) {
            return handleNativeSelect(dropdownWrapper, optionText, dropdownLabel);
        } else {
            return handleCustomDropdown(page, dropdownWrapper, optionText, dropdownLabel);
        }
    }

    /**
     * Handle native HTML <select> element (simplest case)
     */
    private boolean handleNativeSelect(Locator select, String optionText, String label) {
        try {
            logger.debug("Native <select> detected");
            
            // Try by label first
            java.util.List<String> result = select.selectOption(new SelectOption().setLabel(optionText));
            
            if (result.size() > 0) {
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            
            // Fallback to value
            result = select.selectOption(new SelectOption().setValue(optionText));
            if (result.size() > 0) {
                logger.success("Selected '{}' (by value) from dropdown '{}'", optionText, label);
                return true;
            }
            
            logger.failure("Option '{}' not found in dropdown '{}'", optionText, label);
            return false;
            
        } catch (Exception e) {
            logger.failure("Could not select option: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Handler for multiselect custom dropdowns (React-Select, MUI, Ant Design, etc.)
     * For multiselect dropdowns, the menu stays open after selecting an option.
     * Only click the control to open menu on first selection.
     */
    private boolean handleCustomDropdownMultiselect(Page page, Locator wrapper, String optionText, String label, boolean isFirstSelection) {
        try {
            logger.debug("Custom dropdown detected");
            
            // Get the wrapper's ID or class to scope our searches
            String wrapperId = (String) wrapper.evaluate("el => el.id || ''");
            String wrapperClass = (String) wrapper.evaluate("el => el.className || ''");
            
            logger.debug("Wrapper ID: '{}', Class: '{}'", wrapperId, wrapperClass);
            
            // Step 1: Open dropdown (only if not already open)
            if (isFirstSelection) {
                try {
                    // Check if menu is already open (might be open from previous actions like deselect)
                    Locator existingMenu = wrapper.locator("[class*='menu']").first();
                    boolean menuAlreadyOpen = existingMenu.count() > 0 && existingMenu.isVisible();
                    
                    if (menuAlreadyOpen) {
                        logger.debug("Menu is already open, skipping click");
                    } else {
                        // Try clicking the control div specifically for React-Select
                        Locator control = wrapper.locator("[class*='control'], [class*='css-'][class*='-control']").first();
                        if (control.count() > 0) {
                            logger.debug("Clicking React-Select control");
                            control.click(new Locator.ClickOptions().setTimeout(5000));
                        } else {
                            logger.debug("Clicking dropdown wrapper");
                            wrapper.click(new Locator.ClickOptions().setTimeout(5000).setForce(true));
                        }
                        
                        logger.debug("Clicked dropdown, waiting for options menu");
                        Thread.sleep(500); // Wait for animation
                        
                        // Wait for menu container to appear
                        Locator menu = wrapper.locator("[class*='menu']").first();
                        if (menu.count() > 0) {
                            menu.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                            logger.debug("Dropdown menu container appeared");
                        } else {
                            // Fallback: wait for any option elements to appear
                            logger.debug("No menu container found, waiting for options");
                            page.locator("[id*='react-select'][id*='option'], div[class*='option'], [role='option']")
                                .first()
                                .waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
                            logger.debug("Options appeared");
                        }
                    }
                    
                } catch (Exception e) {
                    logger.warning("Failed to open dropdown or menu didn't appear: {}", e.getMessage());
                    logger.warning("Attempting to continue anyway");
                    // Don't fail immediately, try to find options anyway
                }
            } else {
                logger.debug("Multiselect menu already open, skipping click");
                Thread.sleep(300); // Small delay for stability
            }
            
            // Step 2: Find and click the option (same logic for all selections)
            return selectOptionFromOpenMenu(page, optionText, label);
            
        } catch (Exception e) {
            logger.error("Custom dropdown interaction failed: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Select an option from an already-open menu.
     * This method assumes the menu is visible and searches for the option.
     */
    private boolean selectOptionFromOpenMenu(Page page, String optionText, String label) {
        // FIRST: Check if this option is already selected (appears as a chip/tag)
        // This is important for multiselect dropdowns where already-selected options don't appear in the menu
        Locator selectedChip = page.locator(String.format("div[class*='multiValue']:has-text(\"%s\")", optionText)).first();
        if (selectedChip.count() > 0 && selectedChip.isVisible()) {
            logger.debug("Option '{}' is already selected, skipping", optionText);
            return true; // Already selected, no need to select again
        }
        
        Locator option = null;
        
        // Try multiple strategies in order of specificity
        
        // STRATEGY 1: React-Select with ID pattern (most reliable for React-Select)
        String reactSelectIdPattern = String.format("div[id^='react-select-'][id*='-option-']:has-text(\"%s\")", optionText);
        option = page.locator(reactSelectIdPattern).first();
        if (option.count() > 0 && option.isVisible()) {
            logger.debug("Found option using React-Select ID pattern");
            option.click();
            logger.success("Selected '{}' from dropdown '{}'", optionText, label);
            return true;
        }
        logger.debug("React-Select ID pattern didn't match");
        
        // STRATEGY 2: React-Select with class pattern
        String reactSelectClassPattern = String.format("div[class*='option']:has-text(\"%s\")", optionText);
        option = page.locator(reactSelectClassPattern).first();
        if (option.count() > 0 && option.isVisible()) {
            logger.debug("Found option using React-Select class pattern");
            option.click();
            logger.success("Selected '{}' from dropdown '{}'", optionText, label);
            return true;
        }
        logger.debug("React-Select class pattern didn't match");
        
        // STRATEGY 3: XPath-based React-Select pattern
        option = page.locator(String.format("//div[starts-with(@id, 'react-select-') and contains(@id, '-option-') and contains(text(), '%s')]", optionText)).first();
        if (option.count() > 0 && option.isVisible()) {
            logger.debug("Found option using React-Select XPath pattern");
            option.click();
            logger.success("Selected '{}' from dropdown '{}'", optionText, label);
            return true;
        }
        logger.debug("React-Select XPath pattern didn't match");
        
        // STRATEGY 4: Role-based search (ARIA-compliant dropdowns)
        option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION,
                new Page.GetByRoleOptions().setName(optionText)).first();
        if (option.count() > 0 && option.isVisible()) {
            logger.debug("Found option using ARIA role");
            option.click();
            logger.success("Selected '{}' from dropdown '{}'", optionText, label);
            return true;
        }
        logger.debug("ARIA role pattern didn't match");
        
        // STRATEGY 5: Generic visible text search (last resort)
        option = page.getByText(optionText, new Page.GetByTextOptions().setExact(true)).first();
        if (option.count() > 0 && option.isVisible()) {
            logger.debug("Found option using generic visible text search");
            option.click();
            logger.success("Selected '{}' from dropdown '{}'", optionText, label);
            return true;
        }
        logger.debug("Generic text search didn't match");
        
        logger.failure("Option '{}' not found or not visible after opening dropdown", optionText);
        logger.error("Tried all strategies: React-Select (ID, class, XPath), ARIA, and generic text");
        return false;
    }

    /**
     * Universal handler for custom dropdowns (React-Select, MUI, Ant Design, etc.)
     * 
     * Strategy:
     * 1. Click the wrapper to open the dropdown
     * 2. Wait for options menu to appear
     * 3. Find and click the matching option
     */
    private boolean handleCustomDropdown(Page page, Locator wrapper, String optionText, String label) {
        try {
            logger.success("Custom dropdown detected");
            
            // For multiselect, check if this option is already selected
            // Selected options appear as chips/tags with class containing 'multiValue'
            Locator selectedChip = wrapper.locator(String.format("div[class*='multiValue']:has-text(\"%s\")", optionText)).first();
            if (selectedChip.count() > 0 && selectedChip.isVisible()) {
                logger.debug("Option '{}' is already selected in multiselect, skipping", optionText);
                return true;
            }
            
            // Get the wrapper's ID or class to scope our searches
            String wrapperId = (String) wrapper.evaluate("el => el.id || ''");
            String wrapperClass = (String) wrapper.evaluate("el => el.className || ''");
            
            logger.debug("Wrapper ID: '{}', Class: '{}'", wrapperId, wrapperClass);
            
            // Step 1: Click to open dropdown
            try {
                // Try clicking the control div specifically for React-Select
                Locator control = wrapper.locator("[class*='control'], [class*='css-'][class*='-control']").first();
                if (control.count() > 0) {
                    logger.debug("Clicking React-Select control...");
                    control.click(new Locator.ClickOptions().setTimeout(5000));
                } else {
                    logger.debug("Clicking dropdown wrapper...");
                    wrapper.click(new Locator.ClickOptions().setTimeout(5000).setForce(true));
                }
                
                logger.debug("Clicked dropdown, waiting for options menu...");
                Thread.sleep(500); // Wait for animation
                
                // Wait for menu container to appear within the wrapper (React-Select specific)
                // React-Select creates a menu div inside the wrapper after clicking
                Locator menu = wrapper.locator("[class*='menu']").first();
                if (menu.count() > 0) {
                    menu.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                    logger.debug("Dropdown menu container appeared");
                } else {
                    // Fallback: wait for any option elements to appear
                    logger.debug("No menu container found, waiting for options...");
                    page.locator("[id*='react-select'][id*='option'], div[class*='option'], [role='option']")
                        .first()
                        .waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
                    logger.debug("Options appeared");
                }
                
            } catch (Exception e) {
                logger.warning("Failed to open dropdown or menu didn't appear: {}", e.getMessage());
                logger.warning("Attempting to continue anyway...");
                // Don't fail immediately, try to find options anyway
            }
            
            // Step 2: Find the option in the newly appeared menu
            // Try multiple strategies in order of specificity
            
            Locator option = null;
            
            // STRATEGY 1: React-Select with ID pattern (most reliable for React-Select)
            // Pattern: div[id^="react-select-"][id*="-option-"]
            String reactSelectIdPattern = String.format("div[id^='react-select-'][id*='-option-']:has-text(\"%s\")", optionText);
            option = page.locator(reactSelectIdPattern).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using React-Select ID pattern");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("React-Select ID pattern didn't match");
            
            // STRATEGY 2: React-Select with class pattern
            // Pattern: div[class*="option"]:has-text("...")
            String reactSelectClassPattern = String.format("div[class*='option']:has-text(\"%s\")", optionText);
            option = page.locator(reactSelectClassPattern).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using React-Select class pattern");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("React-Select class pattern didn't match");
            
            // STRATEGY 3: XPath-based React-Select pattern (more flexible text matching)
            option = page.locator(String.format("//div[starts-with(@id, 'react-select-') and contains(@id, '-option-') and contains(text(), '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using React-Select XPath pattern");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("React-Select XPath pattern didn't match");
            
            // STRATEGY 4: Role-based search (ARIA-compliant dropdowns like Material-UI)
            option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION,
                    new Page.GetByRoleOptions().setName(optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using ARIA role");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("ARIA role pattern didn't match");
            
            // STRATEGY 5: Material-UI pattern
            option = page.locator(String.format("//li[@role='option' and contains(., '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using Material-UI pattern");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("Material-UI pattern didn't match");
            
            // STRATEGY 6: Ant Design pattern
            option = page.locator(String.format("//*[contains(@class, 'ant-select-item') and contains(., '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using Ant Design pattern");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("Ant Design pattern didn't match");
            
            // STRATEGY 7: Generic visible text search (last resort - very broad)
            // Look for any visible element with exact text match
            option = page.getByText(optionText, new Page.GetByTextOptions().setExact(true)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using generic visible text search");
                option.click();
                logger.success("Selected '{}' from dropdown '{}'", optionText, label);
                return true;
            }
            logger.debug("Generic text search didn't match");
            
            logger.failure("Option '{}' not found or not visible after opening dropdown", optionText);
            logger.error("Tried all strategies: React-Select (ID, class, XPath), ARIA, Material-UI, Ant Design, and generic text");
            return false;
            
        } catch (Exception e) {
            logger.error("Custom dropdown interaction failed. Error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get list of currently selected options in a multiselect dropdown
     */
    private java.util.List<String> getCurrentlySelectedOptions(Locator wrapper) {
        java.util.List<String> selected = new java.util.ArrayList<>();
        try {
            // For React-Select, selected options appear as chips/tags with class containing 'multiValue'
            com.microsoft.playwright.Locator chips = wrapper.locator("div[class*='multiValue']");
            int chipCount = chips.count();
            
            for (int i = 0; i < chipCount; i++) {
                // Get the text of each chip (exclude the remove button)
                Locator chip = chips.nth(i);
                Locator label = chip.locator("div[class*='multiValueLabel']").first();
                if (label.count() > 0) {
                    String text = label.textContent().trim();
                    selected.add(text);
                }
            }
        } catch (Exception e) {
            // If error, return empty list
        }
        return selected;
    }
    
    /**
     * Clear a single selected option from multiselect dropdown
     */
    private void clearSingleSelection(Locator wrapper, String optionText) {
        try {
            // Find the chip for this option
            Locator chip = wrapper.locator(String.format("div[class*='multiValue']:has-text(\"%s\")", optionText)).first();
            if (chip.count() > 0 && chip.isVisible()) {
                // Find and click the remove button
                Locator removeButton = chip.locator("svg, div[role='button'], *[aria-label*='remove'], *[class*='Remove'], *[class*='remove']").first();
                if (removeButton.count() > 0) {
                    removeButton.click(new Locator.ClickOptions().setTimeout(3000));
                    Thread.sleep(200);
                }
            }
        } catch (Exception e) {
            // Silently fail if can't clear
        }
    }
    
    /**
     * Detect if this is an autocomplete field vs a dropdown.
     * Autocomplete fields typically:
     * - Are input elements (not divs)
     * - Have role="combobox" or type="text"
     * - Have autocomplete attributes
     * - OR contain an input with these properties
     */
    private boolean isAutocompleteField(Locator element) {
        try {
            String tagName = (String) element.evaluate("el => el.tagName.toLowerCase()");
            String role = (String) element.evaluate("el => el.getAttribute('role') || ''");
            String type = (String) element.evaluate("el => el.getAttribute('type') || ''");
            String autocomplete = (String) element.evaluate("el => el.getAttribute('autocomplete') || ''");
            String ariaAutoComplete = (String) element.evaluate("el => el.getAttribute('aria-autocomplete') || ''");
            
            // Check for autocomplete indicators on the element itself
            boolean isInputElement = "input".equals(tagName) || "textarea".equals(tagName);
            boolean hasComboboxRole = "combobox".equals(role);
            boolean hasTextType = "text".equals(type) || type.isEmpty();
            boolean hasAutocompleteAttr = !autocomplete.isEmpty() || !ariaAutoComplete.isEmpty();
            
            // If it's an input with combobox role or autocomplete attributes, it's likely autocomplete
            if (isInputElement && (hasComboboxRole || hasAutocompleteAttr || ariaAutoComplete.equals("list"))) {
                logger.debug("Autocomplete detected: input={}, role={}, autocomplete={}", tagName, role, ariaAutoComplete);
                return true;
            }
            
            // Check if there's an input child with autocomplete attributes (for React-Select wrappers)
            Locator inputChild = element.locator("input[role='combobox'], input[aria-autocomplete], input[type='text']").first();
            if (inputChild.count() > 0) {
                String childRole = (String) inputChild.evaluate("el => el.getAttribute('role') || ''");
                String childAriaAuto = (String) inputChild.evaluate("el => el.getAttribute('aria-autocomplete') || ''");
                
                if ("combobox".equals(childRole) || "list".equals(childAriaAuto) || !childAriaAuto.isEmpty()) {
                    logger.debug("Autocomplete detected: wrapper contains input with role={}, aria-autocomplete={}", childRole, childAriaAuto);
                    // Update the element reference to the input for handleAutocomplete
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.debug("Error detecting autocomplete: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle autocomplete field interaction:
     * 1. Find the actual input field (if wrapper was passed)
     * 2. Clear the field
     * 3. Type partial text to trigger suggestions
     * 4. Wait for suggestions dropdown
     * 5. Click the matching option
     */
    private boolean handleAutocomplete(Page page, Locator elementOrWrapper, String optionText, String label) {
        try {
            logger.info("Autocomplete: Selecting '{}' from '{}'", optionText, label);
            
            // Step 0: Find the actual input field if a wrapper was passed
            String tagName = (String) elementOrWrapper.evaluate("el => el.tagName.toLowerCase()");
            Locator inputField = elementOrWrapper;
            
            if (!"input".equals(tagName) && !"textarea".equals(tagName)) {
                // It's a wrapper, find the input inside
                logger.debug("Wrapper detected, finding input field inside...");
                Locator foundInput = elementOrWrapper.locator("input[role='combobox'], input[aria-autocomplete], input[type='text']").first();
                if (foundInput.count() > 0) {
                    inputField = foundInput;
                    logger.debug("Found input field inside wrapper");
                } else {
                    logger.failure("No input field found inside wrapper for autocomplete");
                    return false;
                }
            }
            
            // Step 1: Clear and focus the input
            inputField.click();
            inputField.fill("");
            Thread.sleep(200);
            
            // Step 2: Type partial text to trigger autocomplete
            // Use first 2 characters or full text if shorter
            String partialText = optionText.length() > 2 ? optionText.substring(0, 2) : optionText;
            logger.debug("Typing partial text: '{}'", partialText);
            inputField.type(partialText);
            
            // Step 3: Wait for autocomplete suggestions to appear
            Thread.sleep(500);
            
            // Step 4: Find and click the matching option from suggestions
            // Try multiple patterns for autocomplete suggestions
            Locator option = null;
            
            // Pattern 1: React-Select autocomplete
            option = page.locator(String.format("div[id*='option']:has-text(\"%s\")", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using React-Select pattern");
                option.click();
                logger.success("Selected '{}' from autocomplete '{}'", optionText, label);
                return true;
            }
            
            // Pattern 2: ARIA role option
            option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION,
                    new Page.GetByRoleOptions().setName(optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using ARIA role");
                option.click();
                logger.success("Selected '{}' from autocomplete '{}'", optionText, label);
                return true;
            }
            
            // Pattern 3: Generic dropdown option
            option = page.locator(String.format("div[class*='option']:has-text(\"%s\")", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using generic pattern");
                option.click();
                logger.success("Selected '{}' from autocomplete '{}'", optionText, label);
                return true;
            }
            
            // Pattern 4: List item
            option = page.locator(String.format("li:has-text(\"%s\")", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                logger.debug("Found option using list pattern");
                option.click();
                logger.success("Selected '{}' from autocomplete '{}'", optionText, label);
                return true;
            }
            
            logger.failure("Autocomplete option '{}' not found in suggestions", optionText);
            return false;
            
        } catch (Exception e) {
            logger.failure("Autocomplete interaction failed: {}", e.getMessage());
            return false;
        }
    }
}
