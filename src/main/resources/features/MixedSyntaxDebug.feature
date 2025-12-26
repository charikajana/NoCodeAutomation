Feature: Test Mixed Syntax
  Debugging mixed "as" and "in" syntax  

  Scenario: Mixed syntax test
    Given Navigate to "https://demoqa.com/automation-practice-form"
    When Enter First Name as "Bob" and Enter "Johnson" in Last Name and Enter Email as "bob.j@example.com"
    And Take the Screenshot
    And Wait for 5 seconds
