package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerCatalog customerCatalog;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void createCustomer() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette");

        customerController.createCustomer(dto);

        verify(customerCatalog, times(1)).register(dto);
    }

    @Test
    void createCustomerThrowsException() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette");
        doThrow(new AlreadyUsedEmailException()).when(customerCatalog).register(dto);
        assertThrows(AlreadyUsedEmailException.class, () -> customerController.createCustomer(dto));
        verify(customerCatalog, times(1)).register(dto);
    }

    @Test
    void findCustomerByEmail() throws Exception {
        String email = "test@example.com";
        customerController.findCustomerByEmail(email);
        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findCustomerByEmailThrowsException() throws Exception {
        String email = "inconnu@example.com";
        doThrow(new UnknownCustomerEmailException()).when(customerCatalog).findCustomerByEmail(email);
        assertThrows(UnknownCustomerEmailException.class, () -> customerController.findCustomerByEmail(email));
        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findCustomerByCardNumber() throws Exception {
        String cardNumber = "CARD123";
        customerController.findCustomerByCardNumber(cardNumber);
        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findCustomerByCardNumberThrowsException() throws Exception {
        String cardNumber = "INVALID";
        doThrow(new UnknownCardNumberException()).when(customerCatalog).findCustomerByCardNum(cardNumber);
        assertThrows(UnknownCardNumberException.class, () -> customerController.findCustomerByCardNumber(cardNumber));
        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findAllCustomers() {
        customerController.findAllCustomers();
        verify(customerCatalog, times(1)).findAll();
    }
}
