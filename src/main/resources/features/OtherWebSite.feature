Feature: Navigation Feature

  Scenario: Open link in new tab
    When I navigate to "https://vinothqaacademy.com/multiple-windows/"
    
    # Use the new robust action: Click AND Switch in one atomic step using waitForPopup logic
    When I click on New Browser Tab button and switch to new window
    And wait for 10 seconds
    And Enter Name as "Vinoth"
    And Enter Role as "QA"
    And Enter Email Address as "vinoth@vinoth.com"
    And Enter Location as "Chennai"
    And Enter Department as "IT"
    And click on Add Row button
    And Verify new row is added with "Vinoth" in Name column
    And wait for 10 seconds
    And take the screenshot
    And select the checkbox in the row where Name column value is "Vinoth"
    And click on Delete Selected Row button
    Then validate "Vinoth" is deleted from the table
    And wait for 10 seconds
    And take the screenshot

    