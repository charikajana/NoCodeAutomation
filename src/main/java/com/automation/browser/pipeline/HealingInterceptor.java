package com.automation.browser.pipeline;

import com.automation.browser.context.ContextManager;
import com.automation.browser.healing.ElementFingerprint;
import com.automation.browser.healing.HealingRegistry;
import com.automation.browser.locator.core.ElementCandidate;
import com.automation.planner.ActionPlan;
import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Page;

/**
 * Intercepts actions to record element 'fingerprints' for future self-healing.
 */
public class HealingInterceptor implements InteractionInterceptor {
    private static final LoggerUtil logger = LoggerUtil.getLogger(HealingInterceptor.class);
    private final HealingRegistry registry = HealingRegistry.getInstance();

    @Override
    public void beforeAction(Page page, ContextManager context, ActionPlan plan) {
        // No pre-action logic for recording
    }

    @Override
    public void afterAction(Page page, ContextManager context, ActionPlan plan, boolean success) {
        if (success) {
            recordFingerprint(context, plan);
        }
    }

    private void recordFingerprint(ContextManager context, ActionPlan plan) {
        // Retrieve the captured candidate from metadata
        if (plan.hasMetadata("last_matched_candidate")) {
            ElementCandidate candidate = (ElementCandidate) plan.getMetadataValue("last_matched_candidate");
            double score = (double) plan.getMetadataValue("last_matched_score");

            ElementFingerprint fingerprint = new ElementFingerprint();
            fingerprint.stepText = plan.getTarget();
            fingerprint.tag = candidate.tag;
            fingerprint.id = candidate.id;
            fingerprint.name = candidate.name;
            fingerprint.text = candidate.text;
            fingerprint.className = candidate.className;
            fingerprint.xpath = candidate.xpath;
            fingerprint.frameAnchor = context.getCurrent().getFrameAnchor();
            fingerprint.confidence = score;

            // Store in registry using the target (NLP step) as key
            // This allows us to heal the exact same step if it fails later
            registry.store(plan.getTarget(), fingerprint);
            logger.debug("Healing Engine: Recorded fingerprint for '{}' (Confidence: {})", plan.getTarget(), score);
        }
    }
}
