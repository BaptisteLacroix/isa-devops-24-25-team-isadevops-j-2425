package fr.univcotedazur.teamj.kiwicard.interfaces.customer;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.List;
import java.util.Optional;


/**
 * Recherche et récupération de client
 */
public interface ICustomerFinder {
    Optional<CustomerDTO> findCustomerByEmail(String customerEMail) throws UnknownCustomerEmailException;
    Optional<CustomerDTO> findCustomerByCartNum(String cardNumber) throws UnknownCardNumberException;
    List<CustomerDTO> findAll();

}
