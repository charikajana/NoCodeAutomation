package agent.browser.locator.core;

public class CandidateScorer {

    public double score(ElementCandidate el, String targetName, String parsedType) {
        String name = targetName.trim();
        String lowerName = name.toLowerCase();
        // pre-calculate clean name? Or do it here. 
        // Optimization: Clean name could be passed in, but doing it here keeps interface simple.
        // Enhanced clean name - remove common suffixes like "icon", "button", etc.
        String cleanName = lowerName.replaceAll("\\s+(button|btn|link|input|field|tab|icon)$", "").trim();

        boolean isFill = "input".equals(parsedType);
        boolean isCheck = "check".equals(parsedType) || "checkbox".equals(parsedType) || "radio".equals(parsedType);
        boolean isClick = "button".equals(parsedType) || "click".equals(parsedType);
        
        double score = 0.0;
        String text = el.text.trim();
        String lowerText = text.toLowerCase();
        String lowerTitle = el.title.toLowerCase();
        String lowerLabel = el.label.toLowerCase();
        
        // ========== TIER 1: EXACT MATCHES (140-150 points) ==========
        if (name.equalsIgnoreCase(text) || cleanName.equalsIgnoreCase(lowerText)) score += 150; 
        if (name.equalsIgnoreCase(el.label) || cleanName.equalsIgnoreCase(lowerLabel)) score += 150;
        if (name.equalsIgnoreCase(el.placeholder) || cleanName.equalsIgnoreCase(el.placeholder)) score += 140;
        if (name.equalsIgnoreCase(el.title) || cleanName.equalsIgnoreCase(lowerTitle)) score += 140;
        
        // ========== TIER 2: BIDIRECTIONAL CONTAINS (100-120 points) ==========
        // User says "Edit Icon" → Element has title="Edit" ✅
        if (!el.title.isEmpty() && lowerName.contains(lowerTitle)) score += 120;
        if (!el.label.isEmpty() && lowerName.contains(lowerLabel)) score += 120;
        
        // User says "Edi" → Element has title="Edit" ✅ (reverse partial)
        if (!el.title.isEmpty() && lowerTitle.contains(lowerName)) score += 110;
        if (!el.label.isEmpty() && lowerLabel.contains(lowerName)) score += 110;
       
        // User says "Edi" → Element text contains search term ✅  
        if (!text.isEmpty() && lowerText.contains(lowerName)) score += 110;
        if (!text.isEmpty() && lowerText.contains(cleanName)) score += 105;

        if (name.equalsIgnoreCase(el.id) || cleanName.equalsIgnoreCase(el.id)) score += 100;
        if (name.equalsIgnoreCase(el.name) || cleanName.equalsIgnoreCase(el.name)) score += 100;

        if (score < 100) {
             if (text.toLowerCase().contains(lowerName) || text.toLowerCase().contains(cleanName)) score += 40; 
             if (el.title.toLowerCase().contains(lowerName) || el.title.toLowerCase().contains(cleanName)) score += 35; // Tooltip contains
             // Using simple string contains or custom util if available. 
             // Assuming agent.util.FuzzyMatch is available as per original code.
             try {
                if (agent.util.FuzzyMatch.ratio(name, text) > 85 || agent.util.FuzzyMatch.ratio(cleanName, text) > 85) score += 30; 
                if (agent.util.FuzzyMatch.ratio(name, el.id) > 85 || agent.util.FuzzyMatch.ratio(cleanName, el.id) > 85) score += 30;
                if (agent.util.FuzzyMatch.ratio(name, el.title) > 85 || agent.util.FuzzyMatch.ratio(cleanName, el.title) > 85) score += 30; // Fuzzy tooltip match
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
