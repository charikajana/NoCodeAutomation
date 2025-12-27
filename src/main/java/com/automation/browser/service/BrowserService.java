package com.automation.browser.service;

import com.automation.browser.locator.core.SmartLocator;
import com.automation.browser.actionhandler.ActionHandlerRegistry;
import com.automation.browser.actions.BrowserAction;
import com.automation.browser.context.AutomationContext;
import com.automation.browser.context.ContextManager;
import com.automation.browser.pipeline.ContextInterceptor;
import com.automation.browser.pipeline.HealingInterceptor;
import com.automation.browser.pipeline.InteractionInterceptor;
import com.automation.browser.pipeline.StabilityInterceptor;
import com.automation.planner.ActionPlan;
import com.automation.reporting.StepExecutionReport;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.*;

import java.util.Map;

public class BrowserService {

    private static final LoggerUtil logger = LoggerUtil.getLogger(BrowserService.class);
    
    private final Page page;
    private final SmartLocator smartLocator;
    private final Map<String, BrowserAction> actionHandlers;
    private final ContextManager contextManager;
    private final java.util.List<InteractionInterceptor> interceptors;

    /**
     * Constructor that accepts externally managed Page and SmartLocator
     */
    public BrowserService(Page page, SmartLocator smartLocator) {
        this.page = page;
        this.smartLocator = smartLocator;
        this.contextManager = new ContextManager(page);
        this.interceptors = new java.util.ArrayList<>();
        
        // Initialize action handlers using the centralized registry
        ActionHandlerRegistry registry = new ActionHandlerRegistry();
        this.actionHandlers = registry.getHandlers();
        
        // Register default interceptors (The Robustness Pipeline)
        interceptors.add(new StabilityInterceptor());
        interceptors.add(new ContextInterceptor());
        interceptors.add(new HealingInterceptor());
        
        logger.info("BrowserService initialized with Pipeline-based Execution Engine");
    }

    public StepExecutionReport executeAction(ActionPlan plan) {
        long startTime = System.currentTimeMillis();
        String actionType = plan.getActionType();
        String stepText = plan.getTarget();
        
        // Always use the currently active page and update managers
        Page activePage = getActivePage();
        smartLocator.setPage(activePage);
        smartLocator.setActivePlan(plan); // Essential for metadata capture
        if (contextManager.getCurrent().getPage() != activePage) {
            contextManager.setActivePage(activePage);
        }

        // Create report
        StepExecutionReport report = new StepExecutionReport()
            .stepName(stepText)
            .action(actionType);

        BrowserAction handler = actionHandlers.get(actionType);
        if (handler != null) {
            try {
                // PHASE 1: PRE-ACTION INTERCEPTORS
                for (InteractionInterceptor interceptor : interceptors) {
                    interceptor.beforeAction(activePage, contextManager, plan);
                }

                // PHASE 2: CONTEXT RESOLUTION
                // Apply the resolved context to the plan for the handler to use
                AutomationContext currentContext = contextManager.getCurrent();
                if (currentContext.getFrameAnchor() != null) {
                    plan.setFrameAnchor(currentContext.getFrameAnchor());
                }
                if (currentContext.getScope() != null) {
                    plan.setRowAnchor("INTERNAL_SCOPED_EXECUTION"); // Mark as internally scoped
                }

                logger.debug("Pipeline: Executing action '{}' in context: {}", actionType, currentContext);
                
                // PHASE 3: EXECUTION
                boolean success = handler.execute(activePage, smartLocator, plan);
                
                // SELF-HEALING FALLBACK
                if (!success && !"unknown".equals(actionType)) {
                    com.microsoft.playwright.Locator healed = smartLocator.heal(plan);
                    if (healed != null) {
                        logger.warning("Pipeline: RETRYING action '{}' with HEALED locator", actionType);
                        plan.setMetadataValue("healed_locator", healed);
                        success = handler.execute(activePage, smartLocator, plan);
                        if (success) {
                            report.addMetadata("selfHealed", true);
                        }
                    }
                }
                
                plan.setExecuted(true);
                
                // PHASE 4: POST-ACTION INTERCEPTORS
                for (int i = interceptors.size() - 1; i >= 0; i--) {
                    interceptors.get(i).afterAction(activePage, contextManager, plan, success);
                }
                
                // Capture execution details
                long duration = System.currentTimeMillis() - startTime;
                report.status(success ? "PASSED" : "FAILED")
                    .duration(duration);
                
                // Extract metadata and details
                extractSemanticDetails(plan, report);
                extractLocatorDetails(plan, report);
                
                if (plan.hasMetadata("validation")) {
                    report.validation((StepExecutionReport.ValidationResult) plan.getMetadataValue("validation"));
                }
                
                return report;
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                logger.error("Pipeline failure in step '{}': {}", stepText, e.getMessage());
                return report.status("FAILED").duration(duration).errorMessage(e.getMessage());
            }
        } else {
            return report.status("FAILED").errorMessage("Unknown action: " + actionType);
        }
    }
    
    /**
     * Extract semantic details from ActionPlan
     */
    private void extractSemanticDetails(ActionPlan plan, StepExecutionReport report) {
        StepExecutionReport.SemanticDetails semantic =
            new StepExecutionReport.SemanticDetails();
        
        if (plan.getElementName() != null) {
            semantic.targetDescription(plan.getElementName());
        }
        
        if (plan.getValue() != null) {
            semantic.valueProvided(plan.getValue());
        }
        
        semantic.intelligenceUsed(plan.hasMetadata("intelligent_locator"));
        
        report.semanticLocator(semantic);
    }
    
    /**
     * Extract locator details from ActionPlan
     */
    private void extractLocatorDetails(ActionPlan plan, StepExecutionReport report) {
        StepExecutionReport.LocatorDetails locator =
            new StepExecutionReport.LocatorDetails();
        
        if (plan.getElementName() != null) {
            locator.elementText(plan.getElementName());
        }
        
        if (plan.getLocatorStrategy() != null) {
            locator.matchStrategy(plan.getLocatorStrategy());
        } else if (plan.hasMetadata("intelligent_locator")) {
            locator.matchStrategy("SEMANTIC");
        } else {
            locator.matchStrategy("PATTERN");
        }
        
        report.locatorIdentified(locator);
    }
    
    /**
     * Get the currently active page (handles multiple windows)
     */
    private Page getActivePage() {
        if (page != null && page.context() != null) {
            java.util.List<Page> pages = page.context().pages();
            logger.debug("getActivePage(): Total pages = {}", pages.size());
            
            for (int i = pages.size() - 1; i >= 0; i--) {
                Page p = pages.get(i);
                try {
                    // Simple check to see if page is still alive
                    String url = p.url();
                    logger.debug("  Page[{}]: URL={}", i, url);
                    logger.info("RETURNING ACTIVE PAGE: {}", url);
                    return p;
                } catch (Exception e) {
                    // Page likely closed
                    logger.debug("Skipping closed page: {}", i);
                }
            }
        }
        logger.warn("No active page found, returning fallback");
        return page; // Fallback to original page
    }

    // Getter methods (optional, for backward compatibility if needed)
    public Page getPage() {
        return getActivePage();
    }
    
    public SmartLocator getSmartLocator() {
        return smartLocator;
    }
}
