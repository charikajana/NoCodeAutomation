package com.automation.planner.patterns;

import com.automation.planner.PatternRegistry.PatternRegistrar;
import com.automation.planner.PatternRegistry.TablePatternRegistrar;
import java.util.Map;

public class InteractionPatterns {
    
    public static void register(PatternRegistrar register) {
        // Scroll Patterns
        register.add("scroll", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:scroll|move)\\s+(?:to\\s+)?(top|bottom|middle|start|end)(?:\\s+(?:of|on)\\s+(?:the\\s+)?(?:page|screen|window))?$", 
            -1, 1, -1);

        register.add("scroll", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:scroll|move)\\s+(down|up|left|right)(?:\\s+by)?\\s+(\\d+)(?:\\s*(?:px|pixels?))?", 
            1, 2, -1);

        register.add("scroll", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:scroll|move)\\s+(?:to\\s+)?(top|bottom|left|right|down|up)(?:\\s+of|\\s+in|\\s+inside)\\s+[\"']?([^\"']+)[\"']?$", 
            2, 1, -1);

        register.add("scroll", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:scroll|move)\\s+(?:to|into|until)\\s+(?:view\\s+of\\s+)?(?:the\\s+)?[\"']?(.+?)[\"']?$", 
            1, -1, -1);

        // Keyboard Actions
        register.add("press_key", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:press|hit|type)\\s+(Escape|Enter|Tab|Space|Delete|Backspace|ArrowUp|ArrowDown|ArrowLeft|ArrowRight)(?:\\s+key)?(?:\\s+to\\s+.+)?$", 
            1, -1, -1);
        
        register.add("refresh_page", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:refresh|reload)(?:\\s+(?:the|current))?\\s+(?:page|browser|screen)$", 
            -1, -1, -1);
        
        register.add("browser_back", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:go|navigate|move|browser|click|press)?\\s*(?:browser\\s*)?back(?:wards?)?\\s*(?:button|icon)?$", 
            -1, -1, -1);
        
        register.add("browser_forward", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:go|navigate|move|browser|click|press)?\\s*(?:browser\\s*)?forward(?:s)?\\s*(?:button|icon)?$", 
            -1, -1, -1);

        // Alert handling (PatternRegistrar phrasings)
        register.add("verify_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|assert)\\s+(?:alert|popup|dialog)\\s+(?:says|shows|displays|contains|has|message)\\s+[\"']([^\"']+)[\"']", 
            -1, 1, -1);
        
        register.add("accept_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:accept|ok|confirm|close)\\s+(?:alert|popup|dialog)(?:\\s+with)?(?:\\s+message)?(?:\\s+)?[\"']?([^\"']*)[\"']?$", 
            -1, 1, -1);
        
        register.add("accept_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify\\s+and\\s+)?(?:accept|ok|confirm)\\s+(?:alert|popup|dialog)\\s+(?:with|having)?\\s+[\"']([^\"']+)[\"']", 
            -1, 1, -1);
        
        register.add("accept_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:accept|ok|click\\s+ok|confirm)\\s+(?:confirm|confirmation)", 
            -1, -1, -1);
        
        register.add("dismiss_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:dismiss|cancel|close|reject)\\s+(?:alert|confirm|popup|dialog|confirmation)", 
            -1, -1, -1);
        
        register.add("prompt_alert", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:enter|type|input|provide)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|to)\\s+(?:prompt|input\\s+dialog)", 
            -1, 1, -1);
        
        register.add("dismiss_prompt", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:dismiss|cancel|close)\\s+(?:prompt|input\\s+dialog)", 
            -1, -1, -1);

        // Utilities
        register.add("screenshot", 
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:take|capture)\\s+(?:a\\s+)?(?:screen\\s?shot|snap\\s?shot)", 
            -1, -1, -1);
    }

    public static void registerTable(TablePatternRegistrar register) {
        // Slider
        register.add("set_slider",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:set|move|adjust|slide|drag|change)\\s+" +
            "(?:(?:the|a|an)\\s+)?" +
            "((?:.+?\\s+)?(?:slider|range|volume|brightness|zoom|percentage|offset|pos|position|level|intensity|value|speed|rate|progress))\\s*" +
            "(?:slider)?\\s*" +
            "(?:to|at)\\s+" +
            "[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "value", 2));

        // Mouse Actions
        register.add("hover",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:hover|mouse\\s*over|move\\s+mouse\\s+to|point\\s+to|focus\\s+on|places?\\s+cursor\\s+on)\\s+(?:over\\s+|to\\s+)?(?:the\\s+)?" +
            "[\"']?([^\"']+)[\"']?",
            Map.of("elementName", 1));

        register.add("verify_tooltip",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:verify|check)\\s+tooltip\\s+(?:of|for)\\s+" +
            "[\"']?([^\"']+)[\"']?\\s+" +
            "(?:contains|is)\\s+" +
            "[\"']([^\"']+)[\"']",
            Map.of("elementName", 1, "value", 2));
        
        register.add("verify_tooltip",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:verify|check)\\s+tooltip\\s+" +
            "[\"']([^\"']+)[\"']\\s+" +
            "(?:appears|is displayed|is visible|shows|is shown)",
            Map.of("value", 1));
        
        register.add("verify_tooltip",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*" +
            "(?:verify|check)\\s+tooltip\\s+" +
            "(?:disappears|is hidden|is not visible|hides|is not displayed)",
            Map.of("value", ""));

        // Alert (Table Registrar)
        register.add("accept_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:accept|click ok on|confirm|click ok|ok)\\s+(?:the\\s+)?(?:alert|confirm|dialog)",
            Map.of());
        
        register.add("accept_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with\\s+message|saying|that says)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        register.add("accept_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check|assert)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:says|message is|contains|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        register.add("accept_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:verify|check)\\s+(?:and\\s+)?(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with|saying|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
            
        register.add("dismiss_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:dismiss|cancel|close|click cancel|click cancel on)\\s+(?:the\\s+)?(?:alert|confirm|dialog|popup alert)",
            Map.of());
            
        register.add("prompt_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:enter|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|to)\\s+(?:the\\s+)?prompt",
            Map.of("value", 1));

        register.add("prompt_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:accept|confirm|click ok on)\\s+(?:the\\s+)?prompt",
            Map.of());
            
        register.add("prompt_alert",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:dismiss|cancel)\\s+(?:the\\s+)?prompt",
            Map.of());

        // Browser lifecycle
        register.add("close_browser",
            "(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:close|quit|exit)\\s+(?:the\\s+)?browser",
            Map.of());
    }
}
