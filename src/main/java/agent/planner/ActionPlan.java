package agent.planner;

public class ActionPlan {

    private String actionType;      
    private String target;          
    private String value;           
    private String keyword;         
    private int priority = 0;       
    private String elementName;     
    private String locatorStrategy; 
    private boolean executed = false;  
    private String rowAnchor;

    public ActionPlan(String actionType, String target) {
        this.actionType = actionType;
        this.target = target;
    }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getLocatorStrategy() { return locatorStrategy; }
    public void setLocatorStrategy(String locatorStrategy) { this.locatorStrategy = locatorStrategy; }

    public boolean isExecuted() { return executed; }
    public void setExecuted(boolean executed) { this.executed = executed; }

    public String getElementName() { return elementName; }
    public void setElementName(String elementName) { this.elementName = elementName; }

    public String getRowAnchor() { return rowAnchor; }
    public void setRowAnchor(String rowAnchor) { this.rowAnchor = rowAnchor; }

    @Override
    public String toString() {
        return "ActionPlan{" +
                "actionType='" + actionType + '\'' +
                ", elementName='" + elementName + '\'' +
                ", target='" + target + '\'' +
                ", value='" + value + '\'' +
                ", keyword='" + keyword + '\'' +
                ", priority=" + priority +
                ", locatorStrategy='" + locatorStrategy + '\'' +
                ", rowAnchor='" + rowAnchor + '\'' +
                ", executed=" + executed +
                '}';
    }
}
