package agent.integration.examples;

import agent.integration.SmartAutomationAgent;
import agent.reporting.StepExecutionReport;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Example: Using isSupported() for Smart Decision Making
 * 
 * Shows how to check if framework supports a step BEFORE executing it,
 * allowing intelligent fallback to custom code when needed.
 */
public class SmartDecisionStepDefs {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(SmartDecisionStepDefs.class);
    
    private Page playwrightPage;
    private WebDriver seleniumDriver;  // Fallback driver
    
    private SmartAutomationAgent agent;
    
    @Before
    public void setUp() {
        // Initialize agent with your Page instance
        agent = new SmartAutomationAgent(playwrightPage);
    }
    
    // ==========================================
    // EXAMPLE 1: Simple isSupported Check
    // ==========================================
    
    @When("I enter {string} in {string}")
    public void enterText(String value, String fieldName) {
        String step = String.format("When I enter \"%s\" in %s", value, fieldName);
        
        // ✅ Check if framework supports this step
        if (agent.isSupported(step)) {
            logger.info("✓ Framework supports this step - using smart automation");
            agent.execute(step);
        } else {
            logger.warn("✗ Framework doesn't support this step - using custom code");
            customEnterText(value, fieldName);
        }
    }
    
    // ==========================================
    // EXAMPLE 2: Conditional Logic Based on Support
    // ==========================================
    
    @When("I perform {string}")
    public void performAction(String actionDescription) {
        String step = "When I " + actionDescription;
        
        if (agent.isSupported(step)) {
            // Use framework
            boolean success = agent.execute(step);
            
            if (!success) {
                logger.warn("Framework execution failed - trying custom logic");
                executeCustomAction(actionDescription);
            }
        } else {
            // Framework doesn't support - use custom code directly
            logger.info("Using custom implementation for: {}", actionDescription);
            executeCustomAction(actionDescription);
        }
    }
    
    // ==========================================
    // EXAMPLE 3: Pre-validate Multiple Steps
    // ==========================================
    
    @When("I complete registration form")
    public void completeRegistrationForm() {
        String[] steps = {
            "When I enter \"John\" in First Name",
            "When I enter \"Doe\" in Last Name",
            "When I enter \"john@test.com\" in Email",
            "When I select \"United States\" in Country",
            "When I check Terms and Conditions",
            "When I click Register button"
        };
        
        // Pre-validate all steps
        logger.info("Pre-validating {} steps...", steps.length);
        int supported = 0;
        int unsupported = 0;
        
        for (String step : steps) {
            if (agent.isSupported(step)) {
                supported++;
            } else {
                unsupported++;
                logger.warn("Step not supported: {}", step);
            }
        }
        
        logger.info("Validation: {} supported, {} need custom code", supported, unsupported);
        
        // Execute all steps
        for (String step : steps) {
            if (agent.isSupported(step)) {
                agent.execute(step);
            } else {
                executeCustomStepLogic(step);
            }
        }
    }
    
    // ==========================================
    // EXAMPLE 4: Smart Reporting
    // ==========================================
    
    @When("I execute test step {string}")
    public void executeTestStep(String stepDescription) {
        // Build step
        String step = "When " + stepDescription;
        
        // Check support and log
        boolean isSupported = agent.isSupported(step);
        
        logger.info("════════════════════════════════════");
        logger.info("Step: {}", step);
        logger.info("Supported by Framework: {}", isSupported ? "YES ✓" : "NO ✗");
        
        if (isSupported) {
            logger.info("Execution Strategy: Smart Automation");
            StepExecutionReport report = agent.executeWithReport(step);
            logger.info("Result: {}", report.getStatus());
            logger.info("Duration: {}ms", report.getDuration());
        } else {
            logger.info("Execution Strategy: Custom Code");
            executeCustomStepLogic(step);
        }
        logger.info("════════════════════════════════════");
    }
    
    // ==========================================
    // EXAMPLE 5: Business-Specific Complex Logic
    // ==========================================
    
    @When("I process order {string} with special handling")
    public void processOrderWithSpecialHandling(String orderId) {
        // Step 1: Navigate to orders (framework can handle)
        String navStep = "When I click Orders menu";
        if (agent.isSupported(navStep)) {
            agent.execute(navStep);
        } else {
            customNavigateToOrders();
        }
        
        // Step 2: Search for order (framework can handle)
        String searchStep = String.format("When I enter \"%s\" in Search Orders", orderId);
        if (agent.isSupported(searchStep)) {
            agent.execute(searchStep);
        } else {
            customSearchOrder(orderId);
        }
        
        // Step 3: Special business logic (framework CAN'T handle this)
        String specialStep = "When I apply enterprise discount rules with tax calculation";
        
        // We KNOW this is custom logic, but still check for completeness
        if (agent.isSupported(specialStep)) {
            agent.execute(specialStep);
        } else {
            // Expected - use custom business logic
            logger.info("Applying custom enterprise business rules...");
            applyEnterpriseDiscountRules(orderId);
            calculateComplexTaxes(orderId);
            applySpecialHandlingFee(orderId);
        }
        
        // Step 4: Submit (framework can handle)
        String submitStep = "When I click Submit Order button";
        if (agent.isSupported(submitStep)) {
            agent.execute(submitStep);
        } else {
            customSubmitOrder();
        }
    }
    
    // ==========================================
    // EXAMPLE 6: Graceful Degradation
    // ==========================================
    
    @When("user interacts with {string}")
    public void userInteractsWith(String element) {
        // Try various step formulations
        String[] attemptedSteps = {
            String.format("When I click %s", element),
            String.format("When I click on %s", element),
            String.format("When I select %s", element),
            String.format("When I interact with %s", element)
        };
        
        for (String step : attemptedSteps) {
            if (agent.isSupported(step)) {
                logger.info("✓ Found supported pattern: {}", step);
                agent.execute(step);
                return;  // Success!
            }
        }
        
        // None of the standard patterns worked - use custom code
        logger.warn("No supported patterns found - using custom interaction");
        customInteractWithElement(element);
    }
    
    // ==========================================
    // EXAMPLE 7: Statistics Collection
    // ==========================================
    
    private int frameworkSteps = 0;
    private int customSteps = 0;
    
    @When("I run step {string}")
    public void runStep(String stepText) {
        if (agent.isSupported(stepText)) {
            frameworkSteps++;
            agent.execute(stepText);
        } else {
            customSteps++;
            executeCustomStepLogic(stepText);
        }
    }
    
    @After
    public void reportStatistics() {
        int total = frameworkSteps + customSteps;
        double automationRate = total > 0 ? (frameworkSteps * 100.0 / total) : 0;
        
        logger.info("═══════════════════════════════════════════");
        logger.info("  STEP EXECUTION STATISTICS");
        logger.info("═══════════════════════════════════════════");
        logger.info("  Framework Steps:     {}", frameworkSteps);
        logger.info("  Custom Code Steps:   {}", customSteps);
        logger.info("  Total Steps:         {}", total);
        logger.info("  Automation Rate:     {:.1f}%", automationRate);
        logger.info("═══════════════════════════════════════════");
    }
    
    // ==========================================
    // HELPER: Custom Code Implementations
    // ==========================================
    
    private void customEnterText(String value, String fieldName) {
        // Your existing Selenium/custom logic
        logger.info("Executing custom enterText: {} = {}", fieldName, value);
        seleniumDriver.findElement(By.id(fieldName.toLowerCase())).sendKeys(value);
    }
    
    private void executeCustomAction(String action) {
        logger.info("Executing custom action: {}", action);
        // Your custom implementation
    }
    
    private void executeCustomStepLogic(String step) {
        logger.info("Executing custom step logic: {}", step);
        // Your custom implementation
    }
    
    private void customNavigateToOrders() {
        logger.info("Custom navigation to orders");
    }
    
    private void customSearchOrder(String orderId) {
        logger.info("Custom order search: {}", orderId);
    }
    
    private void applyEnterpriseDiscountRules(String orderId) {
        logger.info("Applying enterprise discount rules for order: {}", orderId);
        // Complex business logic
    }
    
    private void calculateComplexTaxes(String orderId) {
        logger.info("Calculating complex taxes for order: {}", orderId);
        // Tax calculation logic
    }
    
    private void applySpecialHandlingFee(String orderId) {
        logger.info("Applying special handling fee: {}", orderId);
    }
    
    private void customSubmitOrder() {
        logger.info("Custom order submission");
    }
    
    private void customInteractWithElement(String element) {
        logger.info("Custom interaction with element: {}", element);
        // Fallback interaction logic
    }
}
