package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.connectors.CardEditorProxy;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
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
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.IVfpStatus;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class CustomerCatalog implements ICustomerRegistration, ICustomerFinder, ICustomerCartSaver, IVfpStatus {

    ICustomerRepository customerRepository;

    CardEditorProxy cardEditorProxy;

    private final int nbPurchaseRequired;

    @Autowired
    public CustomerCatalog(ICustomerRepository customerRepository, CardEditorProxy cardEditorProxy, @Value("${kiwi-card.vfp-status.purchase-required}") int nbPurchaseRequired) {
        this.customerRepository = customerRepository;
        this.cardEditorProxy = cardEditorProxy;
        this.nbPurchaseRequired = nbPurchaseRequired;
    }

    /**
     * Enregistre un nouveau client dans la base de données et lui attribue une carte
     *
     * @param customerSubscribeDTO les informations du client à enregistrer
     * @return le DTO du client enregistré
     * @throws AlreadyUsedEmailException           si l'adresse email est déjà utilisée
     * @throws UnreachableExternalServiceException si le service externe est injoignable
     */
    @Override
    @Transactional
    public CustomerDTO register(CustomerSubscribeDTO customerSubscribeDTO) throws AlreadyUsedEmailException, UnreachableExternalServiceException {
        if (customerRepository.findByEmail(customerSubscribeDTO.email()).isPresent()) {
            throw new AlreadyUsedEmailException(customerSubscribeDTO.email());
        }
        CardDTO cardDto = cardEditorProxy.orderACard(customerSubscribeDTO.email(), customerSubscribeDTO.address());
        Customer customer = new Customer(customerSubscribeDTO, cardDto.cardNumber());
        customerRepository.save(customer);
        return new CustomerDTO(customer);
    }

    @Override
    @Transactional
    public Customer findCustomerByEmail(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException(customerEmail);
        }
        return customer;
    }

    @Override
    @Transactional
    public CustomerDTO findCustomerDTOByEmail(String customerEMail) throws UnknownCustomerEmailException {
        return new CustomerDTO(findCustomerByEmail(customerEMail));
    }

    @Override
    @Transactional
    public CustomerDTO findCustomerByCardNum(String cardNumber) throws UnknownCardNumberException {
        Customer customer = customerRepository.findByCardNumber(cardNumber).orElse(null);
        if (customer == null) {
            throw new UnknownCardNumberException(cardNumber);
        }
        return new CustomerDTO(customer);
    }

    @Override
    @Transactional
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDTO::new)
                .toList();

    }

    /**
     * Enregistre le panier d'un client dans la base de données en remplaçant l'ancien panier s'il existe
     * @param customerEmail l'adresse email du client
     * @param cart le panier à enregistrer
     * @return le client mis à jour
     * @throws UnknownCustomerEmailException si l'adresse email n'est pas reconnue
     */
    @Override
    @Transactional
    public Customer setCart(String customerEmail, Cart cart) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException(customerEmail);
        }
        customer.setCart(cart);
        return customerRepository.save(customer);
    }

    /**
     * Vide le panier d'un client
     * @param customerEmail l'adresse email du client
     * @return le client mis à jour
     * @throws UnknownCustomerEmailException si l'adresse email n'est pas reconnue
     */
    @Override
    @Transactional
    public Customer resetCart(String customerEmail) throws UnknownCustomerEmailException {
        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        if (customer == null) {
            throw new UnknownCustomerEmailException(customerEmail);
        }
        customer.setCart(null);
        return customerRepository.save(customer);
    }

    /**
     * Rafraîchit le statut VFP des clients en fonction du nombre d'achats requis
     */
    @Override
    @Transactional
    public void refreshVfpStatus() {
        customerRepository.refreshVfpStatus(nbPurchaseRequired, LocalDateTime.now().minusDays(7), LocalDateTime.now());
    }
}
