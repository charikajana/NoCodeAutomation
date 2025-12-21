Feature: Wait Command Variations Test
  
  Background:
    Given Open the browser and go to "https://demoqa.com/dynamic-properties"
  
  @wait @time-based
  Scenario: Time-based wait variations
    # Test different time-based wait formats
    When wait for 2 seconds
    And pause for 1 sec
    And wait 3s
    Then Verify "This text has random Id" is displayed
  
  @wait @page-load
  Scenario: Page load wait variations
    # Test different page load wait formats
    When Click "Home"
    And wait for page load
    And Click "Elements"
    And wait for page to be loaded
    And Click "Text Box"
    And wait for page load completed
    Then Verify "Full Name" is displayed
  
  @wait @element-appear
  Scenario: Element appearance wait variations
    # Test waiting for elements to appear
    When wait for "Enable After 5 Seconds" to appear
    And wait until "Color Change" is visible
    And wait for "Visible After 5 Seconds" to be displayed
    Then Verify "Visible After 5 Seconds" is displayed
  
  @wait @element-disappear  
  Scenario: Element disappearance wait variations
    # Note: This demo site doesn't have disappearing elements
    # This is just to show the syntax
    Given Open the browser and go to "https://demoqa.com/progress-bar"
    When Click "Start"
    And wait for "Start" to disappear
    And wait 3 seconds
    And Click "Stop"
    Then Verify "Reset" is displayed
  
  @wait @complex-workflow
  Scenario: Complex workflow with multiple wait types
    Given Open the browser and go to "https://demoqa.com/text-box"
    When Fill "Full Name" with "John Doe"
    And Fill "Email" with "john@example.com"
    And wait for 1 second
    And Fill "Current Address" with "123 Main St"
    And wait 1 sec
    And Click "Submit"
    And wait for page load
    And wait for "name:John Doe" to appear
    Then Verify "john@example.com" is displayed
  
  @wait @all-variations
  Scenario: Demonstration of all wait pattern variations
    # Time-based variations
    When wait for 1 seconds
    And wait for 1 sec
    And wait 1s
    And pause for 1 second
    And pause 1 sec
    
    # Page load variations (commented to avoid actual execution)
    # And wait for page load
    # And wait for page to load
    # And wait for page to be loaded
    # And wait for page load completed
    # And wait for network idle
    # And wait for page ready
    
    # Element appear variations (using actual elements on page)
    And wait for "This text has random Id" to appear
    And wait until "Enable After 5 Seconds" is visible
    And wait for "Color Change" to be displayed
    
    # Element disappear variations (commented - no disappearing elements)
    # And wait for "Loading" to disappear
    # And wait for "Spinner" to hide
    # And wait until "Processing" is gone
    
    Then Verify "Dynamic Properties" is displayed
