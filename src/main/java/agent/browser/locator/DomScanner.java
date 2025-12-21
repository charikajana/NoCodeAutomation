package agent.browser.locator;

import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomScanner {

    public List<ElementCandidate> scan(Page page) {
        Object result = page.evaluate("() => {" +
                "  const candidates = Array.from(document.querySelectorAll('button, a, input, textarea, select, [role=\"button\"], label, span, div, p, h1, h2, h3, h4, h5, h6'));" +
                "  return candidates.map(el => {" +
                "    const rect = el.getBoundingClientRect();" +
                "    const visible = rect.width > 0 && rect.height > 0 && window.getComputedStyle(el).visibility !== 'hidden';" +
                "    if (!visible) return null;" +
                "    return {" +
                "      tag: el.tagName.toLowerCase()," +
                "      id: el.id || ''," +
                "      forAttr: el.getAttribute('for') || ''," +
                "      name: el.name || ''," +
                "      text: el.innerText || ''," +
                "      placeholder: el.placeholder || ''," +
                "      label: el.getAttribute('aria-label') || ''," +
                "      title: el.getAttribute('title') || ''," +
                "      type: el.type || ''," +
                "      role: el.getAttribute('role') || ''," +
                "      class: el.className || ''" +
                "    };" +
                "  }).filter(item => item !== null);" +
                "}");

        List<Map<String, String>> rawList = (List<Map<String, String>>) result;
        List<ElementCandidate> candidates = new ArrayList<>();

        for (Map<String, String> map : rawList) {
            ElementCandidate c = new ElementCandidate();
            c.tag = map.getOrDefault("tag", "");
            c.id = map.getOrDefault("id", "");
            c.text = map.getOrDefault("text", "");
            c.name = map.getOrDefault("name", "");
            c.placeholder = map.getOrDefault("placeholder", "");
            c.label = map.getOrDefault("label", "");
            c.title = map.getOrDefault("title", "");
            c.type = map.getOrDefault("type", "");
            c.role = map.getOrDefault("role", "");
            c.className = map.getOrDefault("class", "");
            c.forAttr = map.getOrDefault("forAttr", "");
            candidates.add(c);
        }
        return candidates;
    }
}
