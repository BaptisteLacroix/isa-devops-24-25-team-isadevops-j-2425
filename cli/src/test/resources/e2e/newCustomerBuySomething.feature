Feature: Un nouveau client fait un achat

  Background: Initialize the database with simple data
    Given a simple dataset

  Scenario: Du registre du client jusqu'à l'achat final
    Given the client "pierre.dupont@example.com" is registered with surname "Dupont", firstname "Pierre" and address "123 rue de Paris"
    When the client adds item with id "5" and quantity 2 to the cart
    And the client applies perk with id "2"
    And the client pays the cart
    Then the purchase is successful

  Scenario: Un nouveau client fait un achat après avoir changé son panier
    Given the client "pierre.dupont@example.com" is registered with surname "Dupont", firstname "Pierre" and address "123 rue de Paris"
    When the client adds item with id "5" and quantity 2 to the cart
    And the client deletes item with id 5 from the cart
    When the client adds item with id "4" and quantity 3 to the cart
    And the client applies perk with id "3"
    And the client pays the cart
    Then the purchase is successful
    And the purchase contains 1 cartItem
    And purchase contains item with id "4" and quantity 3
