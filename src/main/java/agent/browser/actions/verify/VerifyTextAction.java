package agent.browser.actions.verify;

import agent.browser.actions.BrowserAction;

import agent.browser.SmartLocator;
import agent.planner.ActionPlan;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import agent.browser.locator.table.TableNavigator;

public class VerifyTextAction implements BrowserAction {
    @Override
    public boolean execute(Page page, SmartLocator locator, ActionPlan plan) {
        String value = plan.getValue();
        String targetName = plan.getElementName();
        String textToVerify = (value != null && !value.isEmpty()) ? value : targetName;
        
        if (textToVerify == null) {
            System.err.println("FAILURE: Verification failed - No text specified.");
            return false;
        }

        System.out.println("Verifying text presence: " + textToVerify);
        
        // Determine search scope
        Locator searchScope = null;
        if (plan.getRowAnchor() != null) {
             TableNavigator navigator = new TableNavigator();
             searchScope = navigator.findRowByAnchor(page, plan.getRowAnchor());
             if (searchScope == null) {
                 System.err.println("FAILURE: Row not found for anchor: " + plan.getRowAnchor());
                 return false;
             }
        }
        
        // Try multiple strategies for maximum compatibility
        String foundText = null;
        String matchType = null;
        Locator foundElement = null;
        
        // Strategy 1: Exact text match (most frameworks)
        foundElement = tryFindText(page, searchScope, textToVerify, true);
        if (foundElement != null && foundElement.count() > 0) {
            foundText = getElementText(foundElement);
            matchType = "EXACT";
        }
        
        // Strategy 2: Contains match (for partial text)
        if (foundElement == null || foundElement.count() == 0) {
            foundElement = tryFindText(page, searchScope, textToVerify, false);
            if (foundElement != null && foundElement.count() > 0) {
                foundText = getElementText(foundElement);
                matchType = "CONTAINS";
            }
        }
        
        // Strategy 3: Case-insensitive match
        if (foundElement == null || foundElement.count() == 0) {
            foundElement = tryFindTextCaseInsensitive(page, searchScope, textToVerify);
            if (foundElement != null && foundElement.count() > 0) {
                foundText = getElementText(foundElement);
                matchType = "CASE-INSENSITIVE";
            }
        }
        
        // Strategy 4: Check if any select/input has this value
        if (foundElement == null || foundElement.count() == 0) {
            String xpath = String.format("//*[(@value='%s' or .='%s')]", textToVerify, textToVerify);
            foundElement = (searchScope != null) ? searchScope.locator(xpath).first() : page.locator(xpath).first();
            if (foundElement != null && foundElement.count() > 0) {
                foundText = getElementText(foundElement);
                matchType = "VALUE/XPATH";
            }
        }
        
        // Validate result
        if (foundElement != null && foundElement.count() > 0) {
            boolean visible = false;
            try {
                visible = foundElement.isVisible();
            } catch (Exception e) {
                // Ignore
            }

            if (visible || "VALUE/XPATH".equals(matchType) || "option".equalsIgnoreCase((String)foundElement.evaluate("el => el.tagName"))) {
                System.out.println("--------------------------------------------------");
                System.out.println(" ✅ VALIDATION SUCCESS");
                System.out.println(" Expected: " + textToVerify);
                System.out.println(" Found in Element: " + foundText);
                System.out.println(" Match Strategy: " + matchType + (visible ? "" : " (Hidden/Value)"));
                System.out.println("--------------------------------------------------");
                return true;
            } else {
                System.err.println("--------------------------------------------------");
                System.err.println(" ❌ VALIDATION FAILED");
                System.err.println(" Expected: " + textToVerify);
                System.err.println(" Issue: Element found but NOT visible");
                System.err.println("--------------------------------------------------");
                return false;
            }
        } else {
            System.err.println("--------------------------------------------------");
            System.err.println(" ❌ VALIDATION FAILED");
            System.err.println(" Expected: " + textToVerify);
            System.err.println(" Issue: Text NOT found on page");
            System.err.println("--------------------------------------------------");
            return false;
        }
    }
    
    /**
     * Try to find text using exact or contains match
     */
    private Locator tryFindText(Page page, Locator scope, String text, boolean exact) {
        try {
            if (scope != null) {
                return scope.getByText(text, new Locator.GetByTextOptions().setExact(exact)).first();
            } else {
                return page.getByText(text, new Page.GetByTextOptions().setExact(exact)).first();
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Try case-insensitive search (for frameworks that change case)
     */
    private Locator tryFindTextCaseInsensitive(Page page, Locator scope, String text) {
        try {
            String xpath = String.format(
                "//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                text.toLowerCase()
            );
            
            if (scope != null) {
                return scope.locator(xpath).first();
            } else {
                return page.locator(xpath).first();
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get text from element, checking multiple sources
     */
    private String getElementText(Locator element) {
        try {
            // Try innerText first (most common)
            String text = element.innerText().trim();
            if (!text.isEmpty()) return text;
            
            // Try textContent (for hidden elements)
            text = element.textContent().trim();
            if (!text.isEmpty()) return text;
            
            // Try value attribute (for inputs)
            text = (String) element.evaluate("el => el.value || ''");
            if (!text.isEmpty()) return text;
            
            // Try aria-label (for accessibility text)
            text = (String) element.evaluate("el => el.getAttribute('aria-label') || ''");
            if (!text.isEmpty()) return text;
            
            return "[No text found]";
        } catch (Exception e) {
            return "[Error reading text]";
        }
    }
}
