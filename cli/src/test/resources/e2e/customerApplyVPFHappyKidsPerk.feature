Feature: Un client réserve des créneaux horaires chez happy kids et applique le perks de vfp.
  Background: Initialize the database with simple data
    Given a simple dataset

  Scenario: Un client réserve des créneaux horaires chez happy kids et applique le perks de vfp.
    Given le client Pierre Cailloux est connecté en tant que client VFP avec son email "pierre.cailloux@cafaismal.auxpieds"
    When Pierre recherche le partner "HappyKids"
    Then Pierre consulte les avantages du partenaire "6" (HappyKids)
    And Il décide de réserver un créneau horaire grâce à l'item "21" à partir de maintenant à "8"h pour une durée de "2" heures
    And Il décide d appliquer l avantage "1" VFP et recoit bien le message suivant "Ajout de l'avantage ayant l'ID : 1 au client pierre.cailloux@cafaismal.auxpieds"
    Then Il observe que son panier est mis à jour avec le perks de VFP
    And Il décide de payer le panier et recoit un message de validation "Le panier a été validé avec succès ! Plus de détails :"
    And Le panier est bien vidé et recoit le message d erreur suivant "Aucun panier trouvé pour le client avec l'email pierre.cailloux@cafaismal.auxpieds"
