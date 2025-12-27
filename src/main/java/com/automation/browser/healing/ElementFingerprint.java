package com.automation.browser.healing;

import java.io.Serializable;
import java.util.Map;

/**
 * Stores a rich set of attributes and contextual information about an element
 * to enable re-location (healing) if primary selectors fail.
 */
public class ElementFingerprint implements Serializable {
    private static final long serialVersionUID = 1L;

    public String stepText;      // The NLP step text
    public String tag;
    public String id;
    public String name;
    public String text;
    public String className;
    public String xpath;
    public String frameAnchor;   // Which frame it belongs to
    public Map<String, String> attributes;
    
    // Scores and Metadata
    public double confidence;
    public long timestamp;

    public ElementFingerprint() {
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("Fingerprint(Step='%s', ID='%s', XPath='%s')", stepText, id, xpath);
    }
}
