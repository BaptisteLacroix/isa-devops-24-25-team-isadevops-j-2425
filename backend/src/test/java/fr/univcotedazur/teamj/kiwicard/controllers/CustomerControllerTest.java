package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerCatalog customerCatalog;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void createCustomer() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );

        customerController.createCustomer(dto);

        verify(customerCatalog, times(1)).register(dto);
    }

    @Test
    void createCustomerThrowsException() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        doThrow(new AlreadyUsedEmailException()).when(customerCatalog).register(dto);

        assertThrows(AlreadyUsedEmailException.class, () -> customerController.createCustomer(dto));
        verify(customerCatalog, times(1)).register(dto);
    }

    // Test pour la recherche par email (cas classique : email non null)
    @Test
    void findCustomerByEmail() throws Exception {
        String email = "test@example.com";
        String dummyCard = "ignored"; // Ce paramètre n'est pas utilisé si email != null
        CustomerDTO customerDTO = new CustomerDTO("test@example.com", "Roxane", "Roxx", false);
        when(customerCatalog.findCustomerDTOByEmail(email)).thenReturn(customerDTO);

        CustomerDTO result = customerController.findCustomerByEmailOrByCardNumber(email, dummyCard);
        assertEquals(customerDTO, result);
        verify(customerCatalog, times(1)).findCustomerDTOByEmail(email);
    }

    // Test pour la recherche par numéro de carte (cas : email est null)
    @Test
    void findCustomerByCardNumber() throws Exception {
        String cardNumber = "CARD123";
        CustomerDTO customerDTO = new CustomerDTO("someone@example.com", "FirstName", "LastName", false);
        when(customerCatalog.findCustomerByCardNum(cardNumber)).thenReturn(customerDTO);

        CustomerDTO result = customerController.findCustomerByEmailOrByCardNumber(null, cardNumber);
        assertEquals(customerDTO, result);
        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findCustomerByEmailThrowsException() throws Exception {
        String email = "inconnu@example.com";
        String dummyCard = "ignored";
        doThrow(new UnknownCustomerEmailException()).when(customerCatalog).findCustomerDTOByEmail(email);

        assertThrows(UnknownCustomerEmailException.class, () -> customerController.findCustomerByEmailOrByCardNumber(email, dummyCard));
        verify(customerCatalog, times(1)).findCustomerDTOByEmail(email);
    }

    @Test
    void findCustomerByCardNumberThrowsException() throws Exception {
        String cardNumber = "INVALID";
        doThrow(new UnknownCardNumberException()).when(customerCatalog).findCustomerByCardNum(cardNumber);

        assertThrows(UnknownCardNumberException.class, () -> customerController.findCustomerByEmailOrByCardNumber(null, cardNumber));
        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findAllCustomers() {
        customerController.findAllCustomers();
        verify(customerCatalog, times(1)).findAll();
    }
}
