package agent.integration.examples;

import agent.integration.SmartAutomationAgent;
import agent.intelligence.StepSuggestionEngine;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.*;

import java.util.List;

/**
 * Example: Using getSuggestions() for Step Correction
 * 
 * Shows how to get intelligent suggestions when a step isn't supported
 * and help users quickly fix their test steps.
 */
public class SuggestionExamples {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(SuggestionExamples.class);
    
    private Page page;
    private SmartAutomationAgent agent;
    
    @Before
    public void setUp() {
        agent = new SmartAutomationAgent(page);
    }
    
    // ==========================================
    // EXAMPLE 1: Basic Suggestion Usage
    // ==========================================
    
    @When("I perform step {string}")
    public void performStep(String stepText) {
        // Check if supported
        if (agent.isSupported(stepText)) {
            agent.execute(stepText);
            return;
        }
        
        // Not supported - get suggestions
        logger.warn("✗ Step not supported: {}", stepText);
        logger.info("💡 Suggestions:");
        
        List<String> suggestions = agent.getSuggestions(stepText);
        for (int i = 0; i < suggestions.size(); i++) {
            logger.info("  {}. {}", i + 1, suggestions.get(i));
        }
        
        // Use first suggestion (highest confidence)
        if (!suggestions.isEmpty()) {
            String bestSuggestion = suggestions.get(0);
            // Extract the actual step from "95% - When I click..." format
            String correctedStep = bestSuggestion.substring(bestSuggestion.indexOf("-") + 2);
            correctedStep = correctedStep.substring(0, correctedStep.lastIndexOf("(")).trim();
            
            logger.info("✓ Using suggested step: {}", correctedStep);
            agent.execute(correctedStep);
        }
    }
    
    // ==========================================
    // EXAMPLE 2: Interactive Error Messages
    // ==========================================
    
    @When("user tries {string}")
    public void userTries(String unsupportedStep) {
        if (!agent.isSupported(unsupportedStep)) {
            // Build a helpful error message
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("\n╔════════════════════════════════════════╗\n");
            errorMessage.append("║   STEP NOT SUPPORTED                   ║\n");
            errorMessage.append("╚════════════════════════════════════════╝\n\n");
            errorMessage.append("Your step:\n");
            errorMessage.append("  ").append(unsupportedStep).append("\n\n");
            errorMessage.append("Did you mean one of these?\n\n");
            
            List<String> suggestions = agent.getSuggestions(unsupportedStep);
            for (int i = 0; i < Math.min(3, suggestions.size()); i++) {
                errorMessage.append("  ").append(i + 1).append(". ");
                errorMessage.append(suggestions.get(i)).append("\n");
            }
            
            errorMessage.append("\nPlease update your step and try again.\n");
            
            throw new AssertionError(errorMessage.toString());
        }
        
        agent.execute(unsupportedStep);
    }
    
    // ==========================================
    // EXAMPLE 3: Auto-Correction with Confirmation
    // ==========================================
    
    private boolean autoCorrectEnabled = true;
    
    @When("execute {string}")
    public void executeWithAutoCorrect(String step) {
        if (agent.isSupported(step)) {
            agent.execute(step);
            return;
        }
        
        // Step not supported - auto-correct if enabled
        if (autoCorrectEnabled) {
            List<StepSuggestionEngine.StepSuggestion> detailedSuggestions = 
                agent.getDetailedSuggestions(step);
            
            if (!detailedSuggestions.isEmpty()) {
                StepSuggestionEngine.StepSuggestion best = detailedSuggestions.get(0);
                
                // Only auto-correct if confidence is high (> 90%)
                if (best.getConfidence() > 0.90) {
                    logger.warn("⚠ Original step not supported: {}", step);
                    logger.info("✓ Auto-correcting to: {}", best.getSuggestedStep());
                    logger.info("  Confidence: {:.0f}%", best.getConfidence() * 100);
                    logger.info("  Reason: {}", best.getReason());
                    
                    agent.execute(best.getSuggestedStep());
                    return;
                }
            }
        }
        
        throw new AssertionError("Step not supported and auto-correct failed: " + step);
    }
    
    // ==========================================
    // EXAMPLE 4: Learning Mode (Log All Suggestions)
    // ==========================================
    
    private boolean learningMode = false;
    
    @When("I learn from {string}")
    public void learnFromStep(String step) {
        boolean supported = agent.isSupported(step);
        
        logger.info("════════════════════════════════════════");
        logger.info("LEARNING MODE - Step Analysis");
        logger.info("════════════════════════════════════════");
        logger.info("Step: {}", step);
        logger.info("Supported: {}", supported ? "YES ✓" : "NO ✗");
        
        if (!supported) {
            logger.info("\nSuggested Alternatives:");
            List<String> suggestions = agent.getSuggestions(step);
            
            if (suggestions.isEmpty()) {
                logger.info("  (No suggestions available - may need custom code)");
            } else {
                for (String suggestion : suggestions) {
                    logger.info("  • {}", suggestion);
                }
            }
        }
        
        logger.info("════════════════════════════════════════\n");
        
        // Execute if supported
        if (supported) {
            agent.execute(step);
        }
    }
    
    // ==========================================
    // EXAMPLE 5: Batch Validation with Report
    // ==========================================
    
    @Then("validate all test steps")
    public void validateAllSteps() {
        String[] steps = {
            "Click the submit button",              // Not supported
            "When I click Submit button",           // Supported
            "Type 'test' into email field",        // Not supported
            "When I enter 'test' in Email",        // Supported
            "Verify message",                       // Not supported
            "Then I should see 'Success'",         // Supported
        };
        
        logger.info("\n╔══════════════════════════════════════════════════════╗");
        logger.info("║         STEP VALIDATION REPORT                       ║");
        logger.info("╚══════════════════════════════════════════════════════╝\n");
        
        int supported = 0;
        int needsCorrection = 0;
        
        for (String step : steps) {
            boolean isSupported = agent.isSupported(step);
            
            if (isSupported) {
                supported++;
                logger.info("✓ {}", step);
            } else {
                needsCorrection++;
                logger.warn("✗ {}", step);
                
                List<String> suggestions = agent.getSuggestions(step);
                if (!suggestions.isEmpty()) {
                    logger.info("  → {}", suggestions.get(0));
                }
            }
        }
        
        logger.info("\n────────────────────────────────────────────────────");
        logger.info("Summary:");
        logger.info("  Supported:        {}/ {}", supported, steps.length);
        logger.info("  Needs Correction: {}", needsCorrection);
        logger.info("  Coverage:         {:.1f}%", (supported * 100.0 / steps.length));
        logger.info("────────────────────────────────────────────────────\n");
    }
    
    // ==========================================
    // EXAMPLE 6: IDE-Friendly Output
    // ==========================================
    
    @When("I execute IDE step {string} at line {int}")
    public void executeIDEStep(String step, int lineNumber) {
        if (!agent.isSupported(step)) {
            // Format like IDE error message
            List<String> suggestions = agent.getSuggestions(step);
            
            if (!suggestions.isEmpty()) {
                String firstSuggestion = suggestions.get(0);
                // Extract just the suggested step
                String suggested = firstSuggestion.substring(
                    firstSuggestion.indexOf("-") + 2,
                    firstSuggestion.lastIndexOf("(")
                ).trim();
                
                String message = String.format(
                    "Feature file error at line %d: Step not supported\n" +
                    "  Current: %s\n" +
                    "  Suggested: %s",
                    lineNumber, step, suggested
                );
                
                throw new AssertionError(message);
            }
        }
        
        agent.execute(step);
    }
    
    // ==========================================
    // EXAMPLE 7: Detailed Suggestion Handling
    // ==========================================
    
    @When("process complex step {string}")
    public void processComplexStep(String step) {
        if (agent.isSupported(step)) {
            agent.execute(step);
            return;
        }
        
        // Get detailed suggestions
        List<StepSuggestionEngine.StepSuggestion> detailed = 
            agent.getDetailedSuggestions(step);
        
        logger.warn("Step requires correction: {}", step);
        logger.info("\nAvailable alternatives:");
        
        for (int i = 0; i < detailed.size(); i++) {
            StepSuggestionEngine.StepSuggestion suggestion = detailed.get(i);
            
            logger.info("\nOption {}: (Confidence: {:.0f}%)", 
                i + 1, suggestion.getConfidence() * 100);
            logger.info("  Pattern: {}", suggestion.getReason());
            logger.info("  Suggested: {}", suggestion.getSuggestedStep());
            
            // Validate that suggestion actually works
            if (agent.isSupported(suggestion.getSuggestedStep())) {
                logger.info("  Status: ✓ VERIFIED");
            } else {
                logger.warn("  Status: ✗ NEEDS REFINEMENT");
            }
        }
        
        // Try to execute best suggestion
        if (!detailed.isEmpty() && detailed.get(0).getConfidence() > 0.85) {
            String best = detailed.get(0).getSuggestedStep();
            logger.info("\nExecuting best suggestion: {}", best);
            agent.execute(best);
        } else {
            throw new AssertionError("No high-confidence suggestions found");
        }
    }
}
