package agent.browser.actions;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Page;

public class NavigateAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String stepText = plan.getTarget();
        
        String url = extractUrl(stepText);
        if (url == null && value != null && value.startsWith("http")) url = value;
        
        if (url != null) {
            System.out.println("Navigating to: " + url);
            page.navigate(url);
        }
        return true;
    }

    private String extractUrl(String stepText) {
        if (stepText.contains("http")) {
            int start = stepText.indexOf("http");
            int end = stepText.indexOf("\"", start);
            if (end == -1) end = stepText.indexOf(" ", start);
            if (end == -1) end = stepText.length();
            return stepText.substring(start, end);
        }
        return null;
    }
}
