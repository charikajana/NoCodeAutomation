package agent;

import agent.browser.BrowserService;
import agent.feature.FeatureReader;
import agent.planner.ActionPlan;
import agent.planner.SmartStepParser;

import java.util.List;

public class AgentApplication {
    public static void main(String[] args) throws Exception {
        System.out.println("Simple Test Agent started...");

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
                    System.out.println("⏭️  SKIPPED: " + step);
                    continue;
                }
                
                ActionPlan plan = planner.parseStep(step);
                System.out.println(plan);
                
                // Check if this is a composite action plan
                if (plan instanceof agent.planner.CompositeActionPlan) {
                    agent.planner.CompositeActionPlan compositePlan = (agent.planner.CompositeActionPlan) plan;
                    boolean allSubActionsSucceeded = true;
                    
                    System.out.println("  🔄 Executing " + compositePlan.getSubActionCount() + " sub-actions...");
                    
                    // Execute each sub-action sequentially
                    int subIndex = 1;
                    for (ActionPlan subAction : compositePlan.getSubActions()) {
                        System.out.println("    ▶️  Sub-action " + subIndex + "/" + compositePlan.getSubActionCount() + ": " + subAction.getActionType());
                        
                        boolean subSuccess = browserService.executeAction(subAction);
                        
                        if (subSuccess) {
                            System.out.println("    ✅ Sub-action " + subIndex + " succeeded");
                        } else {
                            System.err.println("    ❌ Sub-action " + subIndex + " failed");
                            allSubActionsSucceeded = false;
                            break; // Stop executing remaining sub-actions on first failure
                        }
                        subIndex++;
                    }
                    
                    if (allSubActionsSucceeded) {
                        passed++;
                        System.out.println("  ✅ All sub-actions completed successfully");
                    } else {
                        failed++;
                        shouldContinue = false;
                        System.err.println("\n❌ EXECUTION STOPPED: Composite action failed, skipping remaining steps\n");
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
                        System.err.println("\n❌ EXECUTION STOPPED: Step failed, skipping remaining steps to prevent uncontrolled loop\n");
                    }
                }
            }
        } catch (Throwable e) {
            System.err.println("Critical Error (Agent Crash): " + e.getMessage());
            e.printStackTrace();
            shouldContinue = false;
        } finally {
            browserService.closeBrowser();

            System.out.println("\n==========================================");
            System.out.println("       EXECUTION SUMMARY");
            System.out.println("==========================================");
            System.out.println("Total Steps : " + totalSteps);
            System.out.println("Passed      : " + passed);
            System.out.println("Failed      : " + failed);
            System.out.println("Skipped     : " + skipped);
            System.out.println("==========================================\n");

           // System.exit(failed > 0 ? 1 : 0);
        }
    }
}
