package agent;

import agent.browser.BrowserService;
import agent.feature.FeatureReader;
import agent.planner.ActionPlan;
import agent.planner.SmartStepParser;
import agent.utils.LoggerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllFeaturesTestRunner {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(AllFeaturesTestRunner.class);
    
    public static void main(String[] args) throws Exception {
        logger.header("🚀 COMPREHENSIVE TEST SUITE EXECUTION");
        
        // Get all feature files
        File featuresDir = new File("src/main/resources/features");
        File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
        
        if (featureFiles == null || featureFiles.length == 0) {
            logger.failure("No feature files found!");
            return;
        }
        
        logger.info("📁 Found {} feature files\n", featureFiles.length);
        
        // Overall statistics
        int totalFeatures = featureFiles.length;
        int passedFeatures = 0;
        int failedFeatures = 0;
        
        List<String> failedFeaturesList = new ArrayList<>();
        
        // Execute each feature file
        for (File featureFile : featureFiles) {
            String featurePath = featureFile.getPath();
            String featureName = featureFile.getName();
            
            logger.header("EXECUTING: " + featureName);
            
            boolean featureSuccess = runFeature(featurePath);
            
            if (featureSuccess) {
                passedFeatures++;
                logger.success("FEATURE PASSED: {}", featureName);
            } else {
                failedFeatures++;
                failedFeaturesList.add(featureName);
                logger.failure("FEATURE FAILED: {}", featureName);
            }
        }
        
        // Print final summary
        logger.info("\n");
        logger.header("COMPREHENSIVE TEST EXECUTION SUMMARY");
        logger.info("📊 FEATURE FILES:");
        logger.info("   Total Features  : {}", totalFeatures);
        logger.info("   Passed Features : {} ✅", passedFeatures);
        logger.info("   Failed Features : {} ❌", failedFeatures);
        logger.info("   Success Rate    : {}%", (passedFeatures * 100 / totalFeatures));
        logger.info("");
        
        if (!failedFeaturesList.isEmpty()) {
            logger.error("❌ FAILED FEATURES:");
            for (String failed : failedFeaturesList) {
                logger.error("   - {}", failed);
            }
            logger.info("");
        }
        
        logger.info("════════════════════════════════════════════════════════════════");
        logger.info("");
        
        // Set exit code
        System.exit(failedFeatures > 0 ? 1 : 0);
    }
    
    private static boolean runFeature(String featurePath) {
        FeatureReader reader = new FeatureReader();
        SmartStepParser planner = new SmartStepParser();
        BrowserService browserService = null;
        
        try {
            List<String> steps = reader.readSteps(featurePath);
            
            browserService = new BrowserService();
            browserService.startBrowser();
            
            int totalSteps = steps.size();
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            boolean shouldContinue = true;
            
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
                    
                    int subIndex = 1;
                    for (ActionPlan subAction : compositePlan.getSubActions()) {
                        logger.step("Sub-action {}/{}: {}", subIndex, compositePlan.getSubActionCount(), subAction.getActionType());
                        
                        boolean subSuccess = browserService.executeAction(subAction);
                        
                        if (subSuccess) {
                            logger.success("Sub-action {} succeeded", subIndex);
                        } else {
                            logger.failure("Sub-action {} failed", subIndex);
                            allSubActionsSucceeded = false;
                            break;
                        }
                        subIndex++;
                    }
                    
                    if (allSubActionsSucceeded) {
                        passed++;
                    } else {
                        failed++;
                        shouldContinue = false;
                    }
                } else {
                    boolean success = browserService.executeAction(plan);
                    
                    if (success) {
                        passed++;
                    } else {
                        failed++;
                        shouldContinue = false;
                    }
                }
            }
            
            logger.section("FEATURE SUMMARY");
            logger.info("  Total Steps : {}", totalSteps);
            logger.info("  Passed      : {}", passed);
            logger.info("  Failed      : {}", failed);
            logger.info("  Skipped     : {}", skipped);
            logger.info("--------------------------------------------------\n");
            
            return failed == 0;
            
        } catch (Exception e) {
            logger.error("Exception in feature execution: {}", e.getMessage(), e);
            return false;
        } finally {
            if (browserService != null) {
                try {
                    browserService.closeBrowser();
                } catch (Exception e) {
                    logger.error("Error closing browser: {}", e.getMessage());
                }
            }
        }
    }
}
