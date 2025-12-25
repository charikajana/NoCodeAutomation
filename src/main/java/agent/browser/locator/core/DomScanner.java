package agent.browser.locator.core;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomScanner {

    public List<ElementCandidate> scan(Page page, boolean includeHidden) {
        return scanInternal(page, null, null, includeHidden);
    }

    public List<ElementCandidate> scan(Page page) {
        return scanInternal(page, null, null, false);
    }
    
    public List<ElementCandidate> scan(Locator scope, boolean includeHidden) {
        return scanInternal(null, null, scope, includeHidden);
    }

    public List<ElementCandidate> scan(Locator scope) {
        return scanInternal(null, null, scope, false);
    }

    public List<ElementCandidate> scan(Frame frame, boolean includeHidden) {
        return scanInternal(null, frame, null, includeHidden);
    }

    public List<ElementCandidate> scan(Frame frame) {
        return scanInternal(null, frame, null, false);
    }

    @SuppressWarnings("unchecked")
    private List<ElementCandidate> scanInternal(Page page, Frame frame, Locator scope, boolean includeHidden) {
        String js = "includeHidden => root => {" +
                "  const base = root || document;" +
                "  const candidates = Array.from(base.querySelectorAll('button, a, input, textarea, select, [role=\"button\"], label, li, span, div, p, h1, h2, h3, h4, h5, h6'));" +
                "  return candidates.map(el => {" +
                "    const rect = el.getBoundingClientRect();" +
                "    const hasDimension = rect.width > 0 && rect.height > 0;" +
                "    const isStyleVisible = window.getComputedStyle(el).visibility !== 'hidden';" +
                "    const isVisible = hasDimension && isStyleVisible;" +
                "    if (!includeHidden && !isVisible) return null;" +
                "    return {" +
                "      tag: el.tagName.toLowerCase()," +
                "      id: el.id || ''," +
                "      forAttr: el.getAttribute('for') || ''," +
                "      name: el.name || ''," +
                "      text: el.innerText || el.textContent || ''," +
                "      placeholder: el.placeholder || ''," +
                "      label: el.getAttribute('aria-label') || ''," +
                "      title: el.getAttribute('title') || ''," +
                "      type: el.type || ''," +
                "      role: el.getAttribute('role') || ''," +
                "      class: el.className || ''," +
                "      visible: isVisible" +
                "    };" +
                "  }).filter(item => item !== null);" +
                "}";

        Object result = new ArrayList<>();
        try {
            if (scope != null) {
                result = scope.evaluate(js, includeHidden);
            } else if (frame != null) {
                if (!frame.isDetached()) {
                    result = frame.evaluate("(" + js + ")(" + includeHidden + ")");
                }
            } else {
                result = page.evaluate("(" + js + ")(" + includeHidden + ")");
            }
        } catch (Exception e) {
            // Log and return empty results for detached frames or cross-origin issues
            // System.err.println("Warning: Frame detachment or security error during scan: " + e.getMessage());
            return new ArrayList<>();
        }

        if (result == null) return new ArrayList<>();
        List<Map<String, Object>> rawList = (List<Map<String, Object>>) result;
        List<ElementCandidate> candidates = new ArrayList<>();

        for (Map<String, Object> map : rawList) {
            ElementCandidate c = new ElementCandidate();
            c.tag = (String) map.getOrDefault("tag", "");
            c.id = (String) map.getOrDefault("id", "");
            c.text = (String) map.getOrDefault("text", "");
            c.name = (String) map.getOrDefault("name", "");
            c.placeholder = (String) map.getOrDefault("placeholder", "");
            c.label = (String) map.getOrDefault("label", "");
            c.title = (String) map.getOrDefault("title", "");
            c.type = (String) map.getOrDefault("type", "");
            c.role = (String) map.getOrDefault("role", "");
            c.className = (String) map.getOrDefault("class", "");
            c.forAttr = (String) map.getOrDefault("forAttr", "");
            Object vis = map.get("visible");
            c.visible = vis != null && (Boolean) vis;
            candidates.add(c);
        }
        return candidates;
    }
}
