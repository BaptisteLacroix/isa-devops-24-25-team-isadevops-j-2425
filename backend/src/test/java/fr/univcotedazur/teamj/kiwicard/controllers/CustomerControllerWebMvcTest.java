package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.dto.ErrorDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerWebMvcTest extends BaseUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerCatalog customerCatalog;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createCustomerIntegrationTest() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO(
                "test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette"
        );
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(customerCatalog, times(1)).register(any(CustomerSubscribeDTO.class));
    }

    @Test
    void createCustomerIntegrationThrowsException() throws Exception {
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
    void findCustomerByEmailIntegrationTest() throws Exception {
        String email = "test@example.com";
        String dummyCard = "dummyCard"; // valeur factice pour le paramètre cardNumber (non utilisé)
        // Création d'un CustomerDTO factice
        Customer customer = new Customer("test@example.com", "Roxane", "Roxx", "3 passage du test", false);
        when(customerCatalog.findCustomerByEmail(email)).thenReturn(customer);

        // Appel GET sur /customers sans slash final, avec les paramètres requis
        mockMvc.perform(get("/customers")
                        .param("email", email)
                        .param("cardNumber", dummyCard))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new CustomerDTO(customer))));

        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findAllCustomersIntegrationTest() throws Exception {
        // Appel GET sur /customers/ avec slash final pour obtenir findAllCustomers()
        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk());

        verify(customerCatalog, times(1)).findAll();
    }
}
