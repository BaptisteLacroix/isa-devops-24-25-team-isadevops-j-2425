package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

/**
 ** Modifie un achat ou un item acheté pour le tagger comme "consommé" dans
* l'utilisation d'un avantage. Les éléments consommables étendent la classe PerkConsumable
*/
public interface IPurchaseConsumer {

    void consumeNLastPurchaseoOfCustomer (int nbPurchasesToConsume, String customerEmail) throws UnknownCustomerEmailException;

    void consumeNLastPurchaseOfCustomerInPartner (int nbPurchasesToConsume, String customerEmail,
                                                  long partnerId);

    void consumeNLastItemsOfCustomerInPartner(long itemId, int nbItemsConsumed, String customerEmail) throws UnknownCustomerEmailException;
}
