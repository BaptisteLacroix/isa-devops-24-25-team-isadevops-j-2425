package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerWebMvcTest extends BaseUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerCatalog customerCatalog;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createCustomerTest() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        CustomerDTO customerDTO = new CustomerDTO(new Customer(dto, "dummyCard"));
        String json = mapper.writeValueAsString(dto);
        when(customerCatalog.register(dto)).thenReturn(customerDTO);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(customerCatalog, times(1)).register(any(CustomerSubscribeDTO.class));
    }

    @Test
    void createCustomerThrowsException() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        String json = mapper.writeValueAsString(dto);

        // Simule la levée de l'exception dans le service
        doThrow(new AlreadyUsedEmailException("test@example.com")).when(customerCatalog).register(any(CustomerSubscribeDTO.class));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorDTO("L'email test@example.com est déjà utilisé"))));

        verify(customerCatalog, times(1)).register(any(CustomerSubscribeDTO.class));
    }

    @Test
    void findCustomerByEmailTest() throws Exception {
        String email = "test@example.com";
        String dummyCard = "dummyCard";
        Customer customer = new Customer("test@example.com", "Roxane", "Roxx", "3 passage du test", false);
        when(customerCatalog.findCustomerByEmail(email)).thenReturn(customer);

        mockMvc.perform(get("/customers")
                        .param("email", email)
                        .param("cardNumber", dummyCard))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new CustomerDTO(customer))));

        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findCustomerByEmailThrowsUnknownCustomerEmailExceptionTest() throws Exception {
        String email = "inconnu@example.com";
        String dummyCard = "dummyCard";
        doThrow(new UnknownCustomerEmailException(email)).when(customerCatalog).findCustomerByEmail(email);

        mockMvc.perform(get("/customers")
                        .param("email", email)
                        .param("cardNumber", dummyCard))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorDTO("Adresse email inconnue: " + email))));

        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findCustomerByCardNumberTest() throws Exception {
        String cardNumber = "dummyCard";
        Customer customer = new Customer("test@example.com", "Roxane", "Roxx", "3 passage du test", false);
        when(customerCatalog.findCustomerByCardNum(cardNumber)).thenReturn(new CustomerDTO(customer));

        mockMvc.perform(get("/customers")
                        .param("cardNumber", cardNumber))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new CustomerDTO(customer))));

        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findCustomerByCardNumberThrowsUnknownCardNumberExceptionTest() throws Exception {
        String cardNumber = "unknownCard";
        doThrow(new UnknownCardNumberException(cardNumber)).when(customerCatalog).findCustomerByCardNum(cardNumber);

        mockMvc.perform(get("/customers")
                        .param("cardNumber", cardNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(new ErrorDTO("Numéro de carte inconnu: " + cardNumber))));

        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findAllCustomersTest() throws Exception {
        String email = "test@example.com";
        Customer customer = new Customer(email, "Roxane", "Roxx", "3 passage du test", false);
        when(customerCatalog.findAll()).thenReturn(List.of(new CustomerDTO(customer)));

        // Appel GET sur /customers/ avec slash final pour obtenir findAllCustomers()
        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(new CustomerDTO(customer)))));

        verify(customerCatalog, times(1)).findAll();
    }
}
