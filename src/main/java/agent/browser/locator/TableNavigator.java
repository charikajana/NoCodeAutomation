package agent.browser.locator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Handles complex table interactions by resolving row contexts dynamically.
 * Supports standard <table>, ARIA role="row", and <div> soup grids.
 */
public class TableNavigator {

    /**
     * Finds a Row element (TR or Container) that contains the given anchor text.
     * Uses Upward Traversal from the text node effectively.
     */
    public Locator findRowByAnchor(Page page, String anchorText) {
        if (anchorText == null || anchorText.isEmpty()) return null;

        System.out.println("DEBUG: Finding row for anchor: '" + anchorText + "'");
        
        // 1. Locate the text element specifically
        // usage of setExact(true) helps avoid partial matches in other rows/cells
        Locator cell = page.getByText(anchorText, new Page.GetByTextOptions().setExact(true)).first();
        
        if (cell.count() == 0) {
            // Fallback: try non-exact match if exact fails
             System.out.println("  > Exact match failed, trying contains...");
             cell = page.getByText(anchorText).first();
        }

        if (cell.count() == 0) {
             System.out.println("  > Anchor text not found on page.");
             return null;
        }

        // 2. Traversal Logic: Walk UP to find the nearest "Row" container
        // - ancestor::tr              -> Standard tables
        // - ancestor::*[@role='row']  -> Semantic grids
        // - ancestor::*[contains(@class, 'row')] -> Common naming conventions
        // - ancestor::*[contains(@class, 'record')] -> Another common convention
        // - ancestor::li -> List based tables
        Locator row = cell.locator("xpath=ancestor::tr | ancestor::*[@role='row'] | ancestor::*[contains(@class, 'row')] | ancestor::li").first();

        if (row.count() > 0) {
             System.out.println("  > Row found: " + row.evaluate("el => el.tagName + '.' + el.className") + " via robust XPath.");
             return row;
        }

        // 3. Fallback: Sibling Heuristic (The "Div Soup" strategy)
        // If we didn't find a clear row container, let's look for a parent that has siblings of the same tag.
        System.out.println("  > Robust XPath failed. Trying Sibling Heuristic...");
        return findRowUsingHeuristic(cell);
    }

    private Locator findRowUsingHeuristic(Locator origin) {
        Locator current = origin;
        // Search up 4 levels max for a repeating container
        for (int i = 0; i < 4; i++) {
            Locator parent = current.locator("xpath=..");
            
            // Safety check for root
            if (parent.count() == 0) break;
            
            // Check if parent has a preceding sibling with same tag
            // (Simulated check)
            Locator sibling = parent.locator("xpath=preceding-sibling::* | following-sibling::*").first();
             
             if (sibling.count() > 0) {
                 // Compare tags (simplified for demo)
                 String pTag = (String) parent.evaluate("el => el.tagName");
                 String sTag = (String) sibling.evaluate("el => el.tagName");
                 
                 if (pTag != null && pTag.equals(sTag)) {
                     System.out.println("  > Found row via Sibling Heuristic: <" + pTag + ">");
                     return parent;
                 }
             }
             current = parent;
        }
        return null;
    }
}
