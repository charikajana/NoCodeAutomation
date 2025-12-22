Feature: Dropdown/Select Menu Automation

  Scenario: Interact with standard and custom dropdowns
    Given Open the browser and go to "https://demoqa.com/select-menu"
    
    # Standard Select
    When Select "Blue" from "Old Style Select Menu"
    Then Verify "Blue" is displayed
    
    When select "Group 1, option 1" from Select Value
    Then Verify "Group 1, option 1" is displayed

    When select "Mr." from Select One
    Then Verify "Mr." is displayed


    # Variations
    And choose "Yellow" in "Old Style Select Menu"
    Then Verify "Yellow" is displayed
    
    And set "Old Style Select Menu" to "Green"
    Then Verify "Green" is displayed

    # Take screenshot to verify
    And take the screenshot
