Feature: Un partenaire fait des ventes et consulte son historique

  Background:
    Given a simple dataset
    And the client "jacques-dupont@gmail.com" is registered with surname "Dupont", firstname "Jacques" and address "Villeneuve"
    And the client adds item with id "9" and quantity 3 to the cart
    And the client pays the cart

    And the client "jean.pierre@gmail.com" is registered with surname "Pierre", firstname "Jean" and address "Cagnes"
    And the client adds item with id "11" and quantity 1 to the cart
    And the client adds item with id "9" and quantity 2 to the cart
    And the client pays the cart

    And the client "robert.chef@gmail.com" is registered with surname "Chef", firstname "Robert" and address "Nice"
    And the client adds item with id "10" and quantity 5 to the cart
    And the client applies perk with id "3"
    And the client pays the cart


  Scenario: Le client consulte son historique d'achats
    When the partner "3" consults his purchase history
    Then the partner "3" sees the following history
"""
Historique du partenaire Boucherie :
\tDate                 | Articles             | Total payé | Avantages
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | 6 saucisse  7,20€    | 6,00€      | • 3 : Buy 5 saucisse and get 1 for free
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | 1 jambon  1,50€      | 3,50€      |
\t                     | 2 steak  2,00€       |            |
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | 3 steak  3,00€       | 3,00€      |
\t----------------------------------------------------------------------------------------------------
"""
