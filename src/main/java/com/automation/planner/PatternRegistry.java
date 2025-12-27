package com.automation.planner;

import com.automation.planner.patterns.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Central registry for all step matching patterns.
 * Organized by category for easy maintenance and updates.
 * 
 * Reorganized into specialized classes under com.automation.planner.patterns.
 */
public class PatternRegistry {
    
    /**
     * Registers all patterns by calling the provided registration function.
     * 
     * @param register Function that accepts (actionType, regex, elementGroup, valueGroup, rowAnchorGroup)
     */
    public static void registerAllPatterns(PatternRegistrar register) {
        NavigationPatterns.register(register);
        InteractionPatterns.register(register);  // MOVED UP: Must be before ActionPatterns to avoid click pattern capturing "press" keyword
        ActionPatterns.register(register);
        VerificationPatterns.register(register);
        WaitPatterns.register(register);
        TablePatterns.register(register);
        IntelligencePatterns.register(register);
    }
    
    /**
     * Registers all table and frame-related patterns.
     * These are more complex patterns used by SmartStepParser for table operations.
     * 
     * @param register Function that accepts (actionType, regex, groupMap)
     */
    public static void registerTablePatterns(TablePatternRegistrar register) {
        NavigationPatterns.registerTable(register);
        ActionPatterns.registerTable(register);
        VerificationPatterns.registerTable(register);
        TablePatterns.registerTable(register);
        InteractionPatterns.registerTable(register);
    }
    
    /**
     * Functional interface for basic pattern registration (used by StepPlanner)
     */
    @FunctionalInterface
    public interface PatternRegistrar {
        void add(String actionType, String regex, int elementGroup, int valueGroup, int rowAnchorGroup);
    }
    
    /**
     * Functional interface for table pattern registration (used by SmartStepParser)
     */
    @FunctionalInterface
    public interface TablePatternRegistrar {
        void add(String actionType, String regex, java.util.Map<String, Object> groupMap);
    }
    
    /**
     * Get list of combined action patterns (for step validation)
     */
    public static List<Pattern> getCombinedActions() {
        List<Pattern> patterns = new ArrayList<>();
        // Re-using the logic from NavigationPatterns to maintain consistency
        patterns.add(Pattern.compile("(?i)^(?:given|when|then|and|but)?\\s*(?:I|user|we|he|she|they)?\\s*(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+?)[\"']?\\s*(?:link|button|icon|element|img)?\\s*(?:and|&)\\s*(?:switch|navigate|go)\\s+to\\s+(?:the\\s+)?(?:new|second|latest)\\s+(?:window|tab)"));
        return patterns;
    }
    
    /**
     * Get all registered patterns (for step validation)
     */
    public static Map<String, Pattern> getAllPatterns() {
        Map<String, Pattern> allPatterns = new HashMap<>();
        
        // Collect all patterns by registering them
        registerAllPatterns((actionType, regex, elementGroup, valueGroup, rowAnchorGroup) -> {
            allPatterns.put(actionType + "_" + allPatterns.size(), Pattern.compile(regex));
        });
        
        return allPatterns;
    }
}
