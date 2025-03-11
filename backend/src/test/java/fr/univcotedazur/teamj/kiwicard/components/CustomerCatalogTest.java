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
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerCatalogTest {

    @Mock
    private ICustomerRepository customerRepository;

    @Mock
    private CardEditorProxy cardEditorProxy;

    @InjectMocks
    private CustomerCatalog customerCatalog;

    @Test
    void register() throws Exception {
        CustomerSubscribeDTO subscribeDto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        CardDTO cardDto = new CardDTO("CARD123");

        // Retourner null pour simuler qu'aucun client n'est trouvé
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(cardEditorProxy.orderACard("test@example.com", "2 passage Marie Antoinette"))
                .thenReturn(cardDto);

        CustomerDTO result = customerCatalog.register(subscribeDto);

        assertEquals("test@example.com", result.email());
        verify(customerRepository, times(1)).findByEmail("test@example.com");
        verify(cardEditorProxy, times(1)).orderACard("test@example.com", "2 passage Marie Antoinette");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }


    @Test
    void registerAlreadyUsedEmail() throws UnreachableExternalServiceException {
        CustomerSubscribeDTO subscribeDto = new CustomerSubscribeDTO(
                "test@example.com", "Clément", "Clem", "123 rue Exemple"
        );
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new Customer()));

        assertThrows(AlreadyUsedEmailException.class, () -> customerCatalog.register(subscribeDto));

        verify(customerRepository, times(1)).findByEmail("test@example.com");
        verify(cardEditorProxy, times(0)).orderACard(any(), any());
        verify(customerRepository, times(0)).save(any(Customer.class));
    }

    @Test
    void findCustomerByEmail() throws Exception {
        CustomerSubscribeDTO customersubscribe = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        Customer customer = new Customer(customersubscribe, "CARD123");
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));

        CustomerDTO result = new CustomerDTO(customerCatalog.findCustomerByEmail("test@example.com"));

        assertEquals("test@example.com", result.email());
        verify(customerRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void findCustomerByEmailNotFound() {
        when(customerRepository.findByEmail("inconnu@example.com"))
                .thenReturn(Optional.empty());
        assertThrows(UnknownCustomerEmailException.class, () ->
                customerCatalog.findCustomerByEmail("inconnu@example.com"));
        verify(customerRepository, times(1)).findByEmail("inconnu@example.com");
    }

    @Test
    void findCustomerByCardNum() throws Exception {
        CustomerSubscribeDTO customersubscribe = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        Customer customer = new Customer(customersubscribe, "CARD123");
        when(customerRepository.findByCardNumber("CARD123"))
                .thenReturn(Optional.of(customer));

        CustomerDTO result = customerCatalog.findCustomerByCardNum("CARD123");

        assertEquals("test@example.com", result.email());
        verify(customerRepository, times(1)).findByCardNumber("CARD123");
    }

    @Test
    void findCustomerByCardNumNotFound() {
        when(customerRepository.findByCardNumber("INVALID"))
                .thenReturn(Optional.empty());
        assertThrows(UnknownCardNumberException.class, () ->
                customerCatalog.findCustomerByCardNum("INVALID"));
        verify(customerRepository, times(1)).findByCardNumber("INVALID");
    }

    @Test
    void findAll() {
        CustomerSubscribeDTO customersubscribe1 = new CustomerSubscribeDTO(
                "roxane@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        Customer customer1 = new Customer(customersubscribe1, "CARD1608");
        CustomerSubscribeDTO customersubscribe2 = new CustomerSubscribeDTO(
                "clement@example.com", "Clément", "Clem", "2400 Route des Dolines"
        );
        Customer customer2 = new Customer(customersubscribe2, "CARD1003");
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

        List<CustomerDTO> dtos = customerCatalog.findAll();

        assertEquals(2, dtos.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void setCart() throws Exception {
        Customer customer = new Customer("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette", false);
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));

        customerCatalog.setCart("test@example.com", new Cart());

        assertNotNull(customer.getCart());
        verify(customerRepository, times(1)).findByEmail("test@example.com");
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void setCartCustomerNotFound() {
        when(customerRepository.findByEmail("inconnu@example.com"))
                .thenReturn(Optional.empty());
        assertThrows(UnknownCustomerEmailException.class, () ->
                customerCatalog.setCart("inconnu@example.com", new Cart()));
        verify(customerRepository, times(1)).findByEmail("inconnu@example.com");
        verify(customerRepository, times(0)).save(any());
    }

    @Test
    void emptyCart() throws Exception {
        Customer customer = new Customer("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette", false);
        customer.setCart(new Cart());
        when(customerRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));

        customerCatalog.emptyCart("test@example.com");

        assertTrue(customer.getCart().isEmpty());
        verify(customerRepository, times(1)).findByEmail("test@example.com");
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void emptyCartCustomerNotFound() {
        when(customerRepository.findByEmail("inconnu@example.com"))
                .thenReturn(Optional.empty());
        assertThrows(UnknownCustomerEmailException.class, () ->
                customerCatalog.emptyCart("inconnu@example.com"));
        verify(customerRepository, times(1)).findByEmail("inconnu@example.com");
        verify(customerRepository, times(0)).save(any());
    }
}
