package agent.browser.locator.builders;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * Dynamically detects dropdown type and generates appropriate XPath/selectors
 * Supports: Standard Select, React-Select, Material-UI, Ant Design, ChakraUI, and custom dropdowns
 */
public class DynamicDropdownXPathBuilder {

    public enum DropdownType {
        STANDARD_SELECT,
        REACT_SELECT,
        MATERIAL_UI,
        ANT_DESIGN,
        CHAKRA_UI,
        BOOTSTRAP,
        CUSTOM_DIV
    }

    /**
     * Analyze the dropdown wrapper and determine its type
     */
    public Map<String, Object> analyzeDropdown(Page page, Locator dropdownWrapper) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            String tagName = (String) dropdownWrapper.evaluate("el => el.tagName.toLowerCase()");
            String className = (String) dropdownWrapper.evaluate("el => el.className || ''");
            String role = (String) dropdownWrapper.evaluate("el => el.getAttribute('role') || ''");
            String dataTestId = (String) dropdownWrapper.evaluate("el => el.getAttribute('data-testid') || ''");
            
            metadata.put("tagName", tagName);
            metadata.put("className", className);
            metadata.put("role", role);
            
            DropdownType type = detectType(tagName, className, role, dataTestId);
            metadata.put("type", type);
            
            System.out.println("  🔍 Dropdown Analysis:");
            System.out.println("     Tag: " + tagName);
            System.out.println("     Class: " + className);
            System.out.println("     Role: " + role);
            System.out.println("     Detected Type: " + type);
            
            return metadata;
        } catch (Exception e) {
            System.err.println("❌ Failed to analyze dropdown: " + e.getMessage());
            metadata.put("type", DropdownType.CUSTOM_DIV);
            return metadata;
        }
    }

    /**
     * Detect dropdown framework based on HTML attributes
     */
    private DropdownType detectType(String tag, String className, String role, String dataTestId) {
        // Standard HTML select
        if ("select".equals(tag)) {
            return DropdownType.STANDARD_SELECT;
        }
        
        // React-Select (class contains "react-select" or "__control")
        if (className.contains("react-select") || className.contains("__control") || className.contains("__value-container")) {
            return DropdownType.REACT_SELECT;
        }
        
        // Material-UI (MUI)
        if (className.contains("MuiSelect") || className.contains("MuiInput") || "button".equals(role) && className.contains("Mui")) {
            return DropdownType.MATERIAL_UI;
        }
        
        // Ant Design
        if (className.contains("ant-select") || className.contains("ant-picker")) {
            return DropdownType.ANT_DESIGN;
        }
        
        // ChakraUI
        if (className.contains("chakra-select") || className.contains("css-")) {
            return DropdownType.CHAKRA_UI;
        }
        
        // Bootstrap
        if (className.contains("dropdown") || className.contains("form-select") || className.contains("btn-group")) {
            return DropdownType.BOOTSTRAP;
        }
        
        // Default to custom div-based
        return DropdownType.CUSTOM_DIV;
    }

    /**
     * Build XPath to find the actual interactive element
     */
    public String buildDropdownTriggerXPath(Locator wrapper, DropdownType type) {
        switch (type) {
            case STANDARD_SELECT:
                return "."; // The wrapper itself is the select
                
            case REACT_SELECT:
                // React-Select: look for the input or the control div
                return ".//*[contains(@class, '__control') or contains(@class, '__input')]";
                
            case MATERIAL_UI:
                // MUI: look for the button or input with role
                return ".//*[@role='button' or @role='combobox']";
                
            case ANT_DESIGN:
                // Ant Design: look for selector or input
                return ".//*[contains(@class, 'ant-select-selector')]";
                
            case CHAKRA_UI:
                // ChakraUI: look for select element
                return ".//select";
                
            case BOOTSTRAP:
                // Bootstrap: look for button or select
                return ".//button[@data-toggle='dropdown'] | .//select";
                
            case CUSTOM_DIV:
            default:
                // For custom, try common patterns
                return ".//*[@role='combobox' or @role='button' or @role='listbox']";
        }
    }

    /**
     * Build XPath to find dropdown options after opening
     */
    public String buildOptionsXPath(DropdownType type, String optionText) {
        switch (type) {
            case STANDARD_SELECT:
                return String.format("//option[text()='%s' or @value='%s']", optionText, optionText);
                
            case REACT_SELECT:
                return String.format("//*[contains(@class, '__option') and contains(text(), '%s')]", optionText);
                
            case MATERIAL_UI:
                return String.format("//li[@role='option' and contains(., '%s')]", optionText);
                
            case ANT_DESIGN:
                return String.format("//*[contains(@class, 'ant-select-item') and contains(., '%s')]", optionText);
                
            case CHAKRA_UI:
                return String.format("//option[text()='%s']", optionText);
                
            case BOOTSTRAP:
                return String.format("//a[contains(@class, 'dropdown-item') and contains(., '%s')]", optionText);
                
            case CUSTOM_DIV:
            default:
                // Generic fallback
                return String.format("//*[@role='option' and contains(., '%s')] | " +
                                   "//*[contains(@class, 'option') and contains(., '%s')] | " +
                                   "//li[contains(., '%s')]", 
                                   optionText, optionText, optionText);
        }
    }

    /**
     * Get the interaction strategy for this dropdown type
     */
    public InteractionStrategy getInteractionStrategy(DropdownType type) {
        switch (type) {
            case STANDARD_SELECT:
                return new InteractionStrategy(false, "selectOption"); // Use Playwright's selectOption()
                
            case REACT_SELECT:
            case MATERIAL_UI:
            case ANT_DESIGN:
            case BOOTSTRAP:
            case CHAKRA_UI:
            case CUSTOM_DIV:
            default:
                return new InteractionStrategy(true, "clickAndSelect"); // Click to open, then click option
        }
    }

    /**
     * Interaction strategy metadata
     */
    public static class InteractionStrategy {
        public final boolean requiresClick;
        public final String method;

        public InteractionStrategy(boolean requiresClick, String method) {
            this.requiresClick = requiresClick;
            this.method = method;
        }
    }
}
