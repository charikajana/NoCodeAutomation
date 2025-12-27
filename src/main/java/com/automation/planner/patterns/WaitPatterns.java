package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;

public class WaitPatterns {
    
    public static void register(PatternRegistrar register) {
        // Time-based waits
        register.add("wait_time", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:wait|pause)(?:\\s+for)?\\s+(\\d+)\\s*(?:second|sec|s)(?:s)?", 
            1, -1, -1);
        
        // Element state waits
        register.add("wait_disappear", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:disappear|hide|be\\s+hidden|is\\s+gone|vanish|not\\s+visible)", 
            1, -1, -1);
        
        register.add("wait_appear", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:appear|show|be\\s+visible|is\\s+visible|display|be\\s+displayed)", 
            1, -1, -1);
        
        // Page/Network waits
        register.add("wait_page", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:wait|pause)(?:\\s+for)?\\s+(?:page|network)(?:\\s+to)?(?:\\s+be)?(?:\\s+)?(?:load(?:ed)?(?:\\s+completed)?|idle|ready)", 
            -1, -1, -1);

        // Progress Monitoring
        register.add("wait_for_progress",
            "(?i)(?:wait|pause|monitor).*?(progress|loading).*?(?:until\\s+reach|until|reach|to|is|be)\\s+[\"']([^\"']+)[\"']",
            1, 2, -1);
    }
}
