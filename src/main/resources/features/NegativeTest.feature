Feature: Negative Verification Test
  Scenario: Test failure handling with element visibility check
    Given Open the browser and go to "https://demoqa.com"
    When Click "Elements"
    # This verification will pass - element IS visible
    Then Verify "Elements" is displayed
    # This will fail - "Elements" text IS visible but we're verifying it's NOT displayed
    Then Verify "Elements" not displayed
    # These steps should be SKIPPED after the above failure
    And Click "Text Box"
    And fill the "Full Name" field with "Test User"
    And Close the browser
