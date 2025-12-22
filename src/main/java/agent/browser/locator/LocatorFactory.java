package agent.browser.locator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LocatorFactory {

    private final Page page;

    public LocatorFactory(Page page) {
        this.page = page;
    }

    public Locator createLocator(ElementCandidate element, double score, String parsedType) {
        return createLocator(element, score, parsedType, null);
    }

    public Locator createLocator(ElementCandidate element, double score, String parsedType, Locator scope) {
         String foundId = element.id;
         String foundTag = element.tag;
         String foundText = element.text;
         String foundFor = element.forAttr;
         
         System.out.println("  > Found Winner: <" + foundTag + "> Text:'" + foundText + "' ID:'" + foundId + "' (Score: " + score + ")");
         
         Locator finalLocator = null;
         
         // Helper to create base locator (either from page or scope)
         // Note: We cannot use ID if scoped, unless we assume ID is unique globally (which is true by spec but not always in reality).
         // Safer to use scope.locator("#id") if strict.
         
         if (foundId != null && !foundId.isEmpty()) {
             finalLocator = (scope != null) ? scope.locator("#" + foundId) : page.locator("#" + foundId);
         } 
         else if (foundText != null && !foundText.isEmpty() && foundText.length() < 100) {
             if (score >= 150) {
                 if (scope != null) {
                     finalLocator = scope.getByText(foundText, new Locator.GetByTextOptions().setExact(true)).first();
                 } else {
                     finalLocator = page.getByText(foundText, new Page.GetByTextOptions().setExact(true)).first();
                 }
             } else {
                 finalLocator = (scope != null) ? scope.getByText(foundText).first() : page.getByText(foundText).first();
             }
         }
         else if (!element.label.isEmpty()) {
             finalLocator = (scope != null) ? scope.getByLabel(element.label).first() : page.getByLabel(element.label).first();
         }
         else if (!element.name.isEmpty()) {
             finalLocator = (scope != null) ? scope.locator("[name='" + element.name + "']").first() : page.locator("[name='" + element.name + "']").first();
         }
         else if (!element.placeholder.isEmpty()) {
             finalLocator = (scope != null) ? scope.getByPlaceholder(element.placeholder).first() : page.getByPlaceholder(element.placeholder).first();
         }
         else {
             finalLocator = (scope != null) 
                 ? scope.locator(foundTag).filter(new Locator.FilterOptions().setHasText(foundText)).first()
                 : page.locator(foundTag).filter(new Locator.FilterOptions().setHasText(foundText)).first();
         }

         boolean isFill = "input".equals(parsedType);
         boolean isSelect = "select".equals(parsedType);

         // Refine for FILL actions if we matched a non-input wrapper
         if (isFill && !"input".equals(foundTag) && !"textarea".equals(foundTag)) {
             // 1. If label with 'for', use that
             if ("label".equals(foundTag) && foundFor != null && !foundFor.isEmpty()) {
                 System.out.println("  > Refining label match to linked input #" + foundFor);
                 return page.locator("#" + foundFor);
             }
             // 2. Look for nested input/textarea
             Locator nested = finalLocator.locator("input, textarea").first();
             if (nested.count() > 0) {
                 System.out.println("  > Refining wrapper match to nested input.");
                 return nested;
             }
             
             // 3. Look for sibling input (via parent)
             Locator parent = finalLocator.locator("xpath=..");
             Locator sibling = parent.locator("input, textarea").first();
             if (sibling.count() > 0) {
                 System.out.println("  > Refining match to sibling input.");
                 return sibling;
             }

             // 4. Look for cousin input (via grandparent)
             Locator grandParent = finalLocator.locator("xpath=../..");
             Locator cousin = grandParent.locator("input, textarea").first();
             if (cousin.count() > 0) {
                 System.out.println("  > Refining match to cousin input.");
                 return cousin;
             }
             
             System.out.println("  > Match found (" + foundTag + ") but not a valid input/textarea. Discarding.");
             return null;
         }

         // Refine for SELECT actions if we matched a non-select wrapper
         if (isSelect && !"select".equals(foundTag)) {
             // 1. If label with 'for', use that
             if ("label".equals(foundTag) && foundFor != null && !foundFor.isEmpty()) {
                 System.out.println("  > Refining label match to linked select #" + foundFor);
                 return page.locator("#" + foundFor);
             }
             // 2. Look for nested select
             Locator nested = finalLocator.locator("select").first();
             if (nested.count() > 0) {
                 System.out.println("  > Refining wrapper match to nested select.");
                 return nested;
             }
             
             // 3. Look for sibling select
             Locator parent = finalLocator.locator("xpath=..");
             Locator sibling = parent.locator("select").first();
             if (sibling.count() > 0) {
                 System.out.println("  > Refining match to sibling select.");
                 return sibling;
             }

             // 4. Look for cousin select
             Locator grandParent = finalLocator.locator("xpath=../..");
             Locator cousin = grandParent.locator("select").first();
             if (cousin.count() > 0) {
                 System.out.println("  > Refining match to cousin select.");
                 return cousin;
             }
             
             // For select, if we don't find a <select> tag, it might be a custom dropdown (div/span)
             // We return the original locator and let SelectAction handle it by clicking.
             System.out.println("  > No <select> found. Assuming custom dropdown/wrapper.");
         }

         return finalLocator;
    }
}
