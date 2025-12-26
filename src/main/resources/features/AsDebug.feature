Feature: Test AS Syntax
  Debugging "as" syntax  

  Scenario: Test "as" syntax
    Given Navigate to "https://demoqa.com/automation-practice-form"
    When Enter First Name as "John" and Enter Last Name as "Doe"
    And Take the Screenshot
    And Wait for 5 seconds
