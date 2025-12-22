package agent.browser;

import agent.browser.locator.*;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public class SmartLocator {

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
        return waitForSmartElement(name, type, null);
    }

    public Locator waitForSmartElement(String name, String type, Locator scope) {
        long deadline = System.currentTimeMillis() + 30000; // Reduced from 180s to 30s
        int maxRetries = 60; // Max 60 attempts (30 seconds / 500ms)
        int retryCount = 0;
        long lastLogTime = 0;
        
        while (System.currentTimeMillis() < deadline && retryCount < maxRetries) {
            Locator loc = findSmartElement(name, type, scope);
            if (loc != null && loc.isVisible()) {
                return loc;
            }
            
            retryCount++;
            
            // Throttle logging: only log every 5 seconds instead of every 500ms
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLogTime > 5000) {
                System.out.println("⏳ Still waiting for element: '" + name + "' (attempt " + retryCount + "/" + maxRetries + ")");
                lastLogTime = currentTime;
            }
            
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        
        // Log clear failure message
        System.err.println("❌ TIMEOUT: Element '" + name + "' not found after " + retryCount + " attempts (" + (30000/1000) + "s)");
        if (scope != null) {
            System.err.println("   Searched within row scope - element may not exist in this row");
        }
        System.err.println("   💡 Tip: Check if the step syntax is correct and element exists on the page");
        
        return null;
    }

    public Locator findSmartElement(String name, String parsedType) {
        return findSmartElement(name, parsedType, null);
    }

    public Locator findSmartElement(String name, String parsedType, Locator scope) {
        if (name == null) return null;

        System.out.println("Analyzing DOM for target: '" + name + "' (Type: " + parsedType + ")" + (scope != null ? " [Scoped]" : ""));

        // 1. Scan DOM (Scoped or Global)
        List<ElementCandidate> elements = (scope != null) ? docScanner.scan(scope) : docScanner.scan(page);

        double bestScore = 0.0;
        ElementCandidate bestElement = null;

        // 2. Score Candidates
        for (ElementCandidate el : elements) {
            double score = scorer.score(el, name, parsedType);
            if (score > bestScore) {
                bestScore = score;
                bestElement = el;
            }
        }

        // Debug: Show top candidates if no strong match
        if (bestScore <= 30 && scope != null) {
            System.out.println("  🔍 DEBUG: Elements found in row scope:");
            elements.stream()
                .limit(10)
                .forEach(el -> {
                    double s = scorer.score(el, name, parsedType);
                    System.out.println("     - " + el.tag + 
                        (el.text.isEmpty() ? "" : " text='" + el.text.substring(0, Math.min(20, el.text.length())) + "'") +
                        (el.title.isEmpty() ? "" : " title='" + el.title + "'") +
                        (el.label.isEmpty() ? "" : " aria-label='" + el.label + "'") +
                        (el.className.isEmpty() ? "" : " class='" + el.className.substring(0, Math.min(30, el.className.length())) + "'") +
                        " → score=" + s);
                });
        }

        // 3. Resolve Locator
        if (bestScore > 30 && bestElement != null) {
            return locatorFactory.createLocator(bestElement, bestScore, parsedType, scope);
        }
        
        System.out.println("  > No strong match found for '" + name + "'");
        return null;
    }
}
