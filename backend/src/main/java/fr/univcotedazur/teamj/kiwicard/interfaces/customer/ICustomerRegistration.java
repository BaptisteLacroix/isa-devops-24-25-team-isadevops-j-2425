package fr.univcotedazur.teamj.kiwicard.interfaces.customer;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubsbribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;


/**
 * Recherche et récupération de client
 */
public interface ICustomerRegistration {
    CustomerDTO register(CustomerSubsbribeDTO customerSubsbribeDTO) throws AlreadyUsedEmailException, UnreachableExternalServiceException;

}
