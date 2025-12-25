Feature: Hover and Mouse Interactions
  Testing hover menus, tooltips, dropdown menus, and mouse-triggered events

  Background:
    Given Navigate to "https://demoqa.com/menu"

  Scenario: Hover to show tooltip
    When I hover over "Info" icon
    Then Verify tooltip "More information" appears
    And Check tooltip positioned near icon
    When I move mouse away
    Then Verify tooltip disappears

  Scenario: Hover to reveal dropdown menu
    When user hovers over "Products" menu
    Then Verify dropdown menu appears below
    And Check menu contains "Software, Hardware, Services"
    When I hover over "Software"
    Then Verify submenu expands to right
    And Check submenu items are visible

  Scenario: Hover to show hidden buttons
    When I hover over product card
    Then Verify "Add to Cart" button appears
    And Check "Quick View" button appears
    And Verify buttons are clickable
    When mouse leaves card
    Then Verify buttons fade out

  Scenario: Nested hover menus
    When I hover over "Main Menu"
    And I hover over "Submenu 1"
    And I hover over "Sub-submenu A"
    Then Verify all three levels are visible
    And Check correct menu path is highlighted

  Scenario: Hover intent detection
    When I quickly move mouse across menu items
    Then Verify menus don't flicker or flash
    And Check only intentional hover triggers menu
    When I pause over "Settings"
    Then Verify menu opens after brief delay

  Scenario: Tooltip with rich content
    When I hover over chart data point
    Then Verify tooltip shows detailed information
    And Check tooltip contains date, value, percentage
    And Verify tooltip has formatted HTML content

  Scenario: Hover to preview image
    When user hovers over thumbnail
    Then Verify larger preview appears
    And Check preview shows full-size image
    And Verify zoom effect applied
    When I move to next thumbnail
    Then Verify preview updates to new image

  Scenario: Hover to highlight related elements
    When I hover over table row
    Then Verify row is highlighted
    And Check related rows also highlight
    When I hover over column header
    Then Verify entire column highlights

  Scenario: Contextual menu on right-click
    When I right-click on text selection
    Then Verify context menu appears
    And Check menu contains "Copy, Cut, Paste, Delete"
    When I click "Copy"
    Then Verify menu closes
    And Check text is copied

  Scenario: Hover effects with CSS transitions
    When I hover over animated button
    Then Verify background color transitions smoothly
    And Check scale transform applied
    And Verify shadow increases
    When mouse leaves button
    Then Verify button returns to normal state smoothly
