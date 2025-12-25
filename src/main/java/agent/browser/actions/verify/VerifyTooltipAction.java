package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;
import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

/**
 * Action to verify that a specific tooltip appears when an element is hovered.
 */
public class VerifyTooltipAction implements BrowserAction {
    private static final LoggerUtil logger = LoggerUtil.getLogger(VerifyTooltipAction.class);

    @Override
    public boolean execute(Page page, SmartLocator smartLocator, ActionPlan plan) {
        String elementName = plan.getElementName();
        String expectedTooltipText = plan.getValue();

        logger.info("Verifying tooltip for '{}' contains: '{}'", elementName, expectedTooltipText);

        // 1. Find and Hover the target element
        Locator target = smartLocator.waitForSmartElement(elementName, null);
        if (target == null) {
            logger.failure("Could not find target element to hover: {}", elementName);
            return false;
        }

        target.hover();
        logger.debug("Hovered over element, waiting for tooltip...");

        // Strategy 0: Check for standard HTML 'title' attribute (simplest tooltip)
        String title = target.getAttribute("title");
        if (title != null && title.contains(expectedTooltipText)) {
            logger.success("Tooltip verified via 'title' attribute: '{}'", title);
            return true;
        }

        // Tooltips are often dynamic. We look for elements with role="tooltip" or containing the text.
        // We poll for up to 5 seconds.
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            // Strategy 1: Look for aria-describedby and then that ID
            String ariaId = target.getAttribute("aria-describedby");
            if (ariaId != null && !ariaId.isEmpty()) {
                // Handle multiple IDs or colon-prefixed IDs common in some frameworks
                for (String idPart : ariaId.split("\\s+")) {
                    Locator tooltipById = page.locator("#" + idPart.replace(":", "\\:"));
                    if (tooltipById.isVisible()) {
                        String actualText = tooltipById.innerText();
                        if (actualText.contains(expectedTooltipText)) {
                            logger.success("Tooltip verified via aria-describedby ID '{}': '{}'", idPart, actualText.trim());
                            return true;
                        }
                    }
                }
            }

            // Strategy 2: Look for role="tooltip" (Generic ARIA)
            Locator tooltipByRole = page.locator("[role='tooltip']").all().stream()
                .filter(Locator::isVisible)
                .findFirst().orElse(null);
            if (tooltipByRole != null) {
                String actualText = tooltipByRole.innerText();
                if (actualText.contains(expectedTooltipText)) {
                    logger.success("Tooltip verified via role='tooltip': '{}'", actualText.trim());
                    return true;
                }
            }

            // Strategy 3: Look for common Tooltip CSS patterns (Bootstrap, Material, etc.)
            String[] commonClasses = {".tooltip", ".tooltip-inner", ".md-tooltip", ".p-tooltip", ".v-tooltip__content", ".ant-tooltip"};
            for (String cssClass : commonClasses) {
                Locator tooltipByClass = page.locator(cssClass).all().stream()
                    .filter(Locator::isVisible)
                    .findFirst().orElse(null);
                if (tooltipByClass != null) {
                    String actualText = tooltipByClass.innerText();
                    if (actualText.contains(expectedTooltipText)) {
                        logger.success("Tooltip verified via CSS class '{}': '{}'", cssClass, actualText.trim());
                        return true;
                    }
                }
            }
            
            // Strategy 4: Last resort - check any visible element containing the exact text that isn't the target
            Locator fallback = page.locator("text=\"" + expectedTooltipText + "\"").all().stream()
                .filter(Locator::isVisible)
                .findFirst().orElse(null);
            if (fallback != null && !fallback.equals(target)) {
                 logger.success("Tooltip verified via direct text match: '{}'", expectedTooltipText);
                 return true;
            }

            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        logger.failure("Tooltip with text '{}' not found after hover", expectedTooltipText);
        return false;
    }
}
