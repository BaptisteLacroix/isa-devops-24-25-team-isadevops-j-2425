Feature: Un client réserve des créneaux horaires chez happy kids et applique le perks de vfp.
  Background: Initialize the database with simple data
    Given [Pierre HappyKids] a simple dataset

  Scenario: Un client réserve des créneaux horaires chez happy kids et applique le perks de vfp.
    Given le client Pierre Cailloux est connecté en tant que client VFP avec son email "pierre.cailloux@cafaismal.auxpieds"
    When Pierre recherche le partner "HappyKids"
    Then Pierre consulte les avantages du partenaire "6" (HappyKids)
    And Il décide de réserver un créneau horaire grâce à l'item "21" à partir de maintenant à "8"h pour une durée de "2" heures
    Then Il observe que son panier est mis à jour avec le perks de VFP
    And Il se rend compte qu il s est trompé dans l horaire et décide de supprimer l item "21" de son panier
    And Il décide de réserver un nouveau créneau horaire grâce à l item "21" à partir de maintenant à "10"h pour une durée de "2" heures
    Then Il observe que son panier est mis à jour avec la nouvelle horaire et le perk
    And Il décide de payer le panier et recoit un message de validation "Le panier a été validé avec succès ! Plus de détails :"
    And Le panier est bien vidé et recoit le message d erreur suivant "Aucun panier trouvé pour le client avec l'email pierre.cailloux@cafaismal.auxpieds"
    Then Le client Pierre Cailloux consulte son historique d'achat et voit les achats effectués
    """
Historique du client pierre.cailloux@cafaismal.auxpieds :
\tDate                 | Commerçant           | Articles             | Total payé | Avantages
\t----------------------------------------------------------------------------------------------------
\tDATE_PLACEHOLDER   | HappyKids            | 2 Heure de garde HappyKids  20,00€ | 19,00€     | • 1 : 5.0% discount for all VFPs when booking between TIME_PLACEHOLDERh and TIME_PLACEHOLDERh
\t----------------------------------------------------------------------------------------------------
"""

