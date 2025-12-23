package agent;

import agent.browser.BrowserService;
import agent.feature.FeatureReader;
import agent.planner.ActionPlan;
import agent.planner.SmartStepParser;
import agent.utils.LoggerUtil;

import java.util.List;

public class AgentApplication {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(AgentApplication.class);
    
    public static void main(String[] args) throws Exception {
        logger.info("Simple Test Agent started...");

        FeatureReader reader = new FeatureReader();
        SmartStepParser planner = new SmartStepParser();
        BrowserService browserService = new BrowserService();

        List<String> steps = reader.readSteps("src/main/resources/features/Alerts.feature");

        browserService.startBrowser();

        int totalSteps = steps.size();
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        boolean shouldContinue = true;
        
        try {
            for (String step : steps) {
                if (!shouldContinue) {
                    skipped++;
                    logger.warn("⏭️  SKIPPED: {}", step);
                    continue;
                }
                
                ActionPlan plan = planner.parseStep(step);
                logger.debug(plan.toString());
                
                // Check if this is a composite action plan
                if (plan instanceof agent.planner.CompositeActionPlan) {
                    agent.planner.CompositeActionPlan compositePlan = (agent.planner.CompositeActionPlan) plan;
                    boolean allSubActionsSucceeded = true;
                    
                    logger.info("  🔄 Executing {} sub-actions...", compositePlan.getSubActionCount());
                    
                    // Execute each sub-action sequentially
                    int subIndex = 1;
                    for (ActionPlan subAction : compositePlan.getSubActions()) {
                        logger.info("    ▶️  Sub-action {}/{}: {}", 
                            subIndex, compositePlan.getSubActionCount(), subAction.getActionType());
                        
                        boolean subSuccess = browserService.executeAction(subAction);
                        
                        if (subSuccess) {
                            logger.success("    Sub-action {} succeeded", subIndex);
                        } else {
                            logger.failure("    Sub-action {} failed", subIndex);
                            allSubActionsSucceeded = false;
                            break; // Stop executing remaining sub-actions on first failure
                        }
                        subIndex++;
                    }
                    
                    if (allSubActionsSucceeded) {
                        passed++;
                        logger.success("  All sub-actions completed successfully");
                    } else {
                        failed++;
                        shouldContinue = false;
                        logger.error("\n❌ EXECUTION STOPPED: Composite action failed, skipping remaining steps\n");
                    }
                } else {
                    // Regular single action
                    boolean success = browserService.executeAction(plan);
                    
                    if (success) {
                        passed++;
                    } else {
                        failed++;
                        // Stop execution on first failure to prevent cascading errors
                        shouldContinue = false;
                        logger.error("\n❌ EXECUTION STOPPED: Step failed, skipping remaining steps to prevent uncontrolled loop\n");
                    }
                }
            }
        } catch (Throwable e) {
            logger.error("Critical Error (Agent Crash): {}", e.getMessage(), e);
            shouldContinue = false;
        } finally {
            browserService.closeBrowser();
            logger.summary("EXECUTION SUMMARY", totalSteps, passed, failed, skipped);
        }
    }
}
