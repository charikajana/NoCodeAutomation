Feature: Self-Healing Demo

  Scenario: Learn and Heal a Click Action
    Given Navigate to "https://demoqa.com/buttons"
    
    # 1. RECORD: First execution records the fingerprint for this exact step text
    When I double click "Double Click Me"
    Then Verify "You have done a double click" is displayed
    
    # 2. SIMULATE UI CHANGE: Change the text AND ID via JS so semantic matching fails
    And I execute js "let b = document.getElementById('doubleClickBtn'); b.id = 'broken'; b.innerText = 'Something Else'"
    
    # 3. HEAL: Try the SAME step again. Semantic matching fails, but it heals via XPath!
    When I double click "Double Click Me"
    Then Verify "You have done a double click" is displayed
