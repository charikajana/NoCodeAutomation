package agent.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Central registry for all step matching patterns.
 * Organized by category for easy maintenance and updates.
 * 
 * Each pattern is defined with:
 * - Action type (navigate, fill, click, etc.)
 * - Regex pattern
 * - Group indices for element, value, and row anchor
 */
public class PatternRegistry {
    
    /**
     * Registers all patterns by calling the provided registration function.
     * 
     * @param register Function that accepts (actionType, regex, elementGroup, valueGroup, rowAnchorGroup)
     */
    public static void registerAllPatterns(PatternRegistrar register) {
        
        // ========================================
        // NAVIGATION PATTERNS
        // ========================================
        register.add("navigate", 
            "^(?:open|go to|navigate to|launch)\\s+(?:the\\s+)?(?:url|website|site|page)?\\s*[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        // ========================================
        // SCREENSHOT PATTERNS
        // ========================================
        register.add("screenshot", 
            "^(?:take|capture)\\s+(?:a\\s+)?(?:screen\\s?shot|snap\\s?shot)", 
            -1, -1, -1);
        
        // ========================================
        // WAIT PATTERNS
        // ========================================
        // Time-based waits: "wait for 20 seconds", "wait 20 sec", "wait 5s"
        register.add("wait_time", 
            "^(?:wait|pause)(?:\\s+for)?\\s+(\\d+)\\s*(?:second|sec|s)(?:s)?", 
            1, -1, -1);
        
        // Wait for element to disappear: "wait for 'Loading' to disappear"
        register.add("wait_disappear", 
            "^(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:disappear|hide|be\\s+hidden|is\\s+gone|vanish|not\\s+visible)", 
            1, -1, -1);
        
        // Wait for element to appear: "wait for 'Submit' to appear"
        register.add("wait_appear", 
            "^(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:appear|show|be\\s+visible|is\\s+visible|display|be\\s+displayed)", 
            1, -1, -1);
        
        // Page load waits: "wait for page load", "wait for page to be loaded"
        register.add("wait_page", 
            "^(?:wait|pause)(?:\\s+for)?\\s+(?:page|network)(?:\\s+to)?(?:\\s+be)?(?:\\s+)?(?:load(?:ed)?(?:\\s+completed)?|idle|ready)", 
            -1, -1, -1);
        
        // ========================================
        // TABLE-SCOPED ACTIONS
        // ========================================
        // Click button in row: Click "Edit" in the row identifying "John"
        register.add("click", 
            "^(?:click|tap)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at|on)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            1, -1, 2);
        
        // Fill in row: Enter "5000" in "Salary" for the row with "John"
        register.add("fill", 
            "^(?:enter|fill|type)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|for)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            2, 1, 3);
        
        // Verify in row: Verify "5000" is displayed in the row for "John"
        register.add("verify", 
            "^(?:verify|assert)\\s+[\"']([^\"']+)[\"']\\s+is\\s+displayed\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 
            -1, 1, 2);
        
        // ========================================
        // VERIFICATION PATTERNS (HIGH PRIORITY)
        // ========================================
        // Positive verification with various phrasings
        register.add("verify", 
            "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message)\\s+(?:is\\s+)?(?:present|shown|displayed|visible)", 
            -1, 1, -1);
        
        register.add("verify", 
            "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+(?:the\\s+)?(?:text|message)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:present|shown|displayed|visible)", 
            -1, 1, -1);
        
        register.add("verify", 
            "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:message|text)?\\s*(?:should\\s+be)?\\s*(?:display|displayed|present|shown|visible)", 
            -1, 1, -1);
        
        // Negative verification
        register.add("verify_not", 
            "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:message|text)?\\s*(?:should\\s+not\\s+be|should\\s+not)\\s*(?:display|displayed|present|shown|visible)", 
            -1, 1, -1);
        
        register.add("verify_not", 
            "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message)\\s+(?:should\\s+)?not\\s+(?:be\\s+)?(?:display|displayed|present|shown|visible)", 
            -1, 1, -1);
        
        register.add("verify", 
            "^(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"'](?:\\s+message)?\\s+is\\s+(?:displayed|visible|present|shown)", 
            -1, 1, -1);
        
        register.add("verify_enabled", 
            "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?enabled", 
            1, -1, -1);
        
        register.add("verify_disabled", 
            "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?disabled", 
            1, -1, -1);
        
        register.add("verify_not", 
            "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:displayed|visible|present|shown)", 
            1, 2, -1);
        
        register.add("verify", 
            "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:displayed|visible|present)", 
            1, 2, -1);
        
        register.add("verify_not", 
            "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?not\\s+(?:displayed|visible|present)(?:\\s+[\"']([^\"']+)[\"'])?", 
            1, 2, -1);
        
        register.add("verify", 
            "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?(?:displayed|visible|present|equals|contains)(?:\\s+[\"']([^\"']+)[\"'])?", 
            1, 2, -1);
        
        // ========================================
        // FILL/INPUT PATTERNS
        // ========================================
        // Pattern 1: "Enter 'value' in element" (strict, most common)
        register.add("fill", 
            "^(?:enter|fill|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        // Pattern 2: Natural language with filler words
        // "Enter given value 'X' in element", "Enter the text 'X' in element"
        register.add("fill", 
            "^(?:enter|fill|type|input)\\s+(?:given\\s+value|the\\s+text|the\\s+value|given\\s+text|value|text)?\\s*[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        // Pattern 3: "Fill element with 'value'" with variations
        // Supports: Set/Update/Change element with given value/the text/the value 'X'
        register.add("fill", 
            "^(?:fill|enter|type|input|set|update|change)\\s+(?:the\\s+)?(.+?)\\s+with\\s+(?:given\\s+value|the\\s+text|the\\s+value|given\\s+text|value|text)?\\s*[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        // Pattern 4: Backward compatibility - "Fill element: 'value'" or "Fill element 'value'"
        register.add("fill", 
            "^(?:fill|enter|type|input|write)\\s+(?:the\\s+)?([\\w\\s\\-]+?)(?:[: ])?\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        // ========================================
        // DROPDOWN/SELECT PATTERNS
        // ========================================
        // Multi-value select (MUST BE FIRST): Select "A" and "B" and "C" from "Dropdown"
        register.add("select_multi", 
            "^(?:select|choose)\\s+[\"']([^\"']+)[\"'](?:\\s+and\\s+[\"'][^\"']+[\"'])+\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        // Single value select
        register.add("select", 
            "^(?:select|choose)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        register.add("select", 
            "^(?:select|choose)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+(?:from|in|for)\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        register.add("select", 
            "^(?:set|change)\\s+(?:the\\s+)?(?:dropdown\\s+|select\\s+)?[\"']?([^\"']+)[\"']?\\s+to\\s+[\"']([^\"']+)[\"']", 
            1, 2, -1);
        
        // Deselect patterns (for multiselect dropdowns)
        register.add("deselect", 
            "^(?:deselect|remove|unselect|clear)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            2, 1, -1);
        
        // ========================================
        // CHECKBOX PATTERNS
        // ========================================
        register.add("check", 
            "^(?:check|tick|mark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("uncheck", 
            "^(?:uncheck|untick|unmark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        // ========================================
        // CLICK PATTERNS (order matters - more specific first)
        // ========================================
        register.add("double_click", 
            "^double\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("right_click", 
            "^right\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
        
        register.add("click", 
            "^(?:click|tap|press|hit)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 
            1, -1, -1);
    }
    
    /**
     * Registers all table and frame-related patterns.
     * These are more complex patterns used by SmartStepParser for table operations,
     * window/tab management, alerts, iframes, modals, and multiselect lists.
     * 
     * @param register Function that accepts (actionType, regex, groupMap)
     */
    public static void registerTablePatterns(TablePatternRegistrar register) {
        
        // ========================================
        // TABLE STRUCTURE VALIDATION
        // ========================================
        register.add("table_visible", 
            "^(?:given|then)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+(?:is\\s+)?(?:visible|displayed|present)",
            Map.of("tableName", 1));
            
        register.add("table_has_column",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+(?:have|contain)\\s+column\\s+[\"']([^\"']+)[\"']",
            Map.of("tableName", 1, "columnName", 2));
            
        register.add("table_row_count",
            "^then\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+table\\s+should\\s+have\\s+(?:at least|exactly|at most)\\s+(\\d+)\\s+rows?",
            Map.of("tableName", 1, "rowCount", 2));
        
        // ========================================
        // ROW LEVEL OPERATIONS
        // ========================================
        register.add("row_exists",
            "^then\\s+a\\s+row\\s+should\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
            
        register.add("row_not_exists",
            "^then\\s+a\\s+row\\s+should\\s+not\\s+exist\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("columnName", 1, "value", 2));
        
        register.add("row_added_with_value",
            "^(?:then\\s+)?verify\\s+(?:new\\s+)?row\\s+is\\s+(?:added|created|inserted)\\s+with\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+column",
            Map.of("value", 1, "columnName", 2));
            
        register.add("row_cell_validation",
            "^then\\s+in\\s+the\\s+row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"'],\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2, "targetColumn", 3, "expectedValue", 4));
        
        // ========================================
        // CELL VALIDATION
        // ========================================
        register.add("cell_value_by_position",
            "^then\\s+(?:the\\s+)?cell\\s+at\\s+row\\s+(\\d+)\\s+and\\s+column\\s+[\"']([^\"']+)[\"']\\s+should\\s+be\\s+[\"']([^\"']+)[\"']",
            Map.of("rowNumber", 1, "columnName", 2, "expectedValue", 3));
        
        // ========================================
        // ROW ACTIONS
        // ========================================
        register.add("click_in_row",
            "^when\\s+(?:user\\s+)?clicks?\\s+[\"']([^\"']+)[\"']\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
        
        register.add("click_specific_in_row",
            "^(?:when|and)?\\s*clicks?\\s+(?:on|one|the|a|an)?\\s*(.+?)\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("buttonName", 1, "conditionColumn", 2, "conditionValue", 3));
            
        register.add("select_checkbox_in_row",
            "^when\\s+(?:user\\s+)?selects?\\s+(?:the\\s+)?checkbox\\s+in\\s+(?:the\\s+)?row\\s+where\\s+[\"']([^\"']+)[\"']\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        register.add("get_row_values",
            "^(?:when|then|and)?\\s*(?:get|extract|retrieve|fetch)\\s+all\\s+(?:column\\s+)?values?\\s+(?:from\\s+(?:the\\s+)?row\\s+)?where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));
        
        register.add("verify_row_not_exists",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?row\\s+should\\s+not\\s+(?:be\\s+)?(?:present|exist)\\s+where\\s+[\"']?([^\"']+)[\"']?\\s+is\\s+[\"']([^\"']+)[\"']",
            Map.of("conditionColumn", 1, "conditionValue", 2));

        // ========================================
        // BROWSER LIFECYCLE
        // ========================================
        register.add("close_browser",
            "^(?:close|quit|exit)\\s+(?:the\\s+)?browser",
            Map.of());
        
        // ========================================
        // WINDOW/TAB MANAGEMENT
        // ========================================
        register.add("switch_to_new_window",
            "^(?:switch|navigate|go)\\s+to\\s+(?:the\\s+)?(?:new|second|latest)\\s+(?:window|tab)",
            Map.of());
        
        register.add("switch_to_main_window",
            "^(?:switch|navigate|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main|first|original|parent)\\s+(?:window|tab)",
            Map.of());
        
        register.add("close_current_window",
            "^(?:close|shut)\\s+(?:the\\s+)?(?:current|this)\\s+(?:window|tab)",
            Map.of());
        
        register.add("close_window",
            "^(?:close|shut)\\s+(?:the\\s+)?(?:second|new|latest)\\s+(?:window|tab)",
            Map.of());

        // ========================================
        // ALERT HANDLING
        // ========================================
        register.add("accept_alert",
            "^(?:accept|click ok on|confirm|click ok|ok)\\s+(?:the\\s+)?(?:alert|confirm|dialog)",
            Map.of());
        
        register.add("accept_alert",
            "^(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with\\s+message|saying|that says)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        register.add("accept_alert",
            "^(?:verify|check|assert)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:says|message is|contains|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        register.add("accept_alert",
            "^(?:verify|check)\\s+(?:and\\s+)?(?:accept|confirm)\\s+(?:the\\s+)?(?:alert|confirm)\\s+(?:with|saying|shows)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
            
        register.add("dismiss_alert",
            "^(?:dismiss|cancel|close|click cancel|click cancel on)\\s+(?:the\\s+)?(?:alert|confirm|dialog|popup alert)",
            Map.of());
            
        register.add("prompt_alert",
            "^(?:enter|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|to)\\s+(?:the\\s+)?prompt",
            Map.of("value", 1));

        register.add("prompt_alert",
            "^(?:accept|confirm|click ok on)\\s+(?:the\\s+)?prompt",
            Map.of());
            
        register.add("prompt_alert",
            "^(?:dismiss|cancel)\\s+(?:the\\s+)?prompt",
            Map.of());

        // ========================================
        // IFRAME MANAGEMENT
        // ========================================
        register.add("switch_to_frame",
            "^(?:given|when|then|and|but)?\\s*(?:switch|focus|go)\\s+to\\s+(?:the\\s+)?(?:iframe|frame)\\s+[\"']?([^\"']+)[\"']?",
            Map.of("frameName", 1));
            
        register.add("switch_to_main_frame",
            "^(?:given|when|then|and|but)?\\s*(?:switch|focus|go)\\s+(?:back\\s+)?to\\s+(?:the\\s+)?(?:main\\s+content|top\\s+frame|parent\\s+frame)",
            Map.of());

        // ========================================
        // MODAL DIALOGS
        // ========================================
        register.add("verify_modal_visible",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?(?:is\\s+)?(?:visible|displayed|present)",
            Map.of());
            
        register.add("verify_modal_visible",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?with\\s+(?:title|header)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:visible|displayed|present)",
            Map.of("value", 1));

        register.add("verify_modal_not_visible",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?(?:is\\s+)?not\\s+(?:visible|displayed|present)",
            Map.of());

        register.add("verify_modal_not_visible",
            "^(?:then|and)?\\s*(?:validate|verify|check|ensure)\\s+(?:that\\s+)?(?:the\\s+)?modal\\s+(?:dialog\\s+)?with\\s+(?:title|header)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:visible|displayed|present)",
            Map.of("value", 1));

        register.add("close_modal",
            "^(?:when|and)?\\s*(?:close|dismiss|exit|shut|hide)\\s+(?:the\\s+)?(?:modal|dialog|dialog box|popup)",
            Map.of());

        // ========================================
        // MULTISELECT LISTS
        // ========================================
        register.add("multiselect_item",
            "^(?:when|and)?\\s*(?:select|choose|pick)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in)\\s+(?:the\\s+)?(list|grid)",
            Map.of("value", 1));
        
        register.add("multiselect_item",
            "^(?:when|and)?\\s*(?:select|choose|pick)\\s+(?:multiple\\s+)?(?:items?|values?)\\s+[\"']([^\"']+)[\"']",
            Map.of("value", 1));
        
        // Overly broad pattern - commented out to prevent false matches
        // register.add("multiselect_item",
        //     "^(?:when|and)?\\s*(?:multi-?select|select)\\s+(.+)",
        //     Map.of("value", 1));
        
        register.add("verify_selected",
            "^(?:then|and)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?\\s+)?[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+selected",
            Map.of("value", 1));
        
        register.add("verify_selected",
            "^(?:then|and)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?|values?)\\s+[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+selected",
            Map.of("value", 1));
        
        register.add("verify_not_selected",
            "^(?:then|and)?\\s*(?:verify|check|assert)\\s+(?:that\\s+)?(?:items?\\s+)?[\"']([^\"']+)[\"']\\s+(?:is|are)\\s+not\\s+selected",
            Map.of("value", 1));
        
        // Note: Additional patterns for sorting, filtering, pagination can be added here
        // They follow the same structure as above
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
}
