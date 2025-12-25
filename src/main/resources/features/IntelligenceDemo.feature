Feature: Intelligence Layer Demo
  Testing the intelligent NLP-based step parsing

  Scenario: Demonstrate Intelligent Parsing
    Given I navigate to "https://demoqa.com/text-box"
    
    # These steps will be parsed by the Intelligence Layer:
    When I enter "John Doe" in full name
    And I type "john@example.com" in email
    And I fill "123 Main St" in current address
    And I press submit button
    
    Then Verify "John Doe" is displayed
    And Check "john@example.com" message is visible
