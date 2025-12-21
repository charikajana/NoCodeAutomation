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
    Then Verify New Row is added with "John" in First Name column
    And take the ScreenShot
    And wait for 20 seconds
    # Extract all column values from the row where First Name is "John"
    And get all column values where "First Name" is "John"
    And Close the browser



    