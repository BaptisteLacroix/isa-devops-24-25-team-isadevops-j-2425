package fr.univcotedazur.teamj.kiwicard.interfaces.customer;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEMmailException;


/**
 * Recherche et récupération de client
 */
public interface CustomerRegistration {
    CustomerDTO register(String surname, String firstname, String email, String address) throws AlreadyUsedEMmailException;

}
