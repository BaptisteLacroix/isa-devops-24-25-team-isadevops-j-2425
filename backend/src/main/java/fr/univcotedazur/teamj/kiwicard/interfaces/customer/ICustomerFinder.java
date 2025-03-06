package fr.univcotedazur.teamj.kiwicard.interfaces.customer;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;

import java.util.List;


/**
 * Recherche et récupération de client
 */
public interface ICustomerFinder {
    CustomerDTO findCustomerDTOByEmail(String customerEMail) throws UnknownCustomerEmailException;

    Customer findCustomerByEmail(String customerEmail) throws UnknownCustomerEmailException;

    CustomerDTO findCustomerByCardNum(String cardNumber) throws UnknownCardNumberException;
    List<CustomerDTO> findAll();

}
