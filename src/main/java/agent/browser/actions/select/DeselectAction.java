package agent.browser.actions.select;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Handles deselecting options from multiselect dropdowns.
 * For React-Select multiselect, this clicks the "x" button on selected chips/tags.
 */
public class DeselectAction implements BrowserAction {
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String dropdownLabel = plan.getElementName();
        String optionToRemove = plan.getValue();
        
        System.out.println("🗑️ Deselecting option: '" + optionToRemove + "' from dropdown: '" + dropdownLabel + "'");
        
        // Step 1: Find the dropdown wrapper
        Locator dropdownWrapper = locator.waitForSmartElement(dropdownLabel, "select", null);
        
        if (dropdownWrapper == null) {
            System.err.println("❌ FAILED: Dropdown element not found: " + dropdownLabel);
            return false;
        }
        
        // Step 2: Determine if it's a native <select> or custom dropdown
        String tagName = (String) dropdownWrapper.evaluate("el => el.tagName.toLowerCase()");
        
        if ("select".equals(tagName)) {
            // For native multiselect, deselect the option
            return handleNativeDeselect(dropdownWrapper, optionToRemove, dropdownLabel);
        } else {
            // For custom dropdowns (React-Select), click the remove button on the selected chip
            return handleCustomDeselect(page, dropdownWrapper, optionToRemove, dropdownLabel);
        }
    }
    
    /**
     * Deselect option from native HTML <select multiple> element
     */
    private boolean handleNativeDeselect(Locator select, String optionText, String label) {
        try {
            System.out.println("  ✅ Native <select> multiselect detected");
            
            // For native select, we need to find the option and click it to deselect
            // Or use JavaScript to deselect
            Object result = select.evaluate("(el, optionText) => {" +
                "  for (let opt of el.options) {" +
                "    if (opt.text === optionText || opt.value === optionText) {" +
                "      opt.selected = false;" +
                "      return true;" +
                "    }" +
                "  }" +
                "  return false;" +
                "}", optionText);
            
            if (Boolean.TRUE.equals(result)) {
                System.out.println("✅ Deselected '" + optionText + "' from dropdown '" + label + "'");
                return true;
            } else {
                System.err.println("❌ FAILED: Option '" + optionText + "' not found in dropdown '" + label + "'");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ FAILED: Could not deselect option. Error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deselect option from custom dropdown (React-Select multiselect)
     * Strategy: Find the selected chip/tag with the option text and click its "x" button
     */
    private boolean handleCustomDeselect(Page page, Locator wrapper, String optionText, String label) {
        try {
            System.out.println("  ✅ Custom multiselect dropdown detected");
            
            // Get the wrapper's ID or class to scope our searches
            String wrapperId = (String) wrapper.evaluate("el => el.id || ''");
            String wrapperClass = (String) wrapper.evaluate("el => el.className || ''");
            
            System.out.println("  📋 Wrapper ID: '" + wrapperId + "', Class: '" + wrapperClass + "'");
            
            // Strategy 1: React-Select multiselect uses chips/tags with remove buttons
            // The structure is typically: <div class="*-multiValue"><div>OptionText</div><div class="*-multiValueRemove">×</div></div>
            
            // Find the chip/tag containing the option text
            String multiValueSelector = String.format("div[class*='multiValue']:has-text(\"%s\")", optionText);
            Locator chip = wrapper.locator(multiValueSelector).first();
            
            if (chip.count() > 0 && chip.isVisible()) {
                System.out.println("  🎯 Found selected chip for '" + optionText + "'");
                
                // Find the remove button within this chip
                // React-Select uses various patterns: svg with data-* attributes, divs with remove classes, or clickable elements
                Locator removeButton = chip.locator("svg, div[role='button'], *[aria-label*='remove'], *[class*='Remove'], *[class*='remove'], *[class*='clear']").first();
                
                if (removeButton.count() > 0) {
                    System.out.println("  🗑️ Clicking remove button...");
                    removeButton.click(new Locator.ClickOptions().setTimeout(5000));
                    
                    // Wait a longer moment for the chip to be removed and dropdown to stabilize
                    Thread.sleep(800);
                    
                    System.out.println("✅ Deselected '" + optionText + "' from dropdown '" + label + "'");
                    return true;
                } else {
                    // Maybe the entire chip is clickable? Try clicking the chip itself
                    System.out.println("  🗑️ No specific remove button found, trying to click chip...");
                    chip.click(new Locator.ClickOptions().setTimeout(5000));
                    Thread.sleep(800);
                    System.out.println("✅ Deselected '" + optionText + "' from dropdown '" + label + "'");
                    return true;
                }
            }
            
            // Strategy 2: Try finding by exact text match with remove button nearby
            Locator chipByText = wrapper.locator(String.format("//*[contains(@class, 'multi') and contains(text(), '%s')]/..", optionText)).first();
            if (chipByText.count() > 0) {
                Locator removeBtn = chipByText.locator("*[contains(@class, 'remove')]").first();
                if (removeBtn.count() > 0) {
                    System.out.println("  🗑️ Found remove button via XPath, clicking...");
                    removeBtn.click();
                    Thread.sleep(300);
                    System.out.println("✅ Deselected '" + optionText + "' from dropdown '" + label + "'");
                    return true;
                }
            }
            
            // Strategy 3: Generic approach - find any element with the option text and a remove button
            Locator anyChip = page.locator(String.format("div:has-text(\"%s\")", optionText)).first();
            if (anyChip.count() > 0 && anyChip.isVisible()) {
                // Look for SVG close icon or remove div
                Locator genericRemove = anyChip.locator("svg, div[role='button']").last();
                if (genericRemove.count() > 0) {
                    System.out.println("  🗑️ Found generic remove button, clicking...");
                    genericRemove.click();
                    Thread.sleep(300);
                    System.out.println("✅ Deselected '" + optionText + "' from dropdown '" + label + "'");
                    return true;
                }
            }
            
            System.err.println("❌ FAILED: Could not find remove button for option '" + optionText + "'");
            System.err.println("   The option might not be selected, or the structure is different than expected.");
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ FAILED: Custom dropdown deselect failed. Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
