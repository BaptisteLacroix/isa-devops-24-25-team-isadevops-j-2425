package fr.univcotedazur.teamj.kiwicard.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerCatalog customerCatalog;


    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createCustomerIntegrationTest() throws Exception {
        // Créez un CustomerSubscribeDTO en JSON
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette");
        String json = mapper.writeValueAsString(dto);

        // Effectuer l'appel HTTP POST sur l'endpoint "/customers/"
        mockMvc.perform(post("/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        // Vérifier que le service a été appelé avec le DTO correct
        verify(customerCatalog, times(1)).register(any(CustomerSubscribeDTO.class));
    }

    @Test
    void findCustomerByEmailIntegrationTest() throws Exception {
        String email = "test@example.com";
        mockMvc.perform(post("/customers/find-by-email")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk());

        verify(customerCatalog, times(1)).findCustomerByEmail(email);
    }

    @Test
    void findCustomerByCardNumberIntegrationTest() throws Exception {
        String cardNumber = "CARD123";
        mockMvc.perform(post("/customers/find-by-card-number")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(cardNumber))
                .andExpect(status().isOk());

        verify(customerCatalog, times(1)).findCustomerByCardNum(cardNumber);
    }

    @Test
    void findAllCustomersIntegrationTest() throws Exception {
        mockMvc.perform(get("/customers/"))
                .andExpect(status().isOk());

        verify(customerCatalog, times(1)).findAll();
    }

    // Vous pouvez aussi créer des tests qui simulent la levée d'exception (par exemple, AlreadyUsedEmailException)
    @Test
    void createCustomerIntegrationThrowsException() throws Exception {
        CustomerSubscribeDTO dto = new CustomerSubscribeDTO("test@example.com", "Roxane", "Roxx", "2 passage Marie Antoinette");
        String json = mapper.writeValueAsString(dto);

        doThrow(new AlreadyUsedEmailException()).when(customerCatalog).register(any(CustomerSubscribeDTO.class));

        mockMvc.perform(post("/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())  // 409 Conflict
                .andExpect(content().string("Email déjà utilisé"));

        verify(customerCatalog, times(1)).register(any(CustomerSubscribeDTO.class));
    }
}
