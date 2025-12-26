Feature: Composite Debug - Isolate Value Misalignment
  Testing specific scenario to understand value assignment

  Scenario: Test "in" syntax (User reported issue)
    Given Navigate to "https://demoqa.com/automation-practice-form"
    When Enter "Alice" in First Name and Enter "Smith" in Last Name and Enter "alice.smith@test.com" in Email
    And Take the Screenshot
    And Wait for 5 seconds
