import agent.integration.SmartAutomationAgent;
import agent.reporting.StepExecutionReport;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.*;

/**
 * Advanced BDD Step Definitions with Validation Result Handling
 * 
 * Shows how to use Expected vs Actual values from verification results
 * to implement custom business logic and decision-making
 */
public class AdvancedValidationStepDefs {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(AdvancedValidationStepDefs.class);
    
    private Page page;
    private SmartAutomationAgent agent;
    
    @Before
    public void setUp() {
        // Initialize agent with your Page instance
        agent = new SmartAutomationAgent(page);
    }
    
    // ==========================================
    // EXAMPLE 1: Simple Verification with Result Inspection
    // ==========================================
    
    @Then("I should see {string}")
    public void verifyTextPresent(String expectedText) {
        String step = String.format("Then I should see \"%s\"", expectedText);
        
        // Get detailed execution report
        StepExecutionReport report = agent.executeWithReport(step);
        
        if ("PASSED".equals(report.getStatus())) {
            logger.success("✓ Verification passed");
            return;
        }
        
        // Extract validation details
        StepExecutionReport.ValidationResult validation = report.getValidation();
        
        if (validation != null) {
            String expected = validation.getExpected();
            String actual = validation.getActual();
            Boolean elementFound = validation.getElementFound();
            
            // Custom decision making based on results
            if (!elementFound) {
                logger.warn("⚠ Element not found - implementing wait logic");
                waitForElementAndRetry(expectedText);
            } else if (actual != null && !actual.equals(expected)) {
                logger.warn("⚠ Text mismatch:");
                logger.warn("  Expected: {}", expected);
                logger.warn("  Actual:   {}", actual);
                
                // Custom business logic based on partial match
                if (actual.contains(expected)) {
                    logger.success("✓ Partial match accepted for business flow");
                    return; // Accept partial match
                } else {
                    throw new AssertionError("Text mismatch: Expected '" + expected + "' but found '" + actual + "'");
                }
            }
        }
    }
    
    // ==========================================
    // EXAMPLE 2: Price Validation with Tolerance
    // ==========================================
    
    @Then("the total price should be {string}")
    public void verifyTotalPrice(String expectedPrice) {
        String step = String.format("Then I should see \"%s\"", expectedPrice);
        StepExecutionReport report = agent.executeWithReport(step);
        
        if ("PASSED".equals(report.getStatus())) {
            return; // Exact match - perfect!
        }
        
        // Get validation details
        StepExecutionReport.ValidationResult validation = report.getValidation();
        
        if (validation != null && validation.getActual() != null) {
            String expected = validation.getExpected();
            String actual = validation.getActual();
            
            // Custom business logic: Accept price within 1% tolerance
            double expectedAmount = parseCurrency(expected);
            double actualAmount = parseCurrency(actual);
            double difference = Math.abs(actualAmount - expectedAmount);
            double tolerance = expectedAmount * 0.01; // 1%
            
            if (difference <= tolerance) {
                logger.success("✓ Price within acceptable tolerance");
                logger.info("  Expected: ${}", expectedAmount);
                logger.info("  Actual:   ${}", actualAmount);
                logger.info("  Diff:     ${} (within {})", difference, tolerance);
                return; // Accept
            } else {
                throw new AssertionError(String.format(
                    "Price difference too large: Expected $%.2f, Actual $%.2f, Diff $%.2f (tolerance: $%.2f)",
                    expectedAmount, actualAmount, difference, tolerance
                ));
            }
        }
        
        throw new AssertionError("Price verification failed: " + report.getErrorMessage());
    }
    
    // ==========================================
    // EXAMPLE 3: Status Verification with Business Rules
    // ==========================================
    
    @Then("the order status should be {string}")
    public void verifyOrderStatus(String expectedStatus) {
        String step = String.format("Then I should see \"%s\"", expectedStatus);
        StepExecutionReport report = agent.executeWithReport(step);
        
        if ("PASSED".equals(report.getStatus())) {
            return;
        }
        
        // Get validation details
        StepExecutionReport.ValidationResult validation = report.getValidation();
        
        if (validation != null) {
            String expected = validation.getExpected();
            String actual = validation.getActual();
            
            // Business rule: "Processing" and "In Progress" are equivalent
            if (statusesAreEquivalent(expected, actual)) {
                logger.success("✓ Equivalent status accepted");
                logger.info("  Expected: {}", expected);
                logger.info("  Actual:   {} (equivalent)", actual);
                return;
            }
            
            // Business rule: Status progression is acceptable
            if (isAcceptableProgression(expected, actual)) {
                logger.warn("⚠ Status has progressed beyond expected");
                logger.info("  Expected: {}", expected);
                logger.info("  Current:  {}", actual);
                logger.info("  Action: Continuing test with updated status");
                updateTestContext("currentOrderStatus", actual);
                return;
            }
            
            throw new AssertionError("Invalid status: Expected '" + expected + "' but found '" + actual + "'");
        }
    }
    
    // ==========================================
    // EXAMPLE 4: Field Value Verification with Logging
    // ==========================================
    
    @Then("the field {string} should contain {string}")
    public void verifyFieldValue(String fieldName, String expectedValue) {
        String step = String.format("Then I should see \"%s\" in %s", expectedValue, fieldName);
        StepExecutionReport report = agent.executeWithReport(step);
        
        // Log detailed results regardless of outcome
        logValidationResult(report, fieldName, expectedValue);
        
        if ("PASSED".equals(report.getStatus())) {
            return;
        }
        
        // Custom handling based on validation details
        StepExecutionReport.ValidationResult validation = report.getValidation();
        
        if (validation != null && !validation.getElementFound()) {
            // Element doesn't exist - create it (business logic)
            logger.warn("⚠ Field not found - creating default value");
            createDefaultFieldValue(fieldName, expectedValue);
            return;
        }
        
        // Report comparison details
        if (validation != null) {
            logger.error("❌ Field value mismatch:");
            logger.error("  Field:    {}", fieldName);
            logger.error("  Expected: {}", validation.getExpected());
            logger.error("  Actual:   {}", validation.getActual());
            logger.error("  Match:    {}", validation.getMatch());
        }
        
        throw new AssertionError("Field verification failed: " + report.getErrorMessage());
    }
    
    // ==========================================
    // EXAMPLE 5: Full JSON Report for Analytics
    // ==========================================
    
    @Then("verify complete user profile")
    public void verifyCompleteProfile() {
        // Execute multiple verifications and collect results
        String[] verifications = {
            "Then I should see \"John Doe\" in Full Name",
            "Then I should see \"john@example.com\" in Email",
            "Then I should see \"Premium\" in Account Type"
        };
        
        StringBuilder analyticsReport = new StringBuilder();
        analyticsReport.append("PROFILE VERIFICATION REPORT\n");
        analyticsReport.append("============================\n");
        
        int passed = 0;
        int failed = 0;
        
        for (String verification : verifications) {
            StepExecutionReport report = agent.executeWithReport(verification);
            
            if ("PASSED".equals(report.getStatus())) {
                passed++;
                analyticsReport.append("✓ PASS: ").append(verification).append("\n");
            } else {
                failed++;
                analyticsReport.append("✗ FAIL: ").append(verification).append("\n");
                
                // Add validation details
                if (report.getValidation() != null) {
                    analyticsReport.append("  Expected: ").append(report.getValidation().getExpected()).append("\n");
                    analyticsReport.append("  Actual:   ").append(report.getValidation().getActual()).append("\n");
                }
            }
            
            // Log full JSON for analytics system
            logger.debug(report.toJson());
        }
        
        analyticsReport.append("============================\n");
        analyticsReport.append("Passed: ").append(passed).append("\n");
        analyticsReport.append("Failed: ").append(failed).append("\n");
        
        logger.info(analyticsReport.toString());
        
        if (failed > 0) {
            throw new AssertionError(failed + " verification(s) failed");
        }
    }
    
    // ==========================================
    // Helper Methods
    // ==========================================
    
    private double parseCurrency(String currency) {
        return Double.parseDouble(currency.replaceAll("[^0-9.]", ""));
    }
    
    private boolean statusesAreEquivalent(String status1, String status2) {
        // Business logic for equivalent statuses
        Map<String, List<String>> equivalents = Map.of(
            "Processing", List.of("In Progress", "Being Processed"),
            "Completed", List.of("Done", "Finished"),
            "Cancelled", List.of("Canceled", "Aborted")
        );
        
        for (Map.Entry<String, List<String>> entry : equivalents.entrySet()) {
            if ((status1.equals(entry.getKey()) && entry.getValue().contains(status2)) ||
                (status2.equals(entry.getKey()) && entry.getValue().contains(status1))) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isAcceptableProgression(String expected, String actual) {
        // Define acceptable status progressions
        List<String> progression = List.of(
            "Pending", "Processing", "Shipped", "Delivered", "Completed"
        );
        
        int expectedIndex = progression.indexOf(expected);
        int actualIndex = progression.indexOf(actual);
        
        return actualIndex > expectedIndex; // Progression forward is OK
    }
    
    private void logValidationResult(StepExecutionReport report, String field, String expected) {
        logger.info("\n📊 VALIDATION RESULT:");
        logger.info("  Field:    {}", field);
        logger.info("  Expected: {}", expected);
        logger.info("  Status:   {}", report.getStatus());
        
        if (report.getValidation() != null) {
            logger.info("  Actual:   {}", report.getValidation().getActual());
            logger.info("  Match:    {}", report.getValidation().getMatch());
            logger.info("  Found:    {}", report.getValidation().getElementFound());
            logger.info("  Visible:  {}", report.getValidation().getElementVisible());
        }
        logger.info("");
    }
    
    private void waitForElementAndRetry(String text) {
        // Custom wait logic
        logger.info("Waiting for element with text: {}", text);
        page.waitForTimeout(2000);
    }
    
    private void createDefaultFieldValue(String field, String value) {
        // Business logic to create missing field
        logger.info("Creating field '{}' with default value: {}", field, value);
    }
    
    private void updateTestContext(String key, String value) {
        // Update test context for downstream steps
        logger.info("Context updated: {} = {}", key, value);
    }
}
