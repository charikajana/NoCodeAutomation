package agent.browser;

import agent.browser.locator.core.DomScanner;
import agent.browser.locator.core.ElementCandidate;
import agent.browser.locator.core.LocatorFactory;
import agent.browser.locator.core.CandidateScorer;
import agent.utils.LoggerUtil;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public class SmartLocator {
    
    private static final LoggerUtil logger = LoggerUtil.getLogger(SmartLocator.class);
    
    private final Page page;
    private final DomScanner docScanner;
    private final CandidateScorer scorer;
    private final LocatorFactory locatorFactory;

    public SmartLocator(Page page) {
        this.page = page;
        this.docScanner = new DomScanner();
        this.scorer = new CandidateScorer();
        this.locatorFactory = new LocatorFactory(page);
    }

    public Locator waitForSmartElement(String name, String type) {
        return waitForSmartElement(name, type, null, null);
    }

    public Locator waitForSmartElement(String name, String type, Locator scope) {
        return waitForSmartElement(name, type, scope, null);
    }

    /**
     * Wait for an element to appear, with optional scope and frame anchor
     */
    public Locator waitForSmartElement(String name, String type, Locator scope, String frameAnchor) {
        long deadline = System.currentTimeMillis() + 30000; 
        int maxRetries = 60; 
        int retryCount = 0;
        long lastLogTime = 0;
        
        while (System.currentTimeMillis() < deadline && retryCount < maxRetries) {
            Locator loc = findSmartElement(name, type, scope, frameAnchor);
            if (loc != null && loc.isVisible()) {
                return loc;
            }
            
            retryCount++;
            
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLogTime > 5000) {
                logger.waiting(5);
                logger.debug("Still waiting for element: '{}' (attempt {}/{})", name, retryCount, maxRetries);
                lastLogTime = currentTime;
            }
            
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        logger.failure("TIMEOUT: Element '{}' not found after {} attempts ({}s)", name, retryCount, 30);
        if (scope != null) {
            logger.warning("Searched within row scope - element may not exist in this row");
        }
        if (frameAnchor != null) {
            logger.warning("Searched within iframe '{}' - verify iframe existence", frameAnchor);
        }
        logger.info("Tip: Check if the step syntax is correct and element exists on the page");
        
        return null;
    }

    public Locator findSmartElement(String name, String parsedType) {
        return findSmartElement(name, parsedType, null, null);
    }
    
    public Locator findSmartElement(String name, String parsedType, Locator scope) {
        return findSmartElement(name, parsedType, scope, null);
    }

    public Locator findSmartElement(String name, String parsedType, Locator scope, String frameAnchor) {
        if (name == null) return null;

        // 1. If frame anchor is provided, narrow search to that frame
        if (frameAnchor != null) {
            Frame frame = findFrame(frameAnchor);
            if (frame != null) {
                logger.debug("Scoping search to iframe: '{}'", frameAnchor);
                return findInContext(name, parsedType, frame, null);
            } else {
                logger.warning("Target iframe '{}' not found. Searching globally...", frameAnchor);
            }
        }

        // 2. Normal search (Scoped or Page)
        Locator loc = findInContext(name, parsedType, null, scope);
        if (loc != null) return loc;

        // 3. Automatic Frame Traversal: If not found in main page, search all frames
        if (scope == null) {
            logger.debug("Element '{}' not found in main page. Searching across all iframes...", name);
            for (Frame frame : page.frames()) {
                if (frame == page.mainFrame()) continue; // Already searched
                
                loc = findInContext(name, parsedType, frame, null);
                if (loc != null) {
                    logger.success("Found element '{}' inside iframe: '{}'", name, frame.name().isEmpty() ? frame.url() : frame.name());
                    return loc;
                }
            }
        }

        return null;
    }

    /**
     * Searches for a frame by name, ID, or title
     */
    public Frame findFrame(String frameAnchor) {
        for (Frame frame : page.frames()) {
            if (frame.name().equalsIgnoreCase(frameAnchor)) return frame;
            
            // Check ID or other attributes via evaluation if name doesn't match
            try {
                String frameId = (String) frame.evaluate("() => window.frameElement ? window.frameElement.id : ''");
                if (frameAnchor.equalsIgnoreCase(frameId)) return frame;
                
                String frameTitle = (String) frame.evaluate("() => window.frameElement ? window.frameElement.title : ''");
                if (frameAnchor.equalsIgnoreCase(frameTitle)) return frame;
            } catch (Exception e) {
                // Ignore errors in cross-origin frames
            }
        }
        return null;
    }

    private Locator findInContext(String name, String parsedType, Frame frame, Locator scope) {
        logger.analysis("Analyzing DOM context for target: '{}' (Type: {}){}", name, parsedType, (frame != null ? " [Frame]" : (scope != null ? " [Scoped]" : " [Page]")));

        List<ElementCandidate> elements;
        if (scope != null) {
            elements = docScanner.scan(scope);
        } else if (frame != null) {
            elements = docScanner.scan(frame);
        } else {
            elements = docScanner.scan(page);
        }

        double bestScore = 0.0;
        ElementCandidate bestElement = null;

        for (ElementCandidate el : elements) {
            double score = scorer.score(el, name, parsedType);
            if (score > bestScore) {
                bestScore = score;
                bestElement = el;
            }
        }

        // Debug: Show top candidates if no strong match
        if (bestScore <= 30 && (scope != null || frame != null)) { // Added frame context for debug logging
            logger.debug("Elements found in context:");
            elements.stream()
                .limit(10)
                .forEach(el -> {
                    double s = scorer.score(el, name, parsedType);
                    logger.debug("   - {} {} {} {} → score={}",
                        el.tag,
                        (el.text.isEmpty() ? "" : " text='" + el.text.substring(0, Math.min(20, el.text.length())) + "'"),
                        (el.title.isEmpty() ? "" : " title='" + el.title + "'"),
                        (el.label.isEmpty() ? "" : " aria-label='" + el.label + "'"),
                        s);
                });
        }

        if (bestScore > 30 && bestElement != null) {
            // Updated locator factory needed to support Frame
            return createLocator(bestElement, bestScore, parsedType, frame, scope);
        }
        
        logger.debug("No strong match found for '{}' in current context", name);
        return null;
    }

    /**
     * Local createLocator wrapper that handles Frame context
     */
    private Locator createLocator(ElementCandidate element, double score, String parsedType, Frame frame, Locator scope) {
        if (frame != null) {
            // Temporarily use a new factory scoped to the frame
            // LocatorFactory frameFactory = new LocatorFactory(page); // We need a way to tell factory to use frame
            // Actually, LocatorFactory currently uses 'page.locator'
            // I should update LocatorFactory to accept a Frame or have it use scope correctly
            
            // For now, let's use a trick: if it's a frame, we return a locator from the frame
            // But LocatorFactory handles the complex logic.
            return locatorFactory.createLocator(element, score, parsedType, (scope != null ? scope : frame.locator(":root").first()));
            // This is a bit hacky. Better to update LocatorFactory.
        }
        
        return locatorFactory.createLocator(element, score, parsedType, scope);
    }
}
