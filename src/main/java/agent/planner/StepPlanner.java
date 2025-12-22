package agent.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepPlanner {

    private final List<StepPattern> patterns = new ArrayList<>();

    public StepPlanner() {

        addPattern("navigate", "^(?:open|go to|navigate to|launch)\\s+(?:the\\s+)?(?:url|website|site|page)?\\s*[\"']?([^\"']+)[\"']?", 1, -1, -1);

        addPattern("screenshot", "^(?:take|capture)\\s+(?:a\\s+)?(?:screen\\s?shot|snap\\s?shot)", -1, -1, -1);

        // WAIT PATTERNS - Multiple variations handled
        // 1. Time-based waits: "wait for 20 seconds", "wait 20 sec", "wait 5s"
        addPattern("wait_time", "^(?:wait|pause)(?:\\s+for)?\\s+(\\d+)\\s*(?:second|sec|s)(?:s)?", 1, -1, -1);
        
        // 2. Wait for element to disappear/hide: "wait for 'Loading' to disappear", "wait until 'spinner' is gone"
        addPattern("wait_disappear", "^(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:disappear|hide|be\\s+hidden|is\\s+gone|vanish|not\\s+visible)", 1, -1, -1);
        
        // 3. Wait for element to appear/be visible: "wait for 'Submit' to appear", "wait until 'Success' is visible"
        addPattern("wait_appear", "^(?:wait|pause)(?:\\s+for|\\s+until)?\\s+[\"']?([^\"']+)[\"']?\\s+(?:to\\s+)?(?:appear|show|be\\s+visible|is\\s+visible|display|be\\s+displayed)", 1, -1, -1);
        
        // 4. Page load waits (multiple variations): "wait for page load", "wait for page to be loaded", "wait for page load completed"
        addPattern("wait_page", "^(?:wait|pause)(?:\\s+for)?\\s+(?:page|network)(?:\\s+to)?(?:\\s+be)?(?:\\s+)?(?:load(?:ed)?(?:\\s+completed)?|idle|ready)", -1, -1, -1);

        // TABLE ACTIONS
        // Click "Edit" in the row identifying "John"
        addPattern("click", "^(?:click|tap)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at|on)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 1, -1, 2);
        
        // Enter "5000" in "Salary" for the row with "John" -- Element="Salary", Value="5000", Anchor="John"
        addPattern("fill", "^(?:enter|fill|type)\\s+[\"']([^\"']+)[\"']\\s+(?:in|into|for)\\s+[\"']([^\"']+)[\"']\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", 2, 1, 3);
        
        // Verify "5000" is displayed in the row for "John" -- Element=null (check text presence in row), Value="5000", Anchor="John"
        addPattern("verify", "^(?:verify|assert)\\s+[\"']([^\"']+)[\"']\\s+is\\s+displayed\\s+(?:in|for|at)\\s+(?:the\\s+)?(?:row|record)\\s+(?:identifying|for|with|containing)\\s+[\"']([^\"']+)[\"']", -1, 1, 2);


        // Additional flexible patterns for various phrasings - MUST BE FIRST for priority
        // "verify 'text' this text present" or "validate 'text' text present"
        addPattern("verify", "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message)\\s+(?:is\\s+)?(?:present|shown|displayed|visible)", -1, 1, -1);
        addPattern("verify", "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:message|text)?\\s*(?:should\\s+be)?\\s*(?:display|displayed|present|shown|visible)", -1, 1, -1);
        
        // NEGATIVE VERIFICATION: Flexible patterns for 'should not be display/displayed'
        addPattern("verify_not", "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:message|text)?\\s*(?:should\\s+not\\s+be|should\\s+not)\\s*(?:display|displayed|present|shown|visible)", -1, 1, -1);
        addPattern("verify_not", "^(?:then\\s+)?(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"']\\s+(?:this\\s+)?(?:text|message)\\s+(?:should\\s+)?not\\s+(?:be\\s+)?(?:display|displayed|present|shown|visible)", -1, 1, -1);

        addPattern("verify", "^(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"'](?:\\s+message)?\\s+is\\s+(?:displayed|visible|present|shown)", -1, 1, -1);

        addPattern("verify_enabled", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?enabled", 1, -1, -1);
        addPattern("verify_disabled", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?disabled", 1, -1, -1);
        
        addPattern("verify_not", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:displayed|visible|present|shown)", 1, 2, -1);
        addPattern("verify", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:displayed|visible|present)", 1, 2, -1);

        addPattern("verify_not", "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?not\\s+(?:displayed|visible|present)(?:\\s+[\"']([^\"']+)[\"'])?", 1, 2, -1);
        addPattern("verify", "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?(?:displayed|visible|present|equals|contains)(?:\\s+[\"']([^\"']+)[\"'])?", 1, 2, -1);

        addPattern("fill", "^(?:enter|fill|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 2, 1, -1);
        addPattern("fill", "^(?:fill|enter|type|input)\\s+(?:the\\s+)?(.+?)\\s+with\\s+[\"']([^\"']+)[\"']", 1, 2, -1);
        addPattern("fill", "^(?:fill|enter|type|input|write)\\s+(?:the\\s+)?([\\w\\s\\-]+?)(?:[: ])?\\s+[\"']([^\"']+)[\"']", 1, 2, -1);

        // DROPDOWN / SELECT PATTERNS
        // Multi-value select (MUST BE FIRST for priority): Select "A" and "B" and "C" from "Dropdown"
        // This pattern captures the first value and dropdown name, then we'll extract all values in post-processing
        addPattern("select_multi", "^(?:select|choose)\\s+[\"']([^\"']+)[\"'](?:\\s+and\\s+[\"'][^\"']+[\"'])+\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 2, 1, -1);
        
        // Single value select patterns
        addPattern("select", "^(?:select|choose)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 2, 1, -1);
        addPattern("select", "^(?:select|choose)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?\\s+(?:from|in|for)\\s+[\"']([^\"']+)[\"']", 1, 2, -1);
        addPattern("select", "^(?:set|change)\\s+(?:the\\s+)?(?:dropdown\\s+|select\\s+)?[\"']?([^\"']+)[\"']?\\s+to\\s+[\"']([^\"']+)[\"']", 1, 2, -1);
        
        // Deselect patterns (for multiselect dropdowns): "deselect 'option' from 'dropdown'"
        addPattern("deselect", "^(?:deselect|remove|unselect|clear)\\s+[\"']([^\"']+)[\"']\\s+(?:from|in|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 2, 1, -1);

        addPattern("check", "^(?:check|tick|mark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 1, -1, -1);
        addPattern("uncheck", "^(?:uncheck|untick|unmark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 1, -1, -1);

        // CLICK VARIANTS - Must be BEFORE generic click pattern (order matters!)
        addPattern("double_click", "^double\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 1, -1, -1);
        addPattern("right_click", "^right\\s+(?:click|tap)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 1, -1, -1);
        addPattern("click", "^(?:click|tap|press|hit)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 1, -1, -1);
    }

    public ActionPlan plan(String step) {
        step = step.trim();
        ActionPlan plan = null;

        String keyword = extractKeyword(step);
        String cleanStep = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");

        for (StepPattern p : patterns) {
            Matcher m = p.regex.matcher(cleanStep);
            if (m.find()) {
                plan = new ActionPlan(p.actionType, step);
                
                if (p.elementGroup != -1 && m.groupCount() >= p.elementGroup) {
                    plan.setElementName(stripQuotes(m.group(p.elementGroup)));
                }
                
                // Special handling for select_multi: extract ALL quoted values
                if ("select_multi".equals(p.actionType)) {
                    List<String> allValues = extractAllQuoted(cleanStep);
                    if (allValues.size() > 1) {
                        // Remove the last value which is the dropdown name, keep only option values
                        List<String> optionValues = allValues.subList(0, allValues.size() - 1);
                        // Join all values with semicolon separator (safer than comma which may appear in option names)
                        plan.setValue(String.join(";", optionValues));
                    } else if (allValues.size() == 1) {
                        // Fallback - single value (shouldn't match select_multi pattern but just in case)
                        plan.setValue(allValues.get(0));
                    }
                    // Change action type to "select" so SelectAction can handle it
                    plan.setActionType("select");
                } else if (p.valueGroup != -1 && m.groupCount() >= p.valueGroup) {
                    plan.setValue(stripQuotes(m.group(p.valueGroup)));
                }

                if (p.rowAnchorGroup != -1 && m.groupCount() >= p.rowAnchorGroup) {
                    plan.setRowAnchor(stripQuotes(m.group(p.rowAnchorGroup)));
                }

                plan.setLocatorStrategy("regex-smart");
                plan.setKeyword(keyword);
                return plan;
            }
        }

        return fallbackPlan(step, keyword);
    }

    private ActionPlan fallbackPlan(String step, String keyword) {
        String cleanStep = step.replaceAll("^(?i)(Given|When|Then|And|But)\\s+", "");
        String lower = step.toLowerCase();
        ActionPlan plan = new ActionPlan("unknown", step);
        plan.setKeyword(keyword);

        if (lower.contains("click")) {
            plan.setActionType("click");
            String quoted = extractQuoted(step);
            if (quoted != null) {
                plan.setElementName(quoted);
            } else {
                plan.setElementName(cleanStep.replaceAll("^(?i)(?:click|tap|press|hit)(?:\\s+on)?\\s+", "").trim());
            }
        } else if (lower.contains("enter") || lower.contains("fill")) {
            plan.setActionType("fill");
            List<String> q = extractAllQuoted(step);
            if (!q.isEmpty()) plan.setValue(q.get(0)); 
            if (q.size() > 1) plan.setElementName(q.get(1));
        } else if (lower.contains("wait") || lower.contains("load")) {
            plan.setActionType("wait");
        } else if (lower.contains("screen") || lower.contains("shot")) {
            plan.setActionType("screenshot");
        }

        return plan;
    }

    private void addPattern(String action, String regex, int elementGroup, int valueGroup, int rowAnchorGroup) {
        patterns.add(new StepPattern(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), action, elementGroup, valueGroup, rowAnchorGroup));
    }

    private String extractKeyword(String step) {
        String[] keys = {"Given", "When", "Then", "And", "But"};
        for (String k : keys) {
            if (step.trim().matches("(?i)^" + k + " .*")) return k;
        }
        return "And"; 
    }

    private String extractQuoted(String text) {
        Matcher m = Pattern.compile("[\"']([^\"']+)[\"']").matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private String stripQuotes(String text) {
        if (text == null) return null;
        text = text.trim();
        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }

    private List<String> extractAllQuoted(String text) {
        List<String> res = new ArrayList<>();
        Matcher m = Pattern.compile("[\"']([^\"']+)[\"']").matcher(text);
        while (m.find()) res.add(m.group(1));
        return res;
    }

    private static class StepPattern {
        Pattern regex;
        String actionType;
        int elementGroup;
        int valueGroup;
        int rowAnchorGroup;

        StepPattern(Pattern regex, String actionType, int elementGroup, int valueGroup, int rowAnchorGroup) {
            this.regex = regex;
            this.actionType = actionType;
            this.elementGroup = elementGroup;
            this.valueGroup = valueGroup;
            this.rowAnchorGroup = rowAnchorGroup;
        }
    }
}
