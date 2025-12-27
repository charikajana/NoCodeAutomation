package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;
import com.automation.planner.PatternRegistry.TablePatternRegistrar;
import java.util.Map;

public class VerificationPatterns {
    
    public static void register(PatternRegistrar register) {
        // Validation States (High Priority)
        register.add("verify_validation", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|ensure)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+(?:field|box|input)?\\s*(?:is\\s+invalid|has\\s+red\\s+border|shows\\s+error|is\\s+required)$", 
            1, -1, -1);

        // Title & URL
        register.add("verify_page_title", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?page\\s+title\\s+(?:is|contains|equals|has|matches)\\s+[\"']?([^\"']+)[\"']?$", 
            -1, 1, -1);
            
        register.add("verify_url", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure|assert)\\s+(?:that\\s+)?(?:the\\s+)?(?:current\\s+)?URL\\s+(?:contains|is|exactly|is\\s+exactly|starts\\s+with|has|matches|equals)\\s+[\"']?([^\"']+)[\"']?(?:\\s+again|\\s+viewed|\\s+now)?$", 
            -1, 1, -1);
        
        register.add("verify_url", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure|assert)\\s+(?:that\\s+)?(?:the\\s+)?(?:URL\\s+)?(path|domain|hash|parameter|query)\\s+(?:is|contains|equals|matches)\\s+[\"']?([^\"']+)[\"']?(?:\\s+again|\\s+now)?$", 
            -1, 2, -1);

        register.add("verify_url", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure|assert)\\s+(?:that\\s+)?(?:I'm\\s+|I\\s+am\\s+)?(?:back\\s+on\\s+)?(?:the\\s+)?(?:homepage|base\\s+URL|root\\s+URL|start\\s+page)$", 
            -1, -1, -1);

        // Selection & Checked State
        register.add("verify_selected", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure)\\s+(?:the\\s+)?active\\s+menu\\s+item\\s+(?:is|should\\s+be)\\s+[\"']([^\"']+)[\"']$", 
            1, -1, -1);
            
        register.add("verify_selected", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:items\\s+)?[\"']([^\"']+)[\"']\\s+are\\s+(?:selected|checked)$", 
            1, -1, -1);
            
        register.add("verify_selected", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is|should\\s+be|are)\\s+(?:selected|checked)$", 
            1, -1, -1);
            
        register.add("verify_not_selected", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is|should\\s+be|are)\\s+(?:not|un)\\s*(?:selected|checked)$", 
            1, -1, -1);

        // State Verification (Enabled/Disabled)
        register.add("verify_enabled", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+not\\s+disabled|should\\s+be\\s+enabled)", 
            1, -1, -1);
        
        register.add("verify_disabled", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+not\\s+enabled|should\\s+be\\s+disabled)", 
            1, -1, -1);

        register.add("verify_enabled", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:enabled|isEnabled|active|clickable|interactive)", 
            1, -1, -1);
        
        register.add("verify_disabled", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:disabled|isDisabled|greyed out|grayed out|inactive|read-only|readonly|restricted)", 
            1, -1, -1);

        // Selection Revisited
        register.add("verify_not_selected", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?(?:not\\s+selected|not\\s+checked|not\\s+chosen|unchecked|off|should\\s+not\\s+be\\s+selected)", 
            1, -1, -1);

        register.add("verify_selected", 
            "(?i)^(?:then|and|when)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?([^\"']+)\\s+(?:is\\s+)?(?:selected|checked|on|chosen|active|expanded|open)$", 
            1, -1, -1);

        // Visibility Verification (All variations)
        register.add("verify", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|ensure|I\\s+see)\\s+[\"']([^\"']+)[\"'](?:\\s+(?:text|message|label|heading|info|message/text))?\\s*(?:is|are|should\\s+be|should\\s+be\\s+transparently)?\\s*(?:present|shown|displayed|visible|display|be\\s+displayed|be\\s+visible)$", 
            1, -1, -1);
        
        register.add("verify", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|ensure|I\\s+see)\\s+(?:the\\s+)?(?:text|message|label|heading|title|info|content|message/text)\\s+[\"']([^\"']+)[\"'](?:\\s+(?:is|are|should\\s+be)?\\s*(?:present|shown|displayed|visible|display|be\\s+displayed|be\\s+visible))?$", 
            1, -1, -1);

        register.add("verify_not", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:that\\s+)?(?:it\\s+)?(?:is\\s+)?not\\s+(?:displayed|visible|present|shown)$", 
            -1, 1, -1);

        register.add("verify_not", 
           "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:displayed|visible|present|shown)", 
           1, 2, -1);
       
        register.add("verify", 
           "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:displayed|visible|present)", 
           1, 2, -1);
       
        register.add("verify_not", 
           "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|should\\s+be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?not\\s+(?:displayed|visible|present)(?:\\s+[\"']([^\"']+)[\"'])?", 
           1, 2, -1);
       
        register.add("verify", 
           "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check|should\\s+be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?(?:displayed|visible|present|equals|contains)(?:\\s+[\"']([^\"']+)[\"'])?", 
           1, 2, -1);

        register.add("verify_not", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:message|text)?\\s*(?:should\\s+not\\s+be|should\\s+not)\\s*(?:display|displayed|present|shown|visible)", 
            -1, 1, -1);
        
        register.add("verify_not", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message)\\s+(?:should\\s+)?not\\s+(?:be\\s+)?(?:display|displayed|present|shown|visible)", 
            -1, 1, -1);

        register.add("verify", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message|item)\\s+(?:is\\s+)?(?:present|shown|displayed|visible|selected)", 
            -1, 1, -1);

        // Placeholder & Value
        register.add("verify_placeholder", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:with|in|for)\\s+(.+?)\\s+(?:place\\s*holder|placeholder|place holder)$", 
            2, 1, -1);
            
        register.add("verify_placeholder", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+(.+?)\\s+(?:place\\s*holder|placeholder|place holder)\\s+(?:value|text|is)?\\s*[\"']([^\"']+)[\"']$", 
            1, 2, -1);
        
        register.add("verify_value", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|assert|check)\\s+[\"']?([^\"']+)[\"']?\\s+(?:is\\s+filled\\s+in|is|appears\\s+in)\\s+(?:the\\s+)?(.+?)(?:\\s+field|\\s+box|\\s+input)?$", 
            2, 1, -1);

        register.add("verify_selected", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check)\\s+(?:that\\s+)?(?:items?\\s+)?[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+selected$", 
            -1, 1, -1);
    }

    public static void registerTable(TablePatternRegistrar register) {
        // Table Metadata
        register.add("table_visible", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+(?:is\\s+)?(?:visible|displayed|present)",
            Map.of("tableName", 1));
            
        register.add("table_has_column",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+(?:have|contain)\\s+column\\s+[\"']([^\"']+)[\"']",
            Map.of("tableName", 1, "columnName", 2));
            
        register.add("table_row_count",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+have\\s+(?:at least|exactly|at most)\\s+(\\d+)\\s+rows?",
            Map.of("tableName", 1, "rowCount", 2));

        // URL (Table Registrar)
        register.add("verify_url",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:verify|check|assert)\\s+(?:current\\s+|page\\s+)?url\\s+" +
            "(?:exactly\\s+)?(?:matches|is|equals)\\s+" +
            "[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        register.add("verify_url",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:verify|check|assert)\\s+(?:current\\s+|page\\s+)?url\\s+" +
            "contains\\s+" +
            "[\"']([^\"']+)[\"']",
            Map.of("value", 1));

        // Modal (Table Registrar)
        register.add("verify_modal_visible",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?(?:is\\s+)?(?:visible|displayed|present)",
            Map.of());
            
        register.add("verify_modal_visible",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?with\\s+(?:title|header)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:visible|displayed|present)",
            Map.of("value", 1));

        register.add("verify_modal_not_visible",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?(?:is\\s+)?not\\s+(?:visible|displayed|present)",
            Map.of());

        register.add("verify_modal_not_visible",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?with\\s+(?:title|header)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:visible|displayed|present)",
            Map.of("value", 1));

        register.add("close_modal",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:close|dismiss|exit|shut|hide)\\s+(?:the\\s+)?(?:modal|dialog|dialog box|popup)",
            Map.of());

        // Select Verification (Table Registrar)
        register.add("verify_selected",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?\\s+)?[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+selected",
            Map.of("value", 1));
        
        register.add("verify_selected",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?|values?)\\s+[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+selected",
            Map.of("value", 1));
        
        register.add("verify_not_selected",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?\\s+)?[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+not\\s+selected",
            Map.of("value", 1));
    }
}
