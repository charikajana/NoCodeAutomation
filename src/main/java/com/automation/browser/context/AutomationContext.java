package com.automation.browser.context;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Represents a single operational layer in the automation execution.
 * Can be the main page, an iframe, or a specific element scope (like a table row).
 */
public class AutomationContext {
    private final Page page;
    private final String frameAnchor;
    private final Locator scope;

    public AutomationContext(Page page, String frameAnchor, Locator scope) {
        this.page = page;
        this.frameAnchor = frameAnchor;
        this.scope = scope;
    }

    public Page getPage() {
        return page;
    }

    public String getFrameAnchor() {
        return frameAnchor;
    }

    public Locator getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return String.format("AutomationContext(Page=%s, Frame=%s, Scoped=%s)", 
            page != null ? page.url() : "null",
            frameAnchor != null ? frameAnchor : "Main",
            scope != null ? "Yes" : "No");
    }
}
