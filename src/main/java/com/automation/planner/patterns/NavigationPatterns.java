package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;
import com.automation.planner.PatternRegistry.TablePatternRegistrar;
import java.util.Map;

public class NavigationPatterns {
    
    public static void register(PatternRegistrar register) {
        // Multi-action: Click and switch
        register.add("click_and_switch_window",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+?)[\"']?\\s*(?:link|button|icon|element|img)?\\s*(?:and|&)\\s*(?:switch|navigate|go)\\s+to\\s+(?:the\\s+)?(?:new|second|latest)\\s+(?:window|tab)",
            1, -1, -1);

        register.add("navigate", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:open|go to|navigate to|launch|visit)\\s+(?:the\\s+)?(?:url|website|site|page)?\\s*[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("navigate_app", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:open|launch)\\s+(?:the\\s+)?browser\\s+and\\s+navigate\\s+to\\s+(.+)$", 
            1, -1, -1);
    }

    public static void registerTable(TablePatternRegistrar register) {
        // Window Management
        register.add("switch_to_new_window",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:switch|navigate|go)\\s+to\\s+(?:the\\s+)?(?:new|second|latest)\\s+(?:window|tab)",
            Map.of());

        register.add("switch_to_main_window",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:switch|navigate|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main\\s+first|original|parent)\\s+(?:window|tab)",
            Map.of());
        
        register.add("close_current_window",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:close|shut)\\s+(?:the\\s+)?(?:current|this)\\s+(?:window|tab)",
            Map.of());
        
        register.add("verify_window_count", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:the\\s+)?(?:current\\s+)?(?:tab|window)\\s+count\\s+(?:is|equals|should\\s+be)\\s+(\\d+)", 
            Map.of("value", 1));
            
        register.add("verify_window_exists", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?(?:original|main|first|parent)\\s+tab\\s+(?:still\\s+)?exists", 
            Map.of());

        register.add("verify_window_exists", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:a\\s+)?(?:new|second|latest)\\s+(?:window|tab)\\s+(?:exists|is\\s+open|opened|created|present)", 
            Map.of());
            
        register.add("close_window",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:close|shut)\\s+(?:the\\s+)?(?:second|new|latest)\\s+(?:window|tab)",
            Map.of());

        // Frame Management
        register.add("switch_to_frame",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:switch|focus|go)\\s+to\\s+(?:the\\s+)?(?:iframe|frame)\\s+[\"']?([^\"']+)[\"']?",
            Map.of("frameName", 1));
            
        register.add("switch_to_main_frame",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:switch|focus|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main\\s+content|top\\s+frame|parent\\s+frame)",
            Map.of());
    }
}
