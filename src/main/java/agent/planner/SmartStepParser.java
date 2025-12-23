package agent.planner;

import agent.utils.LoggerUtil;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Smart Step Parser that handles 100+ table step variations using:
 * 1. Rule-based regex patterns (fast path)
 * 2. Intent classification (medium path) 
 * 3. Optional LLM fallback (future)
 */
public class SmartStepParser {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(SmartStepParser.class);
    
    private final StepPlanner legacyPlanner;
    private final Map<String, List<TableStepPattern>> tablePatterns;
    
    public SmartStepParser() {
        this.legacyPlanner = new StepPlanner();
        this.tablePatterns = new HashMap<>();
        initializeTablePatterns();
    }
    
    private void initializeTablePatterns() {
       PatternRegistry.registerTablePatterns(this::addTablePattern);
    }
    
    private void addTablePattern(String actionType, String regex, Map<String, Object> groupMapping) {
        tablePatterns.computeIfAbsent(actionType, k -> new ArrayList<>())
            .add(new TableStepPattern(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), groupMapping));
    }
    
    /**
     * Main parsing method - tries multiple strategies
     */
    public ActionPlan parseStep(String step) {
        // STRATEGY 0: Check if this is a combined action step (contains multiple actions)
        if (isCombinedAction(step)) {
            logger.info("Detected Combined Action Step");
            return parseCombinedActions(step);
        }

        // STRATEGY 1: Check for frame-scoped actions ("In iframe 'x', click 'y'")
        ActionPlan frameScopedPlan = tryFrameScoping(step);
        if (frameScopedPlan != null) {
            return frameScopedPlan;
        }
        
        // STRATEGY 2: Try table-specific patterns first (new features)
        ActionPlan tablePlan = tryTablePatterns(step);
        if (tablePlan != null) {
            logger.success("Matched via Table/Frame Pattern: {}", tablePlan.getActionType());
            return tablePlan;
        }
        
        // STRATEGY 3: Fall back to legacy patterns (existing features)
        ActionPlan legacyPlan = legacyPlanner.plan(step);
        if (legacyPlan != null && !"unknown".equals(legacyPlan.getActionType())) {
            logger.success("Matched via Legacy Pattern: {}", legacyPlan.getActionType());
            return legacyPlan;
        }
        
        // STRATEGY 4: Try intent-based fuzzy matching
        ActionPlan fuzzyPlan = tryIntentClassification(step);
        if (fuzzyPlan != null) {
            logger.warning("Matched via Fuzzy Intent: {}", fuzzyPlan.getActionType());
            return fuzzyPlan;
        }
        
        // STRATEGY 5: LLM Fallback (future - would call OpenAI API here)
        logger.error("Could not parse step: {}", step);
        return createUnknownPlan(step);
    }
    
    /**
     * Handles patterns like "In iframe 'frame1', Enter 'John' in 'First Name'"
     */
    private ActionPlan tryFrameScoping(String step) {
        Pattern p = Pattern.compile("^(?i)(?:given|when|then|and|but)?\\s*(?:in|within|inside)\\s+(?:the\\s+)?(?:iframe|frame)\\s+[\"']?([^\"']+)[\"']?[\\s,]+(.+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(step);
        
        if (m.find()) {
            String frameName = m.group(1);
            String remainingStep = m.group(2).trim();
            
            logger.info("Detected Frame Scoping: '{}'", frameName);
            
            // Parse the remaining part as a normal step
            ActionPlan innerPlan = parseStep(remainingStep);
            
            // Set the frame anchor
            innerPlan.setFrameAnchor(frameName);
            return innerPlan;
        }
        return null;
    }

    /**
     * Detects if a step contains multiple actions chained together.
     * Looks for delimiters: "and", "also", "then", ",", "&"
     * 
     * Examples:
     * - "When Enter FirstName and Enter LastName"
     * - "When Enter FirstName also Enter LastName also Enter Email"
     * - "When Enter FirstName, Enter LastName, Enter Email"
     */
    private boolean isCombinedAction(String step) {
        // Remove Gherkin keywords to analyze the actual step content
        String cleanStep = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");
        
        // Define action-related keywords that indicate this is an action step
        // (not a table verification or other complex step)
        String[] actionKeywords = {"enter", "click", "select", "type", "fill", "choose", "check", "uncheck", "close"};
        
        boolean hasActionKeyword = false;
        String lowerStep = cleanStep.toLowerCase();
        for (String keyword : actionKeywords) {
            if (lowerStep.contains(keyword)) {
                hasActionKeyword = true;
                break;
            }
        }
        
        // Only process as combined if it's an action step
        if (!hasActionKeyword) {
            return false;
        }
        
        // Check for delimiters, but be smart about it
        // Pattern: word (delimiter) word (delimiter) word
        // This helps avoid false positives like "First Name and Last Name" (which is a single value)
        
        // Count potential action delimiters
        // Use regex to find patterns like: "Enter X and Enter Y" or "Click X also Click Y"
        Pattern combinedPattern = Pattern.compile(
            "(enter|click|select|type|fill|choose|check|uncheck|close).*?" +  // First action
            "\\s+(?:and|also|then|,|&)\\s+" +  // Delimiter
            "(enter|click|select|type|fill|choose|check|uncheck|close)",  // Second action
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = combinedPattern.matcher(cleanStep);
        boolean isCombined = matcher.find();
        
        if (isCombined) {
            logger.debug("Combined action detected with delimiters: and/also/then/,/&");
        }
        
        return isCombined;
    }
    
    /**
     * Parses a combined action step by splitting it into individual sub-actions.
     * Supports multiple delimiters: "and", "also", "then", ",", "&"
     * 
     * Returns a CompositeActionPlan containing all sub-actions.
     */
    private ActionPlan parseCombinedActions(String step) {
        // Extract the Gherkin keyword (Given/When/Then/And)
        String gherkinKeyword = "";
        Pattern keywordPattern = Pattern.compile("^(Given|When|Then|And|But)\\s+", Pattern.CASE_INSENSITIVE);
        Matcher keywordMatcher = keywordPattern.matcher(step);
        if (keywordMatcher.find()) {
            gherkinKeyword = keywordMatcher.group(1);
        }
        
        // Remove Gherkin keyword for splitting
        String cleanStep = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");
        
        // Split by delimiters while preserving quoted strings
        // This regex splits by: "and", "also", "then", ",", or "&" (with surrounding spaces)
        // But NOT if they're inside quotes
        List<String> subActions = splitByDelimiters(cleanStep);
        
        logger.info("Split into {} sub-actions", subActions.size());
        
        List<ActionPlan> parsedActions = new ArrayList<>();
        
        for (int i = 0; i < subActions.size(); i++) {
            String subAction = subActions.get(i).trim();
            
            // For subsequent actions, prepend "And" if no Gherkin keyword exists
            String fullSubAction = (i == 0 && !gherkinKeyword.isEmpty()) 
                ? gherkinKeyword + " " + subAction 
                : "And " + subAction;
            
            logger.debug("  {}. {}", (i + 1), subAction);
            
            // Recursively parse each sub-action (this will hit the other strategies)
            // Temporarily disable combined action detection to avoid infinite recursion
            ActionPlan subPlan = parseSingleAction(fullSubAction);
            
            if (subPlan != null && !"unknown".equals(subPlan.getActionType())) {
                parsedActions.add(subPlan);
            } else {
                logger.warning("Failed to parse sub-action: {}", subAction);
                // Still add it, but mark as unknown
                parsedActions.add(createUnknownPlan(fullSubAction));
            }
        }
        
        // Create a composite action plan
        CompositeActionPlan compositePlan = new CompositeActionPlan(step, parsedActions);
        logger.success("Created composite plan with {} actions", parsedActions.size());
        
        return compositePlan;
    }
    
    /**
     * Splits a step by delimiters while respecting quoted strings.
     * Delimiters: "and", "also", "then", ",", "&"
     */
    private List<String> splitByDelimiters(String step) {
        List<String> parts = new ArrayList<>();
        
        // Split by common delimiters, but be smart about quotes
        // Use a regex that matches delimiters outside of quotes
        String delimiterRegex = "\\s+(?:and|also|then|,|&)\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        
        String[] rawParts = step.split(delimiterRegex);
        
        for (String part : rawParts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                parts.add(trimmed);
            }
        }
        
        // If no split occurred, return the original step as a single action
        if (parts.isEmpty()) {
            parts.add(step);
        }
        
        return parts;
    }
    
    /**
     * Parse a single action without checking for combined actions.
     * This prevents infinite recursion when parsing sub-actions.
     */
    private ActionPlan parseSingleAction(String step) {
        // Try table patterns
        ActionPlan tablePlan = tryTablePatterns(step);
        if (tablePlan != null) {
            return tablePlan;
        }
        
        // Try legacy patterns
        ActionPlan legacyPlan = legacyPlanner.plan(step);
        if (legacyPlan != null && !"unknown".equals(legacyPlan.getActionType())) {
            return legacyPlan;
        }
        
        // Try intent classification
        ActionPlan fuzzyPlan = tryIntentClassification(step);
        if (fuzzyPlan != null) {
            return fuzzyPlan;
        }
        
        return createUnknownPlan(step);
    }
    
    private ActionPlan tryTablePatterns(String step) {
        String cleanStep = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");
        
        for (Map.Entry<String, List<TableStepPattern>> entry : tablePatterns.entrySet()) {
            String actionType = entry.getKey();
            for (TableStepPattern pattern : entry.getValue()) {
                Matcher m = pattern.regex.matcher(cleanStep);
                if (m.find()) {
                    return buildActionPlan(actionType, step, m, pattern.groupMapping);
                }
            }
        }
        return null;
    }
    
    private ActionPlan buildActionPlan(String actionType, String step, Matcher m, Map<String, Object> mapping) {
        EnhancedActionPlan plan = new EnhancedActionPlan(actionType, step);
        plan.setLocatorStrategy("smart-table");
        
        for (Map.Entry<String, Object> entry : mapping.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof Integer) {
                int groupIndex = (Integer) value;
                String extractedValue = m.group(groupIndex);
                setField(plan, field, extractedValue);
            } else if (value instanceof String) {
                // Static value
                setField(plan, field, (String) value);
            }
        }
        
        // Special handling for click_in_row and click_specific_in_row actions
        // Set rowAnchor to enable row-scoped element finding
        if (("click_in_row".equals(actionType) || "click_specific_in_row".equals(actionType)) 
            && plan.getRowConditionValue() != null) {
            plan.setRowAnchor(plan.getRowConditionValue());
            logger.debug("Setting rowAnchor: {}", plan.getRowConditionValue());
        }
        
        return plan;
    }
    
    private void setField(EnhancedActionPlan plan, String fieldName, String value) {
        if (value == null) return;
        
        switch (fieldName) {
            case "tableName" -> plan.setTableName(value);
            case "columnName" -> plan.setColumnName(value);
            case "conditionColumn" -> plan.setRowConditionColumn(value);
            case "conditionValue" -> plan.setRowConditionValue(value);
            case "targetColumn" -> plan.setTargetColumnName(value);
            case "expectedValue" -> plan.setExpectedValue(value);
            case "value" -> plan.setValue(value);
            case "rowNumber" -> plan.setRowNumber(Integer.parseInt(value));
            case "rowCount" -> plan.setExpectedRowCount(Integer.parseInt(value));
            case "sortOrder" -> plan.setSortOrder(value);
            case "filterValue" -> plan.setFilterValue(value);
            case "pageNumber" -> plan.setPageNumber(Integer.parseInt(value));
            case "bulkAction" -> {
                plan.setIsBulkAction(true);
                plan.setBulkActionType(value);
            }
            case "buttonName", "frameName" -> plan.setElementName(value);
            case "searchValue" -> plan.setValue(value);
        }
    }
    
    /**
     * Intent-based classification using keyword matching
     */
    private ActionPlan tryIntentClassification(String step) {
        String lower = step.toLowerCase();
        
        // Classify by intent keywords
        if (containsAny(lower, "sort", "order by")) {
            return createIntent("sort_table", step, "Detected sorting intent");
        }
        if (containsAny(lower, "filter", "where")) {
            return createIntent("filter_table", step, "Detected filtering intent");
        }
        if (containsAny(lower, "page", "pagination", "navigate")) {
            return createIntent("navigate_to_page", step, "Detected pagination intent");
        }
        if (containsAny(lower, "select all", "deselect all")) {
            return createIntent("bulk_select", step, "Detected bulk selection intent");
        }
        
        return null;
    }
    
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
    
    private ActionPlan createIntent(String actionType, String step, String reason) {
        ActionPlan plan = new ActionPlan(actionType, step);
        plan.setLocatorStrategy("intent-based");
        logger.debug("Reason: {}", reason);
        return plan;
    }
    
    private ActionPlan createUnknownPlan(String step) {
        ActionPlan plan = new ActionPlan("unknown", step);
        plan.setLocatorStrategy("failed");
        return plan;
    }
    
    /**
     * Helper class to store pattern and its group mapping
     */
    private static class TableStepPattern {
        Pattern regex;
        Map<String, Object> groupMapping;
        
        TableStepPattern(Pattern regex, Map<String, Object> groupMapping) {
            this.regex = regex;
            this.groupMapping = groupMapping;
        }
    }
}
