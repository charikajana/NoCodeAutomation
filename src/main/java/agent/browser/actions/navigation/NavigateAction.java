package agent.browser.actions.navigation;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;

public class NavigateAction implements BrowserAction {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(NavigateAction.class);
    
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String stepText = plan.getTarget();
        
        String url = extractUrl(stepText);
        if (url == null && value != null && value.startsWith("http")) url = value;
        
        if (url != null) {
            logger.browserAction("Navigate", url);
            page.navigate(url);
            logger.success("Navigated to: {}", url);
        } else {
            logger.error("No valid URL found in step: {}", stepText);
            return false;
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
