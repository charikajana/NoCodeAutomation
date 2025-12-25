package agent.intelligence;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the extracted intent from a natural language step.
 * Contains all semantic information needed for intelligent execution.
 */
public class StepIntent {
    
    private String originalStep;
    private String cleanStep;
    private IntentAnalyzer.ActionType actionType;
    private String targetDescription;
    private String value;
    private String elementType;
    private Map<String, String> modifiers;
    
    public StepIntent() {
        this.modifiers = new HashMap<>();
    }
    
    // Getters and Setters
    
    public String getOriginalStep() {
        return originalStep;
    }
    
    public void setOriginalStep(String originalStep) {
        this.originalStep = originalStep;
    }
    
    public String getCleanStep() {
        return cleanStep;
    }
    
    public void setCleanStep(String cleanStep) {
        this.cleanStep = cleanStep;
    }
    
    public IntentAnalyzer.ActionType getActionType() {
        return actionType;
    }
    
    public void setActionType(IntentAnalyzer.ActionType actionType) {
        this.actionType = actionType;
    }
    
    public String getTargetDescription() {
        return targetDescription;
    }
    
    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getElementType() {
        return elementType;
    }
    
    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
    
    public Map<String, String> getModifiers() {
        return modifiers;
    }
    
    public void setModifiers(Map<String, String> modifiers) {
        this.modifiers = modifiers;
    }
    
    public boolean hasModifier(String key) {
        return modifiers.containsKey(key);
    }
    
    public String getModifier(String key) {
        return modifiers.get(key);
    }
    
    @Override
    public String toString() {
        return String.format("StepIntent{action=%s, target='%s', value='%s', type='%s', modifiers=%s}",
            actionType, targetDescription, value, elementType, modifiers);
    }
}
