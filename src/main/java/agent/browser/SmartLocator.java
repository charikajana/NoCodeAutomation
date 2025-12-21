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
        long deadline = System.currentTimeMillis() + 180000;
        while (System.currentTimeMillis() < deadline) {
            Locator loc = findSmartElement(name, type, scope);
            if (loc != null && loc.isVisible()) {
                return loc;
            }
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
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

        // 3. Resolve Locator
        if (bestScore > 30 && bestElement != null) {
            return locatorFactory.createLocator(bestElement, bestScore, parsedType, scope);
        }
        
        System.out.println("  > No strong match found for '" + name + "'");
        return null;
    }
}
