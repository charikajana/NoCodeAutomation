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
        // CATEGORY 1: TABLE STRUCTURE VALIDATION
        addTablePattern("table_visible", 
            "^(?:given|then)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+(?:is\\s+)?(?:visible|displayed|present)",
            Map.of("tableName", 1));
            
        addTablePattern("table_has_column",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+(?:have|contain)\\s+column\\s+[\"']([^\"']+)[\"']",
            Map.of("tableName", 1, "columnName", 2));
            
        addTablePattern("table_row_count",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+have\\s+(?:at least|exactly|at most)\\s+(\\d+)\\s+rows?",
            Map.of("tableName", 1, "rowCount", 2));
        
        // CATEGORY 2: ROW LEVEL OPERATIONS
        addTablePattern("row_exists",
            "^then\\s+a\\s+row\\s+should\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
            
        addTablePattern("row_not_exists",
            "^then\\s+a\\s+row\\s+should\\s+not\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
        
        // Pattern for: "Verify New Row is added with 'value' in 'column' column"
        addTablePattern("row_added_with_value",
            "^(?:then\\s+)?verify\\s+(?:new\\s+)?row\\s+is\\s+(?:added|created|inserted)\\s+with\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+column",
            Map.of("value", 1, "columnName", 2));
            
        addTablePattern("row_cell_validation",
            "^then\\s+in\\s+the\\s+row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"'],\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2, "targetColumn", 3, "expectedValue", 4));
        
        // CATEGORY 3: CELL VALIDATION
        addTablePattern("cell_value_by_position",
            "^then\\s+(?:the\\s+)?cell\\s+at\\s+row\\s+(\\d+)\\s+and\\s+column\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("rowNumber", 1, "columnName", 2, "expectedValue", 3));
        
        // CATEGORY 4: ROW ACTIONS
        addTablePattern("click_in_row",
            "^when\\s+(?:user\\s+)?clicks?\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
        
        // Pattern for: "click one/on/the Edit Icon in the row where 'First Name' is 'John'"
        addTablePattern("click_specific_in_row",
            "^(?:when|and)?\\s*clicks?\\s+(?:on|one|the|a|an)?\\s*(.+?)\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
            
        addTablePattern("select_checkbox_in_row",
            "^when\\s+(?:user\\s+)?selects?\\s+(?:the\\s+)?checkbox\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        // DATA EXTRACTION: Get all column values from a row
        addTablePattern("get_row_values",
            "^(?:when|then|and)?\\s*(?:get|extract|retrieve|fetch)\\s+all\\s+(?:column\\s+)?values?\\s+(?:from\\s+(?:the\\s+)?row\\s+)?where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        // ROW VALIDATION: Verify row does NOT exist
        addTablePattern("verify_row_not_exists",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?row\\s+should\\s+not\\s+(?:be\\s+)?(?:present|exist)\\s+where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));

        // CATEGORY: BROWSER LIFECYCLE
        addTablePattern("close_browser",
            "^(?:close|quit|exit)\\s+(?:the\\s+)?browser",
            Map.of());
        
        // CATEGORY: WINDOW/TAB MANAGEMENT
        addTablePattern("switch_to_new_window",
            "^(?:switch|navigate|go)\\s+to\\s+(?:the\\s+)?(?:new|second|latest)\\s+(?:window|tab)",
            Map.of());
        
        addTablePattern("switch_to_main_window",
            "^(?:switch|navigate|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main|first|original|parent)\\s+(?:window|tab)",
            Map.of());
        
        addTablePattern("close_current_window",
            "^(?:close|shut)\\s+(?:the\\s+)?(?:current|this)\\s+(?:window|tab)",
            Map.of());
        
        addTablePattern("close_window",
            "^(?:close|shut)\\s+(?:the\\s+)?(?:second|new|latest)\\s+(?:window|tab)",
            Map.of());

        // CATEGORY: ALERT HANDLING
        // Accept alert/confirm - simple
        addTablePattern("accept_alert",
            "^(?:accept|click ok on|confirm|click ok|ok)\\s+(?:the\\s+)?(?:alert|confirm|dialog)",
            Map.of());
        
        // Accept alert with message verification - "with message"
        addTablePattern("accept_alert",
            "^(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with\\s+message|saying|that says)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        // Verify alert message - "Verify alert says"
        addTablePattern("accept_alert",
            "^(?:verify|check|assert)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:says|message is|contains|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        // Verify and accept alert - "Verify and accept alert"
        addTablePattern("accept_alert",
            "^(?:verify|check)\\s+(?:and\\s+)?(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with|saying|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
            
        // Dismiss confirm dialog
        addTablePattern("dismiss_alert",
            "^(?:dismiss|cancel|click cancel|click cancel on)\\s+(?:the\\s+)?(?:alert|confirm|dialog)",
            Map.of());
            
        // Handle prompt - accept with text
        addTablePattern("prompt_alert",
            "^(?:enter|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|to)\\s+(?:the\\s+)?prompt",
            Map.of("value", 1));

        // CATEGORY: IFRAME MANAGEMENT
        addTablePattern("switch_to_frame",
            "^(?:given|when|then|and|but)?\\s*(?:switch|focus|go)\\s+to\\s+(?:the\\s+)?(?:iframe|frame)\\s+[\"']?([^\"']+)[\"']?",
            Map.of("frameName", 1));
            
        addTablePattern("switch_to_main_frame",
            "^(?:given|when|then|and|but)?\\s*(?:switch|focus|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main\\s+content|top\\s+frame|parent\\s+frame)",
            Map.of());
            
        // Handle prompt - just accept
        addTablePattern("prompt_alert",
            "^(?:accept|confirm|click ok on)\\s+(?:the\\s+)?prompt",
            Map.of());
            
        // Handle prompt - dismiss
        addTablePattern("prompt_alert",
            "^(?:dismiss|cancel)\\s+(?:the\\s+)?prompt",
            Map.of("elementName", "dismiss"));


        
        // CATEGORY 5: SORTING
        addTablePattern("sort_table",
            "^when\\s+(?:user\\s+)?sorts?\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+by\\s+column\\s+[\"']([^\"']+)[\"']\\s+in\\s+(ascending|descending)\\s+order",
            Map.of("tableName", 1, "columnName", 2, "sortOrder", 3));
            
        addTablePattern("verify_sort",
            "^then\\s+(?:the\\s+)?[\"']([^\"']+)[\"']\\s+column\\s+should\\s+be\\s+sorted\\s+in\\s+(ascending|descending)\\s+order",
            Map.of("columnName", 1, "sortOrder", 2));
        
        // CATEGORY 6: FILTERING
        addTablePattern("filter_table",
            "^when\\s+(?:user\\s+)?filters?\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+by\\s+[\"']([^\"']+)[\"']\\s+with\\s+value\\s+[\"']([^\"']+)[\"']",
            Map.of("tableName", 1, "columnName", 2, "filterValue", 3));
            
        addTablePattern("search_table",
            "^when\\s+(?:user\\s+)?searches?\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table",
            Map.of("searchValue", 1, "tableName", 2));
        
        // CATEGORY 7: PAGINATION
        addTablePattern("navigate_to_page",
            "^when\\s+(?:user\\s+)?navigates?\\s+to\\s+page\\s+(\\d+)\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table",
            Map.of("pageNumber", 1, "tableName", 2));
        
        // CATEGORY 8: BULK ACTIONS
        addTablePattern("select_all_rows",
            "^when\\s+(?:user\\s+)?selects?\\s+all\\s+rows\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table",
            Map.of("tableName", 1, "bulkAction", "SELECT_ALL"));
            
        addTablePattern("deselect_all_rows",
            "^when\\s+(?:user\\s+)?deselects?\\s+all\\s+rows\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table",
            Map.of("tableName", 1, "bulkAction", "DESELECT_ALL"));
        
        // CATEGORY 9: EMPTY STATES
        addTablePattern("verify_empty_state",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+(?:show|display)\\s+(?:empty state message|no records found|\"([^\"']+)\")",
            Map.of("tableName", 1, "emptyMessage", 2));
            
        addTablePattern("verify_loading",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+(?:show|display)\\s+loading\\s+indicator",
            Map.of("tableName", 1));
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
            logger.info("🔗 Detected Combined Action Step");
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
        logger.error("❌ Could not parse step: {}", step);
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
            
            logger.info("📦 Detected Frame Scoping: '{}'", frameName);
            
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
        String[] actionKeywords = {"enter", "click", "select", "type", "fill", "choose", "check", "uncheck"};
        
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
            "(?i)(enter|click|select|type|fill|choose|check|uncheck).*?" +  // First action
            "\\s+(?:and|also|then|,|&)\\s+" +  // Delimiter
            "(enter|click|select|type|fill|choose|check|uncheck)",  // Second action
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
        
        logger.info("📋 Split into {} sub-actions", subActions.size());
        
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
