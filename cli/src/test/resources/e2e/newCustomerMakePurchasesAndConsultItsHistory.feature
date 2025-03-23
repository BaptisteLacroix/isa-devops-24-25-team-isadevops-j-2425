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
\t18/03/2025 à 10:00   | Fleuriste            | 2 rose  2,00€        | 1.60€      | • 2 : Discount of 20.0% after 11:04 on all items
\t----------------------------------------------------------------------------------------------------
\t21/03/2025 à 11:57   | Boulange             | 1 croissant  1,00€   | 2,20€      |
\t                     |                      | 1 baguette  1,20€    |            |
\t----------------------------------------------------------------------------------------------------
\t21/03/2025 à 11:57   | Boulange             | 3 croissant  3,00€   | 3,00€      |
\t----------------------------------------------------------------------------------------------------
"""
