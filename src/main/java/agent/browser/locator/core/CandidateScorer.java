package agent.browser.locator.core;

import agent.utils.FuzzyMatch;

public class CandidateScorer {

    public double score(ElementCandidate el, String targetName, String parsedType) {
        String name = targetName.trim();
        String lowerName = name.toLowerCase();
        // pre-calculate clean name? Or do it here. 
        // Optimization: Clean name could be passed in, but doing it here keeps interface simple.
        // Enhanced clean name - remove common suffixes like "icon", "button", etc.
        String cleanName = lowerName;
        boolean changed;
        do {
            changed = false;
            String before = cleanName;
            cleanName = cleanName.replaceAll("\\s+(button|btn|link|input|field|tab|icon|radio|checkbox|dropdown|select|box|menu|card|item|element|option|header|title|label|slider|range)$", "").trim();
            if (!before.equals(cleanName)) changed = true;
        } while (changed);

        boolean isFill = "input".equals(parsedType);
        boolean isCheck = "check".equals(parsedType) || "checkbox".equals(parsedType) || "radio".equals(parsedType);
        boolean isClick = "button".equals(parsedType) || "click".equals(parsedType);
        boolean isSlider = "slider".equals(parsedType);
        
        double score = 0.0;
        String text = el.text.trim();
        String lowerText = text.toLowerCase();
        String lowerTitle = el.title.toLowerCase();
        String lowerLabel = el.label.toLowerCase();
        String lowerRole = el.role.toLowerCase();
        String lowerType = el.type.toLowerCase();
        
        // ========== TIER 1: EXACT MATCHES (140-150 points) ==========
        boolean matchedExact = false;
        if (name.equalsIgnoreCase(text) || cleanName.equalsIgnoreCase(lowerText)) {
            score += 150;
            matchedExact = true;
        } else if (name.equalsIgnoreCase(el.label) || cleanName.equalsIgnoreCase(lowerLabel)) {
            score += 150;
            matchedExact = true;
        } else if (name.equalsIgnoreCase(el.placeholder) || cleanName.equalsIgnoreCase(el.placeholder)) {
            score += 140;
            matchedExact = true;
        } else if (name.equalsIgnoreCase(el.title) || cleanName.equalsIgnoreCase(lowerTitle)) {
            score += 140;
            matchedExact = true;
        }
        
        // ========== TIER 2: BIDIRECTIONAL CONTAINS (100-120 points) ==========
        if (!matchedExact) {
            if (!el.title.isEmpty() && (lowerName.contains(lowerTitle) || lowerTitle.contains(lowerName))) {
                score += 120;
            } else if (!el.label.isEmpty() && (lowerName.contains(lowerLabel) || lowerLabel.contains(lowerName))) {
                score += 120;
            } else if (!text.isEmpty() && (lowerText.contains(lowerName) || lowerText.contains(cleanName))) {
                score += 110;
            } else if (name.equalsIgnoreCase(el.id) || cleanName.equalsIgnoreCase(el.id)) {
                score += 100;
            } else if (name.equalsIgnoreCase(el.name) || cleanName.equalsIgnoreCase(el.name)) {
                score += 100;
            }
        }

        // Fuzzy matching only if no solid match yet
        if (score < 100) {
             if (text.toLowerCase().contains(lowerName) || text.toLowerCase().contains(cleanName)) score += 40; 
             if (el.title.toLowerCase().contains(lowerName) || el.title.toLowerCase().contains(cleanName)) score += 35; // Tooltip contains
             
             try {
                if (FuzzyMatch.ratio(name, text) > 85 || FuzzyMatch.ratio(cleanName, text) > 85) score += 30;
                if (FuzzyMatch.ratio(name, el.id) > 85 || FuzzyMatch.ratio(cleanName, el.id) > 85) score += 30;
                if (FuzzyMatch.ratio(name, el.title) > 85 || FuzzyMatch.ratio(cleanName, el.title) > 85) score += 30;
             } catch (Throwable t) {
             }
        }

        // ========== TIER 3: TYPE-SPECIFIC BOOSTS AND PENALTIES ==========
        if (isFill) {
            String tag = el.tag.toLowerCase();
            if (("input".equals(tag) || "textarea".equals(tag)) && !"checkbox".equals(el.type) && !"radio".equals(el.type) && !"range".equals(el.type)) {
                score += 50;
            } else {
                score -= 100; // Penalize non-inputs for FILL
            }
        }
        if (isCheck) {
            if ("input".equals(el.tag) && ("checkbox".equals(el.type) || "radio".equals(el.type))) {
                score += 100;
            } else {
                score -= 100; // Penalize non-checkboxes for CHECK
            }
        }
        if (isClick) {
            if ("button".equals(el.tag) || "a".equals(el.tag) || "submit".equals(el.type) || el.className.contains("btn") || "button".equals(lowerRole)) {
                score += 50;
            }
            // No penalty for click as almost anything can be clicked
        } else {
            // General boost for interactive tags even if action type is unknown
            if ("button".equals(el.tag) || "a".equals(el.tag) || "submit".equals(el.type) || "button".equals(lowerRole)) {
                score += 10;
            }
        }
        if (isSlider) {
            if ("range".equals(lowerType) || "slider".equals(lowerRole)) {
                score += 500; // Huge boost for ACTUAL sliders
            } else if (el.className.toLowerCase().contains("slider") || el.className.toLowerCase().contains("range")) {
                score += 100; // Moderate boost for potential custom sliders
            } else {
                score -= 200; // HEAVY penalty for non-slider elements when a slider is requested
            }
        }
        if ("progressbar".equals(lowerType) || "progressbar".equals(lowerRole)) {
            score += 500; // Found exact role
        } else if (el.className.toLowerCase().contains("progress")) {
            score += 100; // Likely a progress component
        }
        
        // ========== TIER 4: VISIBILITY BOOST ==========
        // Substantial boost for visible elements to prefer them over hidden duplicates (e.g., mobile menus)
        if (el.visible) {
            score += 100;
        }
        
        return score;
    }
}
