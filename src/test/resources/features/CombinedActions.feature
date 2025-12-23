Feature: Combined Actions - Student Registration Form
  Testing realistic combined actions on the DemoQA Practice Form
  
  Background:
    Given Navigate to "https://demoqa.com/automation-practice-form"

  Scenario: Quick registration with basic info using "and"
    When Enter "John" in First Name and Enter "Doe" in Last Name and Enter "john.doe@test.com" in Email
    And Click on Male and Enter "9876543210" in Mobile Number
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser
