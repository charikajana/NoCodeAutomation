package agent.browser.locator;

public class CandidateScorer {

    public double score(ElementCandidate el, String targetName, String parsedType) {
        String name = targetName.trim();
        String lowerName = name.toLowerCase();
        // pre-calculate clean name? Or do it here. 
        // Optimization: Clean name could be passed in, but doing it here keeps interface simple.
        String cleanName = lowerName.replaceAll("\\s+(button|link|input|field|tab)$", "").trim();

        boolean isFill = "input".equals(parsedType);
        boolean isCheck = "check".equals(parsedType) || "checkbox".equals(parsedType) || "radio".equals(parsedType);
        boolean isClick = "button".equals(parsedType) || "click".equals(parsedType);
        
        double score = 0.0;
        String text = el.text.trim();
        
        // Exact match on original or cleaned name
        if (name.equalsIgnoreCase(text) || cleanName.equalsIgnoreCase(text)) score += 150; 
        if (name.equalsIgnoreCase(el.label) || cleanName.equalsIgnoreCase(el.label)) score += 150;
        if (name.equalsIgnoreCase(el.placeholder) || cleanName.equalsIgnoreCase(el.placeholder)) score += 140;

        if (name.equalsIgnoreCase(el.id) || cleanName.equalsIgnoreCase(el.id)) score += 100;
        if (name.equalsIgnoreCase(el.name) || cleanName.equalsIgnoreCase(el.name)) score += 100;

        if (score < 100) {
             if (text.toLowerCase().contains(lowerName) || text.toLowerCase().contains(cleanName)) score += 40; 
             // Using simple string contains or custom util if available. 
             // Assuming agent.util.FuzzyMatch is available as per original code.
             try {
                if (agent.util.FuzzyMatch.ratio(name, text) > 85 || agent.util.FuzzyMatch.ratio(cleanName, text) > 85) score += 30; 
                if (agent.util.FuzzyMatch.ratio(name, el.id) > 85 || agent.util.FuzzyMatch.ratio(cleanName, el.id) > 85) score += 30; 
             } catch (Throwable t) {
                 // Fallback if FuzzyMatch is missing/fails
             }
        }

        if (isFill) {
            if ("input".equals(el.tag) && !"checkbox".equals(el.type) && !"radio".equals(el.type)) score += 20;
            if ("textarea".equals(el.tag)) score += 20;
            if ("label".equals(el.tag)) score += 5; 
        }
        if (isCheck) {
            if ("input".equals(el.tag) && ("checkbox".equals(el.type) || "radio".equals(el.type))) score += 50; 
        }
        if (isClick && ("button".equals(el.tag) || "a".equals(el.tag) || "submit".equals(el.type) || "radio".equals(el.type))) {
            score += 20;
        }
        
        return score;
    }
}
