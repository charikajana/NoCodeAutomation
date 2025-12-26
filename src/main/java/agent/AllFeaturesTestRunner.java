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
        logger.header("COMPREHENSIVE TEST SUITE EXECUTION");
        
        // Get all feature files
        File featuresDir = new File("src/main/resources/features");
        
        // Target specific features
        java.util.Set<String> targetFeatures = java.util.Set.of(
            
            "Alerts.feature",
            "AutoComplete.feature",
            "BookStore.feature",
            "Buttons.feature",
            "CheckBox.feature",
            "CombinedActions.feature",
            "DatePicker.feature",
            "ScrollActions.feature", 
            "Select.feature", 
            "Selectable.feature",
            "Slider.feature"
        );

        File[] featureFiles = featuresDir.listFiles((dir, name) -> 
            name.endsWith(".feature") && targetFeatures.contains(name)
        );
        
        if (featureFiles == null || featureFiles.length == 0) {
            logger.failure("No matching feature files found in: {}", featuresDir.getAbsolutePath());
            return;
        }

        // Sort files to ensure consistent execution order
        java.util.Arrays.sort(featureFiles, (a, b) -> a.getName().compareTo(b.getName()));
        
        logger.info("Found {} feature files\n", featureFiles.length);
        
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
            
            // Small delay between features for system cleanup
            Thread.sleep(1000);
            
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
        logger.info("FEATURE FILES:");
        logger.info("   Total Features  : {}", totalFeatures);
        logger.info("   Passed Features : {}", passedFeatures);
        logger.info("   Failed Features : {}", failedFeatures);
        logger.info("   Success Rate    : {}%", (passedFeatures * 100 / totalFeatures));
        logger.info("");
        
        if (!failedFeaturesList.isEmpty()) {
            logger.error("FAILED FEATURES:");
            for (String failed : failedFeaturesList) {
                logger.error("   - {}", failed);
            }
            logger.info("");
        }
        
        logger.info("==================================================");
        logger.info("");
        
        // Set exit code
        System.exit(failedFeatures > 0 ? 1 : 0);
    }
    
    private static boolean runFeature(String featurePath) {
        FeatureReader reader = new FeatureReader();
        SmartStepParser planner = new SmartStepParser();
        
        // Playwright resources
        com.microsoft.playwright.Playwright playwright = null;
        com.microsoft.playwright.Browser browser = null;
        com.microsoft.playwright.Page page = null;
        
        try {
            List<String> steps = reader.readSteps(featurePath);
            
            // Initialize browser
            playwright = com.microsoft.playwright.Playwright.create();
            browser = playwright.chromium().launch(new com.microsoft.playwright.BrowserType.LaunchOptions().setHeadless(false));
            page = browser.newPage();
            page.setDefaultTimeout(120000);
            page.setDefaultNavigationTimeout(120000);
            
            // Create services with Page instance
            agent.browser.SmartLocator smartLocator = new agent.browser.SmartLocator(page);
            BrowserService browserService = new BrowserService(page, smartLocator);
            
        int totalSteps = steps.size();
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        boolean shouldContinue = true;
        
        for (String step : steps) {
            if (!shouldContinue) {
                skipped++;
                logger.warn("SKIPPED: {}", step);
                continue;
            }
            
            ActionPlan plan = planner.parseStep(step, page, smartLocator);
            logger.debug(plan.toString());
            
            // Check if this is a composite action plan
            if (plan instanceof agent.planner.CompositeActionPlan) {
                agent.planner.CompositeActionPlan compositePlan = (agent.planner.CompositeActionPlan) plan;
                boolean allSubActionsSucceeded = true;
                
                logger.info("  Executing {} sub-actions...", compositePlan.getSubActionCount());
                
                int subIndex = 1;
                for (ActionPlan subAction : compositePlan.getSubActions()) {
                    logger.step("Sub-action {}/{}: {}", subIndex, compositePlan.getSubActionCount(), subAction.getActionType());
                    
                    agent.reporting.StepExecutionReport subReport = browserService.executeAction(subAction);
                    
                    if ("PASSED".equals(subReport.getStatus())) {
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
                agent.reporting.StepExecutionReport report = browserService.executeAction(plan);
                
                if ("PASSED".equals(report.getStatus())) {
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
            // Close browser resources
            if (browser != null) {
                try {
                    browser.close();
                } catch (Exception e) {
                    logger.error("Error closing browser: {}", e.getMessage());
                }
            }
            if (playwright != null) {
                try {
                    playwright.close();
                } catch (Exception e) {
                    logger.error("Error closing playwright: {}", e.getMessage());
                }
            }
        }
    }
}
