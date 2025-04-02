package fr.univcotedazur.teamj.kiwicard.interfaces.purchase;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;

/**
 ** Modifie un achat ou un item acheté pour le tagger comme "consommé" dans
* l'utilisation d'un avantage. Les éléments consommables étendent la classe PerkConsumable
*/
public interface IPurchaseConsumer {

    void consumeNLastPurchaseOfCustomer(CustomerDTO customer, int nbPurchasesToConsume) throws UnknownCustomerEmailException;

    void consumeNLastPurchaseOfCustomerInPartner (String customerEmail, Long partnerId, int nbPurchasesToConsume) throws UnknownCustomerEmailException, UnknownPartnerIdException;

    void consumeNLastItemsOfCustomerInPartner(int nbItemsConsumed, String customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException;
}
