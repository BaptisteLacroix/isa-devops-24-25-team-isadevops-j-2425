package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerCartSaver;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerRegistration;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerCatalog implements ICustomerRegistration, ICustomerFinder, ICustomerCartSaver {

    ICustomerRepository customerRepository;

    @Override
    public CustomerDTO register(CustomerSubscribeDTO customerSubsbribeDTO) throws AlreadyUsedEmailException, UnreachableExternalServiceException {
        if (customerRepository.findByEmail(customerSubsbribeDTO.email()) != null) {
            throw new AlreadyUsedEmailException();
        }
        Customer customer = new Customer(customerSubsbribeDTO.firstName(), customerSubsbribeDTO.surname(), customerSubsbribeDTO.address(), customerSubsbribeDTO.email(), false);
        customerRepository.save(customer);
        return new CustomerDTO(customer);
    }

    @Override
    public CustomerDTO findCustomerByEmail(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        return new CustomerDTO(customer);
    }

    @Override
    public CustomerDTO findCustomerByCardNum(String cardNumber) throws UnknownCardNumberException {
        Customer customer = customerRepository.findByCardNumber(cardNumber);
        if (customer == null) {
            throw new UnknownCardNumberException();
        }
        return new CustomerDTO(customer);
    }

    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> new CustomerDTO(customer.getEmail(), customer.getFirstName(), customer.getSurname(), customer.isVfp() ? "true" : "false"))
                .collect(Collectors.toList());
    }

    @Override
    public void setCart(String customerEmail, CartDTO cart) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        customer.setCart(new Cart(cart));
    }

    @Override
    public void emptyCart(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        customer.getCart().empty();
    }
}
