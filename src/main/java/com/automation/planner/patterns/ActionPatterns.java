package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;
import com.automation.planner.PatternRegistry.TablePatternRegistrar;

public class ActionPatterns {
    
    public static void register(PatternRegistrar register) {
        // Autocomplete
        register.add("fill_autocomplete", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:enter|type|select)s?\\s+(.+?)\\s+[\"']([^\"']+)[\"']\\s+from\\s+(?:suggestion|dropdown|list|autocomplete)$", 
            1, 2, -1);
        
        // Date Selection
        register.add("select_date_relative", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*selects?\\s+(.+?)\\s+(\\d+)\\s+days?\\s+from\\s+(today|tomorrow)$", 
            1, 2, -1);

        register.add("set_date",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:set|select|enter)\\s+(?:the\\s+)?(?:date\\s+)?[\"']([^\"']+)[\"']\\s+(?:in|for|into|to)\\s+(?:the\\s+)?(?:date\\s+picker|field|input)?\\s*[\"']?([^\"']+)[\"']?$",
            2, 1, -1);
        
        register.add("set_date",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:set|select|enter)\\s+(?:the\\s+)?(?:date\\s+)?(?:of\\s+)?(today|tomorrow|yesterday)\\s+(?:in|for|into|to)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?$",
            2, 1, -1);

        // Selection
        register.add("select_with_criteria", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*select\\s+(?:the\\s+)?(.+?)\\s+from\\s+[\"']([^\"']+)[\"']\\s+with\\s+(.+?)\\s+[\"']([^\"']+)[\"']$", 
            1, 2, -1);

        // Toggle
        register.add("toggle_setting", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(enable|disable)\\s+(.+?)(?:\\s+in\\s+(.+))?$", 
            2, 1, -1);

        // Fill / Input
        register.add("fill", 
            "(?i)^(?:I|user|we|he|she|they)?\\s*(?:enter|fill|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        register.add("fill", 
            "(?i)^(?:I|user|we|he|she|they)?\\s*(?:enter|fill|type|input)\\s+(?:given\\s+value|the\\s+text|the\\s+value|given\\s+text|value|text)?\\s*[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        register.add("fill", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:fill|enter|type|input|set|update|change)\\s+(?:the\\s+)?(.+?)\\s+with\\s+(?:given\\s+value|the\\s+text|the\\s+value|given\\s+text|value|text)?\\s*[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        register.add("fill", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:fill|enter|type|input|write)\\s+(?:the\\s+)?([\\w\\s\\-]+?)(?:[: ])?\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);

        // Dropdown / Menu Select
        register.add("select_menu", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose|click|navigate\\s+to|open)\\s+[\"']?([^\"']+)[\"']?\\s+(?:from|in|using|via)\\s+(?:the\\s+)?(?:navigation\\s+|sidebar\\s+|top\\s+)?(?:menu|navbar|nav|menu\\s*bar)$", 
            -1, 1, -1);

        register.add("select_multi", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose)\\s+[\"']([^\"']+)[\"'](?:\\s+and\\s+[\"'][^\"']+[\"'])+\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        register.add("select", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        register.add("select", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+(?:from|in|for)\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        register.add("select", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:set|change)\\s+(?:the\\s+)?(?:dropdown\\s+|select\\s+)?[\"']?([^\"']+)[\"']?\\s+to\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);

        // Multiselect / Deselect
        register.add("multiselect_item", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*select\\s+(?:multiple\\s+items\\s+|items\\s+)?[\"']([^\"']+)[\"'](?:\\s+from\\s+(?:list|grid|table))?$", 
            -1, 1, -1);

        register.add("multiselect_item", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose)\\s+(?:multiple\\s+items\\s+)?[\"']([^\"']+)[\"'](?:\\s+from\\s+(?:the\\s+)?(?:list|grid))?$", 
            -1, 1, -1);

        register.add("deselect", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:deselect|remove|unselect|clear)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);

        // Checkbox
        register.add("check", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:check|tick|mark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("uncheck", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:uncheck|untick|unmark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);

        // Click
        register.add("double_click", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*double\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("right_click", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*right\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("click", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:click|tap|press|hit)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);

        register.add("click", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:click|press|tap)(?:\\s+on)?\\s+(.+?)$", 
            1, -1, -1);
    }

    public static void registerTable(TablePatternRegistrar register) {
        // Multiselect Lists
        register.add("multiselect_item",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose|pick)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in)\\s+(?:the\\s+)?(list|grid)",
            java.util.Map.of("value", 1));
        
        register.add("multiselect_item",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:select|choose|pick)\\s+(?:multiple\\s+)?(?:items?|values?)\\s+[\"']([^\"']+)[\"']",
            java.util.Map.of("value", 1));

        // Deselect
        register.add("deselect",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:remove|deselect|clear|delete)\\s+[\"']([^\"']+)[\"']\\s+from\\s+(.+?)$",
            java.util.Map.of("value", 1, "elementName", 2));
    }
}
