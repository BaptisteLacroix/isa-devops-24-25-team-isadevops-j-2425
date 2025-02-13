package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEMmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerCartSaver;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerRegistration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerCatalog implements ICustomerRegistration, ICustomerFinder, ICustomerCartSaver {


    @Override
    public CustomerDTO register(String surname, String firstname, String email, String address) throws AlreadyUsedEMmailException {
        return null;
    }

    @Override
    public Optional<CustomerDTO> findCustomerByEmail(String customerEMail) throws UnknownCustomerEmailException {
        return Optional.empty();
    }

    @Override
    public Optional<CustomerDTO> findCustomerByCartNum(String cardNumber) throws UnknownCardNumberException {
        return Optional.empty();
    }

    @Override
    public List<CustomerDTO> findAll() {
        return List.of();
    }

    @Override
    public void setCart(String customerEMail, CartDTO cart) throws UnknownCustomerEmailException {

    }

    @Override
    public void emptyCart(String customerEMail) throws UnknownCustomerEmailException {

    }
}
