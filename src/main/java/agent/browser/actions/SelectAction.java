package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.browser.locator.TableNavigator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

/**
 * Handles dropdown/select interactions
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
                System.err.println("FAILURE: Row not found for anchor: " + plan.getRowAnchor());
                return false;
            }
        }

        System.out.println("Analyzing DOM for dropdown: '" + dropdownLabel + "'");
        
        // Find the select element or dropdown wrapper using smart locator
        Locator dropdown = locator.waitForSmartElement(dropdownLabel, "select", scope);
        
        if (dropdown != null) {
            String tagName = (String) dropdown.evaluate("el => el.tagName.toLowerCase()");
            
            if ("select".equals(tagName)) {
                return handleStandardSelect(dropdown, optionText, dropdownLabel);
            } else {
                return handleCustomDropdown(page, dropdown, optionText, dropdownLabel);
            }
        } else {
            System.err.println("❌ FAILED: Dropdown element not found: " + dropdownLabel);
            return false;
        }
    }

    private boolean handleStandardSelect(Locator select, String optionText, String label) {
        try {
            System.out.println("  > Detected standard <select> element.");
            // Playwright's selectOption handles values, labels, and indices
            java.util.List<String> result = select.selectOption(new SelectOption().setLabel(optionText));
            
            if (result.size() > 0) {
                System.out.println("✅ Selected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            } else {
                // Fallback to value if label didn't match anything
                result = select.selectOption(new SelectOption().setValue(optionText));
                if (result.size() > 0) {
                    System.out.println("✅ Selected '" + optionText + "' (by value) from dropdown '" + label + "'");
                    return true;
                }
            }
            
            System.err.println("❌ FAILED: Could not find option '" + optionText + "' in dropdown '" + label + "'");
            return false;
        } catch (Exception e) {
            System.err.println("❌ FAILED: Could not select '" + optionText + "' in standard select. Error: " + e.getMessage());
            return false;
        }
    }

    private boolean handleCustomDropdown(Page page, Locator dropdown, String optionText, String label) {
        System.out.println("  > Detected custom dropdown (React-select/div). Attempting click-and-select...");
        try {
            // 1. Click the dropdown to open it
            dropdown.click();
            
            // 2. Wait a moment for options to appear
            Thread.sleep(500);
            
            // 3. Find the option text on the page and click it
            // We search globally or within the dropdown's parent to avoid capturing background text
            Locator option = page.getByRole(com.microsoft.playwright.options.AriaRole.OPTION, 
                                          new Page.GetByRoleOptions().setName(optionText)).first();
            
            if (option.count() == 0) {
                // Fallback: search for any element containing the text that is likely an option
                option = page.locator("//*[text()='" + optionText + "' or contains(text(), '" + optionText + "')]").first();
            }

            if (option.count() > 0 && option.isVisible()) {
                option.click();
                System.out.println("✅ Selected '" + optionText + "' from custom dropdown '" + label + "'");
                return true;
            } else {
                System.err.println("❌ FAILED: Custom dropdown opened but could not find option text: '" + optionText + "'");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FAILED: Custom dropdown interaction failed. Error: " + e.getMessage());
            return false;
        }
    }
}
