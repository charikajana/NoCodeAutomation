package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;

public class IntelligencePatterns {
    
    public static void register(PatternRegistrar register) {
        // Credentials
        register.add("fill_credentials", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*enters?\\s+username\\s+and\\s+password$", 
            -1, -1, -1);
        
        // Form Sections
        register.add("fill_form_section", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*add\\s+(.+?)\\s+details$", 
            1, -1, -1);
        
        // Context Storage & Retrieval
        register.add("store_context", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*store\\s+(?:the\\s+)?(.+?)(?:\\s+as\\s+(.+))?$", 
            1, 2, -1);
        
        register.add("enter_stored_reference", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*enters?\\s+booking\\s+reference$", 
            -1, -1, -1);

        // Scripting
        register.add("execute_js",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:run script|execute js|execute javascript) \"(.*)\".*",
            -1, 1, -1);
    }
}
