Feature: Modern UI Components Testing
  Testing accordions, tabs, carousels, sliders, progress bars, and other modern UI elements

  Background:
    Given Navigate to "https://demoqa.com/widgets"

  Scenario: Accordion expand and collapse
    When I click on "Section 1" accordion header
    Then Verify "Section 1" content expands
    And Check other sections remain collapsed
    When I click on "Section 2" header
    Then Verify "Section 1" auto-collapses
    And Check "Section 2" expands
    When I click "Section 2" again
    Then Verify "Section 2" collapses
    And Check all sections are collapsed

  Scenario: Multiple accordions can be open
    When accordion allows multiple open
    And I expand "Panel A"
    And I expand "Panel B"
    And I expand "Panel C"
    Then Verify all 3 panels are expanded
    And Check each shows its content

  Scenario: Tab navigation
    When I click "Profile" tab
    Then Verify "Profile" content displays
    And Check "Profile" tab is active
    And Verify other tabs are inactive
    When I click "Settings" tab
    Then Verify content switches to "Settings"
    And Check "Settings" tab is now active

  Scenario: Tab navigation with URL hash
    When I click "Account" tab
    Then Verify URL contains "#account"
    When I refresh page
    Then Verify "Account" tab is still active
    And Check correct content is displayed

  Scenario: Carousel/Slider automatic rotation
    When carousel loads
    Then Verify first slide is visible
    And Wait 5 seconds
    And Verify carousel auto-advances to slide 2
    And Wait 5 seconds
    And Check carousel shows slide 3

  Scenario: Carousel manual navigation
    When I click next arrow on carousel
    Then Verify next slide appears
    When I click previous arrow
    Then Verify previous slide shows
    When I click slide indicator 3
    Then Verify carousel jumps to slide 3

  Scenario: Progress bar update
    When I start file upload
    Then Verify progress bar appears at 0%
    And Wait for progress to update
    And Check progress increases gradually
    And Verify progress reaches 100%
    And Check success message appears

  Scenario: Progress bar with steps
    When multi-step process starts
    Then Verify step 1 is marked active
    When step 1 completes
    Then Verify step 1 marked complete
    And Check step 2 becomes active
    And Verify progress bar at 50%

  Scenario: Toast notifications
    When I click "Show Notification"
    Then Verify toast appears at top-right
    And Check notification has success icon
    And Verify message is "Action completed successfully"
    And Wait for toast to auto-dismiss after 3 seconds

  Scenario: Toggle switch
    When I toggle dark mode switch
    Then Verify switch moves to "on" position
    And Check theme changes to dark
    When I toggle switch again
    Then Verify switch returns to "off"
    And Check theme reverts to light

  Scenario: Slider/Range input
    When I drag slider to position 75
    Then Verify value displays as "75"
    And Check slider thumb at correct position
    When I click at position 30 on slider
    Then Verify slider jumps to 30
    And Check value updates to "30"

  Scenario: Badges and chips
    When I add tag "JavaScript"
    Then Verify chip appears with text "JavaScript"
    And Check chip has remove icon
    When I click remove on chip
    Then Verify chip disappears
    And Check tag is removed from list

  Scenario: Tooltip positioning
    When I hover over element near top edge
    Then Verify tooltip appears below element
    When I hover over element near bottom
    Then Verify tooltip appears above element
    And Check tooltip doesn't overflow viewport

  Scenario: Expandable panel
    When I click "Show More" on panel
    Then Verify panel expands with animation
    And Check additional content is visible
    And Verify button text changes to "Show Less"
    When I click "Show Less"
    Then Verify panel collapses smoothly

  Scenario: Sidebar drawer
    When I click hamburger menu icon
    Then Verify sidebar slides in from left
    And Check overlay appears behind sidebar
    When I click overlay
    Then Verify sidebar slides out
    And Check overlay disappears

  Scenario: Infinite scroll with loader
    When I scroll to bottom of feed
    Then Verify loading spinner appears
    And Wait for new content to load
    And Check 20 new items are added
    And Verify user can continue scrolling

  Scenario: Card layout with actions
    When I hover over product card
    Then Verify action buttons appear
    When I click "Add to Wishlist" on card
    Then Verify heart icon becomes filled
    And Check "Added to wishlist" toast appears

  Scenario: Stepper component
    When registration wizard loads
    Then Verify step 1 is active
    And Check steps 2-4 are disabled
    When I complete step 1 and click next
    Then Verify step 2 becomes active
    And Check step 1 is marked complete
    And Verify back button is enabled
