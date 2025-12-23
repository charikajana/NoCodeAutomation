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

  Scenario: Fill personal details with "also" delimiter
    When Enter "Sarah" in First Name also Enter "Williams" in Last Name also Enter "sarah.w@test.com" in Email
    And Enter "9876543211" in Mobile Number also Click on Female
    And Enter "23 Test Street, Test City" in Current Address
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser

  Scenario: Complete form with hobbies using "then"
    When Enter "Michael" in First Name then Enter "Johnson" in Last Name
    And Enter "michael.j@test.com" in Email then Enter "9876543212" in Mobile Number
    And Click on Male then Check Sports then Check Reading
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser

  Scenario: Female student with address using comma delimiter  
    When Enter "Emma" in First Name, Enter "Smith" in Last Name, Enter "emma.s@test.com" in Email
    And Click on Female, Enter "9876543213" in Mobile Number
    And Check Music, Check Reading
    And Enter "45 Main Road, Downtown" in Current Address
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser

  Scenario: Male student with multiple hobbies using ampersand
    When Enter "David" in First Name & Enter "Brown" in Last Name & Enter "david.b@test.com" in Email
    And Click on Male & Enter "9876543214" in Mobile Number
    And Check Sports & Check Music & Check Reading
    And Enter "78 Park Avenue, Uptown" in Current Address
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser

  Scenario: Quick name and email only
    When Enter "Alice" in First Name and Enter "Cooper" in Last Name
    And Enter "alice.c@test.com" in Email and Click on Female
    And Enter "9876543215" in Mobile Number
    And Click Submit
    Then Verify form submitted
    Then close browser

  Scenario: Other gender with hobbies
    When Enter "Taylor" in First Name also Enter "Morgan" in Last Name
    And Enter "taylor.m@test.com" in Email also Enter "9876543216" in Mobile Number
    And Click on Other also Check Sports also Check Music
    And Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser

  Scenario: Mix different delimiters in same scenario
    When Enter "Chris" in First Name and Enter "Anderson" in Last Name
    And Enter "chris.a@test.com" in Email, Enter "9876543217" in Mobile Number
    And Click on Male also Check Reading
    And Enter "12 Elm Street" in Current Address & Click Submit
    Then Verify "Thanks for submitting the form" is displayed
    Then close browser
