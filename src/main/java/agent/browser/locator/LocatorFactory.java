package agent.browser.locator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LocatorFactory {

    private final Page page;

    public LocatorFactory(Page page) {
        this.page = page;
    }

    public Locator createLocator(ElementCandidate element, double score, String parsedType) {
         String foundId = element.id;
         String foundTag = element.tag;
         String foundText = element.text;
         String foundFor = element.forAttr;
         
         System.out.println("  > Found Winner: <" + foundTag + "> Text:'" + foundText + "' ID:'" + foundId + "' (Score: " + score + ")");
         
         Locator finalLocator = null;

         if (foundId != null && !foundId.isEmpty()) {
             finalLocator = page.locator("#" + foundId);
         } 
         else if (foundText != null && !foundText.isEmpty() && foundText.length() < 100) {
             if (score >= 150) {
                 finalLocator = page.getByText(foundText, new Page.GetByTextOptions().setExact(true)).first();
             } else {
                 finalLocator = page.getByText(foundText).first();
             }
         }
         else if (!element.label.isEmpty()) {
             finalLocator = page.getByLabel(element.label).first();
         }
         else if (!element.name.isEmpty()) {
             finalLocator = page.locator("[name='" + element.name + "']").first();
         }
         else if (!element.placeholder.isEmpty()) {
             finalLocator = page.getByPlaceholder(element.placeholder).first();
         }
         else {
             finalLocator = page.locator(foundTag).filter(new Locator.FilterOptions().setHasText(foundText)).first();
         }

         boolean isFill = "input".equals(parsedType);

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
             // This handles: <div class="group"><label>Name</label><input></div>
             Locator parent = finalLocator.locator("xpath=..");
             Locator sibling = parent.locator("input, textarea").first();
             if (sibling.count() > 0) {
                 System.out.println("  > Refining match to sibling input.");
                 return sibling;
             }

             // 4. Look for cousin input (via grandparent)
             // This handles: <div class="row"><div class="col"><label>Name</label></div><div class="col"><input></div></div>
             Locator grandParent = finalLocator.locator("xpath=../..");
             Locator cousin = grandParent.locator("input, textarea").first();
             if (cousin.count() > 0) {
                 System.out.println("  > Refining match to cousin input.");
                 return cousin;
             }
             
             // If we reached here, we have a match (e.g. valid text in a div) but it's not an input/textarea
             // and we couldn't find a related input. Returning it will cause FillAction to crash.
             System.out.println("  > Match found (" + foundTag + ") but not a valid input/textarea. Discarding.");
             return null;
         }

         return finalLocator;
    }
}
