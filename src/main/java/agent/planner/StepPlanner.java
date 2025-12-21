package agent.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepPlanner {

    private final List<StepPattern> patterns = new ArrayList<>();

    public StepPlanner() {

        addPattern("navigate", "^(?:open|go to|navigate to|launch)\\s+(?:the\\s+)?(?:url|website|site|page)?\\s*[\"']?([^\"']+)[\"']?", 1, -1);

        addPattern("screenshot", "^(?:take|capture)\\s+(?:a\\s+)?(?:screen\\s?shot|snap\\s?shot)", -1, -1);

        addPattern("wait", "^(?:wait\\s+for\\s+)?(?:page\\s+to\\s+load|network\\s+idle|seconds?)", -1, -1);

        addPattern("verify", "^(?:validate|verify|assert|check)\\s+[\"']([^\"']+)[\"'](?:\\s+message)?\\s+is\\s+(?:displayed|visible|present)", -1, 1);

        addPattern("verify_enabled", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?enabled", 1, -1);
        addPattern("verify_disabled", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+)?disabled", 1, -1);
        
        addPattern("verify_not", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?not\\s+(?:displayed|visible|present)", 1, 2);
        addPattern("verify", "^(?:validate|verify|assert|check)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+[\"']([^\"']+)[\"']\\s+(?:is\\s+)?(?:displayed|visible|present)", 1, 2);

        addPattern("verify_not", "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?not\\s+(?:displayed|visible|present)(?:\\s+[\"']([^\"']+)[\"'])?", 1, 2);
        addPattern("verify", "^(?:validate|verify|assert|check|should be)\\s+(?:that\\s+)?(?:the\\s+)?(.+?)\\s+(?:is\\s+|are\\s+)?(?:displayed|visible|present|equals|contains)(?:\\s+[\"']([^\"']+)[\"'])?", 1, 2);

        addPattern("fill", "^(?:enter|fill|type|input)\\s+[\"']([^\"']+)[\"']\\s+(?:into|in|to|for)\\s+(?:the\\s+)?[\"']?([^\"']+)[\"']?", 2, 1);
        addPattern("fill", "^(?:fill|enter|type|input)\\s+(?:the\\s+)?(.+?)\\s+with\\s+[\"']([^\"']+)[\"']", 1, 2);
        addPattern("fill", "^(?:fill|enter|type|input|write)\\s+(?:the\\s+)?([\\w\\s\\-]+?)(?:[: ])?\\s+[\"']([^\"']+)[\"']", 1, 2);

        addPattern("check", "^(?:check|tick|mark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 1, -1);
        addPattern("uncheck", "^(?:uncheck|untick|unmark)\\s+(?:the\\s+)?(?:checkbox\\s+|box\\s+)?[\"']?([^\"']+)[\"']?", 1, -1);

        addPattern("click", "^(?:click|tap|press|hit)\\s+(?:on\\s+)?(?:the\\s+)?[\"']?([^\"']+)[\"']?", 1, -1);
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
                
                if (p.valueGroup != -1 && m.groupCount() >= p.valueGroup) {
                    plan.setValue(stripQuotes(m.group(p.valueGroup)));
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

    private void addPattern(String action, String regex, int elementGroup, int valueGroup) {
        patterns.add(new StepPattern(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), action, elementGroup, valueGroup));
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

        StepPattern(Pattern regex, String actionType, int elementGroup, int valueGroup) {
            this.regex = regex;
            this.actionType = actionType;
            this.elementGroup = elementGroup;
            this.valueGroup = valueGroup;
        }
    }
}
