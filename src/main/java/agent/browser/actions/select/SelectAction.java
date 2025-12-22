package agent.browser.actions.select;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.browser.locator.table.TableNavigator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * Universal Dropdown Handler - Works with ANY UI framework
 * Tested with: Native HTML select, React-Select, Material-UI, Ant Design, etc.
 */
public class SelectAction implements BrowserAction {
    
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
                System.err.println("❌ FAILED: Row not found for anchor: " + plan.getRowAnchor());
                return false;
            }
        }

        System.out.println("🔽 Finding dropdown: '" + dropdownLabel + "'");
        
        // Step 1: Find the dropdown wrapper using smart locator
        Locator dropdownWrapper = locator.waitForSmartElement(dropdownLabel, "select", scope);
        
        if (dropdownWrapper == null) {
            System.err.println("❌ FAILED: Dropdown element not found: " + dropdownLabel);
            return false;
        }

        // Step 2: Determine if it's a native <select> or custom dropdown
        String tagName = (String) dropdownWrapper.evaluate("el => el.tagName.toLowerCase()");
        
        // Step 3: Check if we have multiple values to select (semicolon-separated)
        if (optionText != null && optionText.contains(";")) {
            String[] values = optionText.split(";");
            System.out.println("  📋 Multi-value selection detected: " + values.length + " options");
            
            // SMART BEHAVIOR: Check if current selections match desired selections
            if (!"select".equals(tagName)) {
                // For custom multiselect (React-Select), check current selected chips
                java.util.List<String> currentSelections = getCurrentlySelectedOptions(dropdownWrapper);
                java.util.Set<String> desiredSet = new java.util.HashSet<>(java.util.Arrays.asList(values));
                desiredSet = desiredSet.stream().map(String::trim).collect(java.util.stream.Collectors.toSet());
                
                boolean exactMatch = currentSelections.size() == desiredSet.size() && 
                                    currentSelections.stream().allMatch(desiredSet::contains);
                
                if (exactMatch) {
                    System.out.println("  ✓ Current selections already match desired selections. Skipping...");
                    return true;
                }
                
                // Clear existing selections if they don't match
                if (!currentSelections.isEmpty()) {
                    System.out.println("  🗑️ Clearing " + currentSelections.size() + " existing selection(s) before selecting new ones...");
                    for (String existing : currentSelections) {
                        clearSingleSelection(dropdownWrapper, existing);
                    }
                    // Wait for dropdown to stabilize after clearing
                    try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
            
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                System.out.println("  🎯 Selecting option " + (i + 1) + " of " + values.length + ": '" + value + "'");
                
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
                    System.err.println("❌ FAILED: Could not select '" + value + "' from multi-value list");
                    return false;
                }
                
                // Small delay between selections for multiselect dropdowns
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            System.out.println("✅ Successfully selected all " + values.length + " values from '" + dropdownLabel + "'");
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
            System.out.println("  ✅ Native <select> detected");
            
            // Try by label first
            java.util.List<String> result = select.selectOption(new SelectOption().setLabel(optionText));
            
            if (result.size() > 0) {
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            
            // Fallback to value
            result = select.selectOption(new SelectOption().setValue(optionText));
            if (result.size() > 0) {
                System.out.println("✅ Selected '" + optionText + "' (by value) from dropdown '" + label + "'");
                return true;
            }
            
            System.err.println("❌ FAILED: Option '" + optionText + "' not found in dropdown '" + label + "'");
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ FAILED: Could not select option. Error: " + e.getMessage());
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
            System.out.println("  ✅ Custom dropdown detected");
            
            // Get the wrapper's ID or class to scope our searches
            String wrapperId = (String) wrapper.evaluate("el => el.id || ''");
            String wrapperClass = (String) wrapper.evaluate("el => el.className || ''");
            
            System.out.println("  📋 Wrapper ID: '" + wrapperId + "', Class: '" + wrapperClass + "'");
            
            // Step 1: Open dropdown (only if not already open)
            if (isFirstSelection) {
                try {
                    // Check if menu is already open (might be open from previous actions like deselect)
                    Locator existingMenu = wrapper.locator("[class*='menu']").first();
                    boolean menuAlreadyOpen = existingMenu.count() > 0 && existingMenu.isVisible();
                    
                    if (menuAlreadyOpen) {
                        System.out.println("  ✓ Menu is already open, skipping click...");
                    } else {
                        // Try clicking the control div specifically for React-Select
                        Locator control = wrapper.locator("[class*='control'], [class*='css-'][class*='-control']").first();
                        if (control.count() > 0) {
                            System.out.println("  🎯 Clicking React-Select control...");
                            control.click(new Locator.ClickOptions().setTimeout(5000));
                        } else {
                            System.out.println("  🎯 Clicking dropdown wrapper...");
                            wrapper.click(new Locator.ClickOptions().setTimeout(5000).setForce(true));
                        }
                        
                        System.out.println("  ⏳ Clicked dropdown, waiting for options menu...");
                        Thread.sleep(500); // Wait for animation
                        
                        // Wait for menu container to appear
                        Locator menu = wrapper.locator("[class*='menu']").first();
                        if (menu.count() > 0) {
                            menu.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                            System.out.println("  ✅ Dropdown menu container appeared");
                        } else {
                            // Fallback: wait for any option elements to appear
                            System.out.println("  ⏳ No menu container found, waiting for options...");
                            page.locator("[id*='react-select'][id*='option'], div[class*='option'], [role='option']")
                                .first()
                                .waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
                            System.out.println("  ✅ Options appeared");
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("❌ Failed to open dropdown or menu didn't appear: " + e.getMessage());
                    System.err.println("   Attempting to continue anyway...");
                    // Don't fail immediately, try to find options anyway
                }
            } else {
                System.out.println("  ✓ Multiselect menu already open, skipping click...");
                Thread.sleep(300); // Small delay for stability
            }
            
            // Step 2: Find and click the option (same logic for all selections)
            return selectOptionFromOpenMenu(page, optionText, label);
            
        } catch (Exception e) {
            System.err.println("❌ FAILED: Custom dropdown interaction failed. Error: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("  ✓ Option '" + optionText + "' is already selected, skipping...");
            return true; // Already selected, no need to select again
        }
        
        Locator option = null;
        
        // Try multiple strategies in order of specificity
        
        // STRATEGY 1: React-Select with ID pattern (most reliable for React-Select)
        String reactSelectIdPattern = String.format("div[id^='react-select-'][id*='-option-']:has-text(\"%s\")", optionText);
        option = page.locator(reactSelectIdPattern).first();
        if (option.count() > 0 && option.isVisible()) {
            System.out.println("  🎯 Found option using React-Select ID pattern");
            option.click();
            System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
            return true;
        }
        System.out.println("  ⏭️  React-Select ID pattern didn't match");
        
        // STRATEGY 2: React-Select with class pattern
        String reactSelectClassPattern = String.format("div[class*='option']:has-text(\"%s\")", optionText);
        option = page.locator(reactSelectClassPattern).first();
        if (option.count() > 0 && option.isVisible()) {
            System.out.println("  🎯 Found option using React-Select class pattern");
            option.click();
            System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
            return true;
        }
        System.out.println("  ⏭️  React-Select class pattern didn't match");
        
        // STRATEGY 3: XPath-based React-Select pattern
        option = page.locator(String.format("//div[starts-with(@id, 'react-select-') and contains(@id, '-option-') and contains(text(), '%s')]", optionText)).first();
        if (option.count() > 0 && option.isVisible()) {
            System.out.println("  🎯 Found option using React-Select XPath pattern");
            option.click();
            System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
            return true;
        }
        System.out.println("  ⏭️  React-Select XPath pattern didn't match");
        
        // STRATEGY 4: Role-based search (ARIA-compliant dropdowns)
        option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION,
                new Page.GetByRoleOptions().setName(optionText)).first();
        if (option.count() > 0 && option.isVisible()) {
            System.out.println("  🎯 Found option using ARIA role");
            option.click();
            System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
            return true;
        }
        System.out.println("  ⏭️  ARIA role pattern didn't match");
        
        // STRATEGY 5: Generic visible text search (last resort)
        option = page.getByText(optionText, new Page.GetByTextOptions().setExact(true)).first();
        if (option.count() > 0 && option.isVisible()) {
            System.out.println("  🎯 Found option using generic visible text search");
            option.click();
            System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
            return true;
        }
        System.out.println("  ⏭️  Generic text search didn't match");
        
        System.err.println("❌ FAILED: Option '" + optionText + "' not found or not visible after opening dropdown");
        System.err.println("   Tried all strategies: React-Select (ID, class, XPath), ARIA, and generic text");
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
            System.out.println("  ✅ Custom dropdown detected");
            
            // For multiselect, check if this option is already selected
            // Selected options appear as chips/tags with class containing 'multiValue'
            Locator selectedChip = wrapper.locator(String.format("div[class*='multiValue']:has-text(\"%s\")", optionText)).first();
            if (selectedChip.count() > 0 && selectedChip.isVisible()) {
                System.out.println("  ✓ Option '" + optionText + "' is already selected in multiselect, skipping...");
                return true;
            }
            
            // Get the wrapper's ID or class to scope our searches
            String wrapperId = (String) wrapper.evaluate("el => el.id || ''");
            String wrapperClass = (String) wrapper.evaluate("el => el.className || ''");
            
            System.out.println("  📋 Wrapper ID: '" + wrapperId + "', Class: '" + wrapperClass + "'");
            
            // Step 1: Click to open dropdown
            try {
                // Try clicking the control div specifically for React-Select
                Locator control = wrapper.locator("[class*='control'], [class*='css-'][class*='-control']").first();
                if (control.count() > 0) {
                    System.out.println("  🎯 Clicking React-Select control...");
                    control.click(new Locator.ClickOptions().setTimeout(5000));
                } else {
                    System.out.println("  🎯 Clicking dropdown wrapper...");
                    wrapper.click(new Locator.ClickOptions().setTimeout(5000).setForce(true));
                }
                
                System.out.println("  ⏳ Clicked dropdown, waiting for options menu...");
                Thread.sleep(500); // Wait for animation
                
                // Wait for menu container to appear within the wrapper (React-Select specific)
                // React-Select creates a menu div inside the wrapper after clicking
                Locator menu = wrapper.locator("[class*='menu']").first();
                if (menu.count() > 0) {
                    menu.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                    System.out.println("  ✅ Dropdown menu container appeared");
                } else {
                    // Fallback: wait for any option elements to appear
                    System.out.println("  ⏳ No menu container found, waiting for options...");
                    page.locator("[id*='react-select'][id*='option'], div[class*='option'], [role='option']")
                        .first()
                        .waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
                    System.out.println("  ✅ Options appeared");
                }
                
            } catch (Exception e) {
                System.err.println("❌ Failed to open dropdown or menu didn't appear: " + e.getMessage());
                System.err.println("   Attempting to continue anyway...");
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
                System.out.println("  🎯 Found option using React-Select ID pattern");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  React-Select ID pattern didn't match");
            
            // STRATEGY 2: React-Select with class pattern
            // Pattern: div[class*="option"]:has-text("...")
            String reactSelectClassPattern = String.format("div[class*='option']:has-text(\"%s\")", optionText);
            option = page.locator(reactSelectClassPattern).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using React-Select class pattern");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  React-Select class pattern didn't match");
            
            // STRATEGY 3: XPath-based React-Select pattern (more flexible text matching)
            option = page.locator(String.format("//div[starts-with(@id, 'react-select-') and contains(@id, '-option-') and contains(text(), '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using React-Select XPath pattern");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  React-Select XPath pattern didn't match");
            
            // STRATEGY 4: Role-based search (ARIA-compliant dropdowns like Material-UI)
            option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION,
                    new Page.GetByRoleOptions().setName(optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using ARIA role");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  ARIA role pattern didn't match");
            
            // STRATEGY 5: Material-UI pattern
            option = page.locator(String.format("//li[@role='option' and contains(., '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using Material-UI pattern");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  Material-UI pattern didn't match");
            
            // STRATEGY 6: Ant Design pattern
            option = page.locator(String.format("//*[contains(@class, 'ant-select-item') and contains(., '%s')]", optionText)).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using Ant Design pattern");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  Ant Design pattern didn't match");
            
            // STRATEGY 7: Generic visible text search (last resort - very broad)
            // Look for any visible element with exact text match
            option = page.getByText(optionText, new Page.GetByTextOptions().setExact(true)).first();
            if (option.count() > 0 && option.isVisible()) {
                System.out.println("  🎯 Found option using generic visible text search");
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            }
            System.out.println("  ⏭️  Generic text search didn't match");
            
            System.err.println("❌ FAILED: Option '" + optionText + "' not found or not visible after opening dropdown");
            System.err.println("   Tried all strategies: React-Select (ID, class, XPath), ARIA, Material-UI, Ant Design, and generic text");
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ FAILED: Custom dropdown interaction failed. Error: " + e.getMessage());
            e.printStackTrace();
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
}
