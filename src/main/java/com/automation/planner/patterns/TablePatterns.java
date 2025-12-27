package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;
import com.automation.planner.PatternRegistry.TablePatternRegistrar;
import java.util.Map;

public class TablePatterns {
    
    public static void register(PatternRegistrar register) {
        // Table-Scoped Actions (Basicphrased)
        register.add("click", 
            "(?i)^(?:when|and|then)?\\s*(?:I|user|we|he|she|they)?\\s*(?:click|tap)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at|on)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            1, -1, 2);
        
        register.add("fill", 
            "(?i)^(?:when|and|then)?\\s*(?:I|user|we|he|she|they)?\\s*(?:enter|fill|type)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|for)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            2, 1, 3);
        
        register.add("verify", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|assert)\\s+[\"']([^\"']+)[\"']\\s+is\\s+displayed\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            -1, 1, 2);
    }

    public static void registerTable(TablePatternRegistrar register) {
        // Row Level Operations
        register.add("row_exists",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*a\\s+row\\s+should\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
            
        register.add("row_not_exists",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*a\\s+row\\s+should\\s+not\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
        
        register.add("row_added_with_value",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*verify\\s+(?:new\\s+)?row\\s+is\\s+(?:added|created|inserted)\\s+with\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+column",
            Map.of("value", 1, "columnName", 2));
            
        register.add("row_cell_validation",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*in\\s+the\\s+row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"'],\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2, "targetColumn", 3, "expectedValue", 4));
        
        register.add("cell_value_by_position",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:the\\s+)?cell\\s+at\\s+row\\s+(\\d+)\\s+and\\s+column\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("rowNumber", 1, "columnName", 2, "expectedValue", 3));

        // Row Actions
        register.add("click_in_row",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*clicks?\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|on)\\s+(?:the\\s+)?row\\s+(?:where|with|having|that\\s+has)\\s+[\"']?([^\"']+?)[\"']?\\s+(?:column\\s+)?(?:value\\s+)?(?:is|=|equals?)\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
        
        register.add("click_specific_in_row",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*clicks?\\s+(?:on|one|the|a|an)?\\s*(.+?)\\s+(?:in|for|on)\\s+(?:the\\s+)?row\\s+(?:where|with|having|that\\s+has)\\s+[\"']?([^\"']+?)[\"']?\\s+(?:column\\s+)?(?:value\\s+)?(?:is|=|equals?)\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
        
        register.add("click_in_row_position",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*clicks?\\s+(?:on|the)?\\s*(.+?)\\s+(?:in|at)\\s+(?:the\\s+)?(first|last|\\d+(?:st|nd|rd|th)?|row\\s+\\d+)\\s+row",
            Map.of("buttonName", 1, "rowPosition", 2));
        
        register.add("direct_row_action",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(edit|delete|remove|update|modify)\\s+(?:the\\s+)?row\\s+(?:where|with|having|that\\s+has)\\s+[\"']?([^\"']+?)[\"']?\\s+(?:column\\s+)?(?:value\\s+)?(?:is|=|equals?)\\s+[\"']([^\"']+)[\"']",
            Map.of("actionType", 1, "conditionColumn", 2, "conditionValue", 3));
            
        register.add("select_checkbox_in_row",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*selects?\\s+(?:the\\s+)?checkbox\\s+(?:in|for|on)\\s+(?:the\\s+)?row\\s+(?:where|with|having|that\\s+has)\\s+[\"']?([^\"']+?)[\"']?\\s+(?:column\\s+)?(?:value\\s+)?(?:is|=|equals?)\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        register.add("get_row_values",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:get|extract|retrieve|fetch)\\s+all\\s+(?:column\\s+)?values?\\s+(?:from\\s+(?:the\\s+)?row\\s+)?(?:where|with|having)\\s+[\"']?([^\"']+)[\"']?\\s+(?:is|=|equals?)\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        register.add("verify_row_not_exists",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?row\\s+should\\s+not\\s+(?:be\\s+)?(?:present|exist)\\s+where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));

        // State verification in rows (Table Registrar)
        register.add("verify_enabled",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+not\\s+disabled|should\\s+be\\s+enabled)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));

        register.add("verify_disabled",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+not\\s+enabled|should\\s+be\\s+disabled)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));

        register.add("verify_enabled",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:enabled|isEnabled|active|clickable|interactive)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));

        register.add("verify_disabled",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:disabled|isDisabled|greyed out|grayed out|inactive|read-only|readonly|restricted)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));

        // Selection verification in rows (Table Registrar)
        register.add("verify_not_selected",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+not\\s+selected|is\\s+not\\s+checked|is\\s+not\\s+chosen|is\\s+unchecked|is\\s+off|should\\s+not\\s+be\\s+selected)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));

        register.add("verify_selected",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:selected|checked|on|chosen|should\\s+be\\s+selected)\\s+in\\s+(?:the\\s+)?(?:row|record)\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "conditionColumn", 2, "conditionValue", 3));
    }
}
