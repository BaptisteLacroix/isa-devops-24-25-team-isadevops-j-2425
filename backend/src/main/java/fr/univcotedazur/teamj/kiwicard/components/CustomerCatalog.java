package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.connectors.CardEditorProxy;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerCatalog implements ICustomerRegistration, ICustomerFinder, ICustomerCartSaver {

    ICustomerRepository customerRepository;

    CardEditorProxy cardEditorProxy;


    @Autowired
    public CustomerCatalog(ICustomerRepository customerRepository, CardEditorProxy cardEditorProxy) {
        this.customerRepository = customerRepository;
        this.cardEditorProxy = cardEditorProxy;
    }


    @Override
    public CustomerDTO register(CustomerSubscribeDTO customerSubscribeDTO) throws AlreadyUsedEmailException, UnreachableExternalServiceException {
        if (customerRepository.findByEmail(customerSubscribeDTO.email()) != null) {
            throw new AlreadyUsedEmailException();
        }
        CardDTO cardDto = cardEditorProxy.orderACard(customerSubscribeDTO.email(), customerSubscribeDTO.address());
        Customer customer = new Customer(customerSubscribeDTO, cardDto.cardNumber());
        customerRepository.save(customer);
        return new CustomerDTO(customer);
    }

    @Override
    public CustomerDTO findCustomerDTOByEmail(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        return new CustomerDTO(customer);
    }

    public Customer findCustomerByEmail(String customerEmail) throws UnknownCustomerEmailException {
        return customerRepository.findByEmail(customerEmail).orElseThrow(UnknownCustomerEmailException::new);
    }

    @Override
    public CustomerDTO findCustomerByCardNum(String cardNumber) throws UnknownCardNumberException {
        Customer customer = customerRepository.findByCardNumber(cardNumber).orElse(null);
        if (customer == null) {
            throw new UnknownCardNumberException();
        }
        return new CustomerDTO(customer);
    }

    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDTO::new)
                .toList();

    }

    @Override
    public void setCart(String customerEmail, CartDTO cartDto) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        customer.setCart(new Cart(cartDto));
        customerRepository.save(customer);
    }

    @Override
    public void emptyCart(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException();
        }
        customer.getCart().empty();
        customerRepository.save(customer);
    }
}
