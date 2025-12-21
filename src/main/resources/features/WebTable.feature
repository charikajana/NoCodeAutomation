Feature: Checkbox Automation
  Scenario: Interact with Checkboxes
    Given Open the browser and go to "https://demoqa.com"
    When Click "Elements"
    And Click Web Tables
    And Click on Add button
    Then Verify "Registration Form" is displayed
    And fill the first name field with "John"
    And fill the last name field with "Doe"
    And fill the email field with "john.doe@example.com"
    And fill the age field with "30"
    And fill the salary field with "5000"
    And fill the department field with "IT"
    And click on Submit button
    Then Verify "John" is displayed
    And Verify "Doe" is displayed
    And Verify "john.doe@example.com" is displayed
    And Verify "30" is displayed
    And Verify "5000" is displayed
    And Verify "IT" is displayed


    