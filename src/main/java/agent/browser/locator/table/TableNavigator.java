package agent.browser.locator.table;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import agent.browser.locator.builders.DynamicTableXPathBuilder;

/**
 * Handles complex table interactions by resolving row contexts dynamically.
 * Now uses DynamicTableXPathBuilder for framework-agnostic row finding.
 */
public class TableNavigator {

    /**
     * Finds a Row element using column name and value (RECOMMENDED)
     * Example: findRowByColumnValue(page, "First Name", "John")
     */
    public Locator findRowByColumnValue(Page page, String columnName, String columnValue) {
        System.out.println("🎯 Finding row where '" + columnName + "' = '" + columnValue + "'");
        
        DynamicTableXPathBuilder builder = new DynamicTableXPathBuilder(page);
        return builder.findRow(columnName, columnValue);
    }

    /**
     * Legacy method: Finds row by searching for anchor text
     * DEPRECATED: Use findRowByColumnValue for better accuracy
     */
    public Locator findRowByAnchor(Page page, String anchorText) {
        if (anchorText == null || anchorText.isEmpty()) return null;

        System.out.println("⚠️  Using legacy anchor-based search for: '" + anchorText + "'");
        System.out.println("   (Consider using column-based search for better accuracy)");
        
        // CRITICAL FIX: Search within table only - specifically target React Table structure
        String tableSelector = "table, [role='grid'], [role='table'], .rt-table, .ReactTable, " +
                               "div:has(> .rt-thead), div:has(> .rt-tbody), .data-table, [class*='table']";
        
        Locator tableContainer = page.locator(tableSelector).first();
        
        if (tableContainer.count() == 0) {
            System.err.println("  > No table found on page!");
            return null;
        }
        
        // Debug: Show what container was found
        try {
            String tableClass = (String) tableContainer.evaluate("el => el.className || el.tagName");
            System.out.println("  > Found table container: " + tableClass);
        } catch (Exception e) {
            System.out.println("  > Found table container (class read failed)");
        }
        
        System.out.println("  > Searching within table element...");
        
        // 1. Locate the text element WITHIN the table only (not entire page)
        Locator cell = tableContainer.getByText(anchorText, new Locator.GetByTextOptions().setExact(true)).first();
        
        if (cell.count() == 0) {
            // Fallback: try non-exact match if exact fails
             System.out.println("  > Exact match failed, trying contains...");
             cell = tableContainer.getByText(anchorText).first();
        }

        if (cell.count() == 0) {
             System.out.println("  > Anchor text not found in table.");
             return null;
        }

        // 2. Traversal Logic: Walk UP to find the nearest "Row" container
        Locator row = cell.locator("xpath=ancestor::tr | ancestor::*[@role='row'] | ancestor::*[contains(@class, 'row')] | ancestor::li").first();

        if (row.count() > 0) {
             String rowText = (String) row.evaluate("el => el.innerText.substring(0, Math.min(100, el.innerText.length))");
             System.out.println("  > Row found: " + row.evaluate("el => el.tagName + '.' + el.className") + " via robust XPath.");
             System.out.println("  > Row content preview: " + rowText.replaceAll("\\n", " | "));
             return row;
        }

        // 3. Fallback: Sibling Heuristic (The "Div Soup" strategy)
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
            Locator sibling = parent.locator("xpath=preceding-sibling::* | following-sibling::*").first();
             
             if (sibling.count() > 0) {
                 // Compare tags
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
