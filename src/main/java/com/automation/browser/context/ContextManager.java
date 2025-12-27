package com.automation.browser.context;

import com.automation.utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.Stack;

/**
 * Manages a stack of operational contexts for browser automation.
 * Allows pushing and popping frames or element scopes to handle nested interactions.
 */
public class ContextManager {
    private static final LoggerUtil logger = LoggerUtil.getLogger(ContextManager.class);
    private final Stack<AutomationContext> contextStack = new Stack<>();
    private Page activePage;

    public ContextManager(Page page) {
        this.activePage = page;
        // Initial state: Main Page
        contextStack.push(new AutomationContext(page, null, null));
    }

    /**
     * Pushes a new frame context onto the stack.
     */
    public void pushFrame(String frameAnchor) {
        logger.debug("Stack: PUSH Frame '{}'", frameAnchor);
        contextStack.push(new AutomationContext(activePage, frameAnchor, null));
    }

    /**
     * Pushes a new element scope (e.g. table row) onto the stack.
     */
    public void pushScope(Locator scope) {
        AutomationContext current = getCurrent();
        logger.debug("Stack: PUSH Scope");
        contextStack.push(new AutomationContext(activePage, current.getFrameAnchor(), scope));
    }

    /**
     * Pops the top context from the stack.
     */
    public void pop() {
        if (contextStack.size() > 1) {
            AutomationContext popped = contextStack.pop();
            logger.debug("Stack: POP Context '{}'", popped);
        } else {
            logger.warning("Attempted to pop the base context. Operation ignored.");
        }
    }

    /**
     * Resets the stack to the base page context.
     */
    public void reset() {
        while (contextStack.size() > 1) {
            contextStack.pop();
        }
        logger.debug("Stack: RESET to base context");
    }

    /**
     * Updates the active page (e.g. when switching windows).
     */
    public void setActivePage(Page page) {
        this.activePage = page;
        // When page changes, we usually want to reset the stack or re-anchor it
        reset();
        contextStack.pop();
        contextStack.push(new AutomationContext(page, null, null));
        logger.debug("Stack: Updated active page to '{}'", page.url());
    }

    /**
     * Returns the current top of the stack.
     */
    public AutomationContext getCurrent() {
        return contextStack.peek();
    }

    /**
     * Returns the size of the stack.
     */
    public int depth() {
        return contextStack.size();
    }
}
