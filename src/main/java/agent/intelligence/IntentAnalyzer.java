package agent.intelligence;

import agent.utils.LoggerUtil;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent step analyzer that extracts intent from natural language.
 * 
 * Understands:
 * - Action verbs (click, fill, verify, select, etc.)
 * - Target elements (button, field, link, etc.)
 * - Values to input
 * - Spatial relationships (next to, below, inside)
 * - Visual attributes (blue, large, bold)
 * - Ordinal positions (first, second, last)
 */
public class IntentAnalyzer {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(IntentAnalyzer.class);
    
    // Action verb mappings (LinkedHashMap maintains insertion order for predictable matching)
    private static final Map<String, ActionType> ACTION_VERBS = new LinkedHashMap<>();
    private static final Set<String> SYNONYM_GROUPS = new HashSet<>();
    
    static {
        // ========================================
        // CLICK ACTIONS (30+ variants)
        // ========================================
        ACTION_VERBS.put("click", ActionType.CLICK);
        ACTION_VERBS.put("press", ActionType.CLICK);
        ACTION_VERBS.put("tap", ActionType.CLICK);
        ACTION_VERBS.put("hit", ActionType.CLICK);
        ACTION_VERBS.put("push", ActionType.CLICK);
        ACTION_VERBS.put("select", ActionType.CLICK);
        ACTION_VERBS.put("choose", ActionType.CLICK);
        ACTION_VERBS.put("activate", ActionType.CLICK);
        ACTION_VERBS.put("trigger", ActionType.CLICK);
        ACTION_VERBS.put("invoke", ActionType.CLICK);
        ACTION_VERBS.put("execute", ActionType.CLICK);
        ACTION_VERBS.put("submit", ActionType.CLICK);
        ACTION_VERBS.put("apply", ActionType.CLICK);
        
        // Modal/Dialog actions
        ACTION_VERBS.put("close", ActionType.CLICK);
        ACTION_VERBS.put("dismiss", ActionType.CLICK);
        ACTION_VERBS.put("cancel", ActionType.CLICK);
        ACTION_VERBS.put("reject", ActionType.CLICK);
        ACTION_VERBS.put("deny", ActionType.CLICK);
        
        // Toggle/Switch actions
        ACTION_VERBS.put("toggle", ActionType.CLICK);
        ACTION_VERBS.put("switch", ActionType.CLICK);
        ACTION_VERBS.put("enable", ActionType.CLICK);
        ACTION_VERBS.put("disable", ActionType.CLICK);
        
        // Expand/Collapse actions
        ACTION_VERBS.put("expand", ActionType.CLICK);
        ACTION_VERBS.put("collapse", ActionType.CLICK);
        ACTION_VERBS.put("show", ActionType.CLICK);
        ACTION_VERBS.put("hide", ActionType.CLICK);
        ACTION_VERBS.put("reveal", ActionType.CLICK);
        ACTION_VERBS.put("unfold", ActionType.CLICK);
        ACTION_VERBS.put("fold", ActionType.CLICK);
        
        // ========================================
        // FILL/INPUT ACTIONS (30+ variants)
        // ========================================
        ACTION_VERBS.put("fill", ActionType.FILL);
        ACTION_VERBS.put("enter", ActionType.FILL);
        ACTION_VERBS.put("type", ActionType.FILL);
        ACTION_VERBS.put("input", ActionType.FILL);
        ACTION_VERBS.put("write", ActionType.FILL);
        ACTION_VERBS.put("provide", ActionType.FILL);
        ACTION_VERBS.put("set", ActionType.FILL);
        ACTION_VERBS.put("populate", ActionType.FILL);
        ACTION_VERBS.put("insert", ActionType.FILL);
        ACTION_VERBS.put("add", ActionType.FILL);
        ACTION_VERBS.put("specify", ActionType.FILL);
        ACTION_VERBS.put("supply", ActionType.FILL);
        ACTION_VERBS.put("key", ActionType.FILL);
        ACTION_VERBS.put("put", ActionType.FILL);
        
        // Update/Modify actions
        ACTION_VERBS.put("update", ActionType.FILL);
        ACTION_VERBS.put("modify", ActionType.FILL);
        ACTION_VERBS.put("change", ActionType.FILL);
        ACTION_VERBS.put("edit", ActionType.FILL);
        ACTION_VERBS.put("amend", ActionType.FILL);
        ACTION_VERBS.put("revise", ActionType.FILL);
        
        // Clear/Remove actions
        ACTION_VERBS.put("clear", ActionType.FILL);
        ACTION_VERBS.put("erase", ActionType.FILL);
        ACTION_VERBS.put("delete", ActionType.FILL);
        ACTION_VERBS.put("remove", ActionType.FILL);
        ACTION_VERBS.put("empty", ActionType.FILL);
        
        // Copy/Paste actions
        ACTION_VERBS.put("paste", ActionType.FILL);
        ACTION_VERBS.put("copy", ActionType.FILL);
        
        // Upload actions
        ACTION_VERBS.put("upload", ActionType.FILL);
        ACTION_VERBS.put("attach", ActionType.FILL);
        
        // ========================================
        // VERIFY/CHECK ACTIONS (25+ variants)
        // ========================================
        ACTION_VERBS.put("verify", ActionType.VERIFY);
        ACTION_VERBS.put("check", ActionType.VERIFY);
        ACTION_VERBS.put("assert", ActionType.VERIFY);
        ACTION_VERBS.put("validate", ActionType.VERIFY);
        ACTION_VERBS.put("confirm", ActionType.VERIFY);
        ACTION_VERBS.put("ensure", ActionType.VERIFY);
        ACTION_VERBS.put("see", ActionType.VERIFY);
        ACTION_VERBS.put("find", ActionType.VERIFY);
        ACTION_VERBS.put("expect", ActionType.VERIFY);
        ACTION_VERBS.put("observe", ActionType.VERIFY);
        ACTION_VERBS.put("notice", ActionType.VERIFY);
        ACTION_VERBS.put("should", ActionType.VERIFY);
        ACTION_VERBS.put("must", ActionType.VERIFY);
        
        // Display/Show verification
        ACTION_VERBS.put("display", ActionType.VERIFY);
        ACTION_VERBS.put("displays", ActionType.VERIFY);
        ACTION_VERBS.put("shown", ActionType.VERIFY);
        ACTION_VERBS.put("showing", ActionType.VERIFY);
        ACTION_VERBS.put("visible", ActionType.VERIFY);
        
        // Content verification
        ACTION_VERBS.put("contain", ActionType.VERIFY);
        ACTION_VERBS.put("contains", ActionType.VERIFY);
        ACTION_VERBS.put("include", ActionType.VERIFY);
        ACTION_VERBS.put("includes", ActionType.VERIFY);
        ACTION_VERBS.put("match", ActionType.VERIFY);
        ACTION_VERBS.put("matches", ActionType.VERIFY);
        ACTION_VERBS.put("equal", ActionType.VERIFY);
        ACTION_VERBS.put("equals", ActionType.VERIFY);
        
        // Inspection
        ACTION_VERBS.put("inspect", ActionType.VERIFY);
        ACTION_VERBS.put("examine", ActionType.VERIFY);
        ACTION_VERBS.put("review", ActionType.VERIFY);
        ACTION_VERBS.put("test", ActionType.VERIFY);
        
        // ========================================
        // SELECT/CHOOSE ACTIONS (15+ variants)
        // ========================================
        ACTION_VERBS.put("pick", ActionType.SELECT);
        ACTION_VERBS.put("opt", ActionType.SELECT);
        ACTION_VERBS.put("decide", ActionType.SELECT);
        ACTION_VERBS.put("mark", ActionType.SELECT);
        ACTION_VERBS.put("designate", ActionType.SELECT);
        
        // Checkbox/Radio specific (Note: 'check' is in VERIFY, context determines usage)
        ACTION_VERBS.put("uncheck", ActionType.SELECT);
        ACTION_VERBS.put("tick", ActionType.SELECT);
        ACTION_VERBS.put("untick", ActionType.SELECT);
        
        // Dropdown/List specific
        ACTION_VERBS.put("dropdown", ActionType.SELECT);
        ACTION_VERBS.put("deselect", ActionType.SELECT);
        ACTION_VERBS.put("filter", ActionType.SELECT);
        ACTION_VERBS.put("sort", ActionType.SELECT);
        
        // ========================================
        // NAVIGATION ACTIONS (15+ variants)
        // ========================================
        ACTION_VERBS.put("navigate", ActionType.NAVIGATE);
        ACTION_VERBS.put("goto", ActionType.NAVIGATE);
        ACTION_VERBS.put("go", ActionType.NAVIGATE);
        ACTION_VERBS.put("open", ActionType.NAVIGATE);
        ACTION_VERBS.put("visit", ActionType.NAVIGATE);
        ACTION_VERBS.put("access", ActionType.NAVIGATE);
        ACTION_VERBS.put("load", ActionType.NAVIGATE);
        ACTION_VERBS.put("browse", ActionType.NAVIGATE);
        ACTION_VERBS.put("reach", ActionType.NAVIGATE);
        ACTION_VERBS.put("launch", ActionType.NAVIGATE);
        ACTION_VERBS.put("start", ActionType.NAVIGATE);
        ACTION_VERBS.put("redirect", ActionType.NAVIGATE);
        ACTION_VERBS.put("transfer", ActionType.NAVIGATE);
        
        // ========================================
        // WAIT ACTIONS (10+ variants)
        // ========================================
        ACTION_VERBS.put("wait", ActionType.WAIT);
        ACTION_VERBS.put("pause", ActionType.WAIT);
        ACTION_VERBS.put("delay", ActionType.WAIT);
        ACTION_VERBS.put("hold", ActionType.WAIT);
        ACTION_VERBS.put("sleep", ActionType.WAIT);
        ACTION_VERBS.put("idle", ActionType.WAIT);
        ACTION_VERBS.put("rest", ActionType.WAIT);
    }
    
    /**
     * Analyze a natural language step and extract intent
     */
    public StepIntent analyzeStep(String step) {
        logger.debug("Analyzing step: {}", step);
        
        String cleanStep = cleanStep(step);
        
        StepIntent intent = new StepIntent();
        intent.setOriginalStep(step);
        intent.setCleanStep(cleanStep);
        
        // Extract action type
        ActionType actionType = extractActionType(cleanStep);
        intent.setActionType(actionType);
        
        // Extract target description
        String target = extractTarget(cleanStep, actionType);
        intent.setTargetDescription(target);
        
        // Extract value (for fill/input actions)
        String value = extractValue(cleanStep);
        intent.setValue(value);
        
        // Extract modifiers (spatial, visual, ordinal)
        Map<String, String> modifiers = extractModifiers(cleanStep);
        intent.setModifiers(modifiers);
        
        // Extract element type hints
        String elementType = extractElementType(cleanStep);
        intent.setElementType(elementType);
        
        logger.info("Intent: {} → Target='{}' Value='{}' Type='{}'", 
            actionType, target, value, elementType);
        
        return intent;
    }
    
    /**
     * Clean step text (remove Gherkin keywords, extra spaces)
     */
    private String cleanStep(String step) {
        String cleaned = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;
    }
    
    /**
     * Extract action type from step
     */
    private ActionType extractActionType(String step) {
        String lowerStep = step.toLowerCase();
        
        // PRIORITY 1: Explicit NAVIGATE verbs (prevents URL keywords from interfering)
        // This must come FIRST to handle "navigate to .../checkbox" correctly
        String[] navigationVerbs = {"navigate", "goto", "go to", "open", "visit", "access", "load", "browse"};
        for (String navVerb : navigationVerbs) {
            // Check at start: "navigate to..."
            if (lowerStep.startsWith(navVerb + " ") || lowerStep.startsWith(navVerb + " to ")) {
                return ActionType.NAVIGATE;
            }
            // Check after pronoun: "I navigate to...", "user navigate to...", etc.
            String pronounPattern = ".*(^| )(i|user|we|you|he|she|they) " + navVerb + "( to)? .*";
            if (lowerStep.matches(pronounPattern)) {
                return ActionType.NAVIGATE;
            }
        }
        
        // PRIORITY 2: Special handling for ambiguous verbs
        if (lowerStep.contains("check")) {
            // "check checkbox" OR "check the box" → SELECT
            if (lowerStep.contains("checkbox") || 
                lowerStep.contains("check box") ||
                lowerStep.contains("radio")) {
                return ActionType.SELECT;
            }
            // "check that" OR "check if" → VERIFY
            if (lowerStep.contains(" that ") || 
                lowerStep.contains(" if ") ||
                lowerStep.contains(" whether ")) {
                return ActionType.VERIFY;
            }
            // Default "check" → VERIFY (more common)
            if (lowerStep.startsWith("check ") || lowerStep.contains(" check ")) {
                return ActionType.VERIFY;
            }
        }
        
        // PRIORITY 3: Special handling for 'remove' verb (context-sensitive)
        if (lowerStep.contains("remove")) {
            // "remove 'X' from Y" → SELECT (deselect from multiselect/autocomplete)
            if (lowerStep.matches(".*remove.*from.*")) {
                return ActionType.SELECT;
            }
        }

        // PRIORITY 4: Special handling for 'set' verb (context-sensitive for sliders)
        if (lowerStep.contains("set ")) {
            if (lowerStep.contains("slider") || lowerStep.contains("range")) {
                return ActionType.UNKNOWN;
            }
        }

        // PRIORITY 5: Special handling for 'wait' verb (context-sensitive for progress bars)
        if (lowerStep.contains("wait ")) {
            if (lowerStep.contains("progress") || lowerStep.contains("monitoring")) {
                // Let regex patterns handle progress bar synchronization
                return ActionType.UNKNOWN;
            }
        }
        
        // PRIORITY 6: Check for other action verbs (LinkedHashMap maintains insertion order)
        // Longer verbs are checked first for specificity
        for (Map.Entry<String, ActionType> entry : ACTION_VERBS.entrySet()) {
            String verb = entry.getKey();
            
            // Skip navigation verbs (already handled above)
            if (Arrays.asList(navigationVerbs).contains(verb)) {
                continue;
            }
            
            // Match verb at start or with word boundaries
            if (lowerStep.startsWith(verb + " ") || 
                lowerStep.contains(" " + verb + " ") ||
                lowerStep.endsWith(" " + verb)) {
                return entry.getValue();
            }
        }
        
        // Default to CLICK for unknown
        logger.debug("Could not determine action type, defaulting to CLICK");
        return ActionType.CLICK;
    }
    
    /**
     * Extract target element description
     */
    private String extractTarget(String step, ActionType actionType) {
        String target = step;
        
        // Find and remove ONLY the primary action verb that was identified
        // This prevents removing words like "submit" from "submit button"
        String primaryVerb = findPrimaryActionVerb(step);
        if (primaryVerb != null) {
            // Remove only the first occurrence of the primary verb
            target = target.replaceFirst("(?i)\\b" + primaryVerb + "\\b", "").trim();
        }
        
        // For non-FILL/SELECT actions, the quoted content is often the target itself (e.g., click "Submit" button)
        // For FILL/SELECT, the quoted content is the value, so we remove it from the target
        if (actionType == ActionType.FILL || actionType == ActionType.SELECT) {
            target = target.replaceAll("[\"'][^\"']+[\"']", "").trim();
        } else {
            // Strip quotes but keep the text for other actions
            target = target.replaceAll("[\"']([^\"']+)[\"']", "$1").trim();
        }
        
        // Remove container phrases (e.g., "in the left menu", "on the sidebar")
        target = target.replaceAll("(?i)\\b(in|on|within|inside)\\s+(?:the\\s+)?(left\\s+menu|right\\s+menu|sidebar|navbar|header|footer|menu|top\\s+bar|toolbar|main\\s+content)\\b", "").trim();
        
        // Remove common element type suffixes (e.g., "Elements card" -> "Elements", "Submit button" -> "Submit")
        target = target.replaceAll("(?i)\\s+\\b(button|link|card|field|input|checkbox|radio|dropdown|select|tab|menu|sidebar|navbar|header|footer|icon|image|svg|box|panel|item|link)\\b$", "").trim();
        
        // Normalize whitespace (reduce multiple spaces to single space)
        target = target.replaceAll("\\s+", " ").trim();
        
        // For FILL actions, extract text after "in"/"into" BEFORE removing pronouns
        // This is critical because pronouns may appear before "in" (e.g., "I in full name")
        if (actionType == ActionType.FILL) {
            // Pattern: anything + (in|into) + field_name
            Pattern inPattern = Pattern.compile("(?i).+?\\s+(in|into)\\s+(.+)$");
            Matcher matcher = inPattern.matcher(target);
            if (matcher.find()) {
                target = matcher.group(2);  // Get text after "in"
            } else {
                // If no "in/into" pattern, try to remove pronouns
                target = target.replaceAll("(?i)^(I|user|we|you|he|she|they|it|this|that)\\s+", "");
            }
        } else {
            // For non-FILL actions, remove pronouns normally
            target = target.replaceAll("(?i)^(I|user|we|you|he|she|they|it|this|that)\\s+", "");
        }
        
        // Remove common noise words (at start, middle, or end)
        target = target.replaceAll("(?i)^(the|a|an|on|at|to|from|is|are|be|my|your|our|their)\\s+", "");  // At start
        target = target.replaceAll("(?i)\\s+(the|a|an|on|at|to|from|is|are|be)\\s+", " ");  // In middle
        target = target.replaceAll("(?i)\\s+(the|a|an|on|at|to|from|is|are|be)$", "");  // At end
        
        // Remove trailing prepositions
        target = target.replaceAll("(?i)\\s+(with|for|by)\\s*$", "");
        
        return target.trim();
    }
    
    /**
     * Find the primary action verb in the step (the one that determines the action type)
     */
    private String findPrimaryActionVerb(String step) {
        String lowerStep = step.toLowerCase();
        
        // Check for each action verb in order of appearance
        for (Map.Entry<String, ActionType> entry : ACTION_VERBS.entrySet()) {
            String verb = entry.getKey();
            // Look for verb as a whole word
            if (lowerStep.matches(".*\\b" + verb + "\\b.*")) {
                return verb;
            }
        }
        
        return null;
    }
    
    /**
     * Extract value from quoted strings
     */
    private String extractValue(String step) {
        Pattern pattern = Pattern.compile("[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(step);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract modifiers (spatial, visual, ordinal)
     */
    private Map<String, String> extractModifiers(String step) {
        Map<String, String> modifiers = new HashMap<>();
        
        // Container/Scope (e.g., "in the sidebar", "on the left menu")
        Pattern containerPattern = Pattern.compile("(?i)\\b(in|on|within|inside)\\s+(?:the\\s+)?(left\\s+menu|right\\s+menu|sidebar|navbar|header|footer|menu|top\\s+bar|toolbar|main\\s+content)\\b");
        Matcher containerMatcher = containerPattern.matcher(step);
        if (containerMatcher.find()) {
            modifiers.put("container", containerMatcher.group(2).toLowerCase());
        }
        
        // Spatial relationships
        Pattern spatialPattern = Pattern.compile("(?i)(next to|below|above|inside|near|beside|under|over)\\s+(.+?)(?:\\s|$)");
        Matcher spatialMatcher = spatialPattern.matcher(step);
        if (spatialMatcher.find()) {
            modifiers.put("spatial_relation", spatialMatcher.group(1));
            modifiers.put("spatial_reference", spatialMatcher.group(2));
        }
        
        // Visual attributes
        Pattern colorPattern = Pattern.compile("(?i)(red|blue|green|yellow|orange|purple|black|white|gray|grey)\\s");
        Matcher colorMatcher = colorPattern.matcher(step);
        if (colorMatcher.find()) {
            modifiers.put("color", colorMatcher.group(1));
        }
        
        Pattern sizePattern = Pattern.compile("(?i)(large|small|big|tiny|huge)\\s");
        Matcher sizeMatcher = sizePattern.matcher(step);
        if (sizeMatcher.find()) {
            modifiers.put("size", sizeMatcher.group(1));
        }
        
        // Ordinal positions
        Pattern ordinalPattern = Pattern.compile("(?i)(first|second|third|fourth|fifth|last|1st|2nd|3rd|\\d+th)\\s");
        Matcher ordinalMatcher = ordinalPattern.matcher(step);
        if (ordinalMatcher.find()) {
            modifiers.put("position", ordinalMatcher.group(1));
        }
        
        return modifiers;
    }
    
    /**
     * Extract element type hints
     */
    private String extractElementType(String step) {
        String lowerStep = step.toLowerCase();
        
        // Check for explicit type mentions (ordered by specificity)
        String[] types = {
            // Form elements
            "button", "link", "field", "input", "checkbox", "radio", 
            "dropdown", "select", "textarea", "label", "image", "icon",
            
            // Interactive elements (NEW for 122 verbs)
            "toggle", "switch", "slider", "accordion", "tooltip",
            
            // Layout elements
            "menu", "tab", "modal", "dialog", "popup", "form",
            "sidebar", "navbar", "footer", "header", "panel",
            
            // Modern UI components
            "chip", "badge", "card", "breadcrumb", "notification",
            "alert", "banner", "carousel", "spinner"
        };
        
        for (String type : types) {
            if (lowerStep.contains(type)) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * Action type enumeration
     */
    public enum ActionType {
        CLICK,
        FILL,
        VERIFY,
        SELECT,
        NAVIGATE,
        WAIT,
        UNKNOWN
    }
}
