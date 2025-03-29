Feature: Un nouveau client fait des achats et consulte son historique

  Background:
    Given a simple dataset
    And the client "pierre.dupont@gmail.com" is registered with surname "Dupont", firstname "Pierre" and address "321 rue de Paris"
    And the client adds item with id "1" and quantity 3 to the cart
    And the client pays the cart

    And the client adds item with id "2" and quantity 1 to the cart
    And the client adds item with id "1" and quantity 1 to the cart
    And the client pays the cart

    And the client adds item with id "5" and quantity 2 to the cart
    And the client applies perk with id "2"
    And the client pays the cart


  Scenario: Le client consulte son historique d'achats
    When the client "pierre.dupont@gmail.com" consults his purchase history
    Then the client "pierre.dupont@gmail.com" sees the following history
"""
Historique du client pierre.dupont@gmail.com :
\tDate                 | Commerçant           | Articles             | Total payé | Avantages
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | Fleuriste            | 2 rose  2,00€        | 1,20€      | • 2 : Discount of 20.0% after TIME_PLACEHOLDER on all items
\t|                      |                      |            | • 2 : Discount of 20.0% after TIME_PLACEHOLDER on all items
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | Boulange             | 1 croissant  1,00€   | 1,76€      | • 3 : Discount of 10.0% after TIME_PLACEHOLDER on all items
\t|                      | 1 baguette  1,20€    |            | • 3 : Discount of 10.0% after TIME_PLACEHOLDER on all items
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | Boulange             | 3 croissant  3,00€   | 2,70€      | • 3 : Discount of 10.0% after TIME_PLACEHOLDER on all items
\t----------------------------------------------------------------------------------------------------
"""
