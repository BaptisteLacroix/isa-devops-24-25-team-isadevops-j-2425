package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.dto.CartInPurchaseDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PurchaseHistoryDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPurchaseIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPerkManager;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MonitoringController.class)
class MonitoringControllerTest extends BaseUnitTest {

    private static final String BASE_URI = "/monitoring";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public IPurchaseFinder purchaseCatalog;

    @MockitoBean
    public IPerkManager perkManager; // unused mock to avoid spring autowiring error

    @MockitoBean
    public IPurchaseStats stats; // unused mock to avoid spring autowiring error

    
    @BeforeEach
    void setup() {
        var mockPurchase = new PurchaseHistoryDTO(mock(CartInPurchaseDTO.class),mock(PaymentHistoryDTO.class));
        try {
            when(purchaseCatalog.findPurchasesByPartnerId(1L)).thenReturn(List.of(mockPurchase));
            when(purchaseCatalog.findPurchaseById(1L)).thenReturn(mockPurchase);
            when(purchaseCatalog.findPurchasesByCustomerEmail("test@example.com")).thenReturn(List.of(mockPurchase));
            when(purchaseCatalog.findPurchasesByCustomerAndPartner("test@example.com", 1L)).thenReturn(List.of(mockPurchase));
        } catch (UnknownPartnerIdException | UnknownPurchaseIdException | UnknownCustomerEmailException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getPartnerHistoryIntegrationTest() throws Exception {
        mockMvc.perform(get(BASE_URI + "/partner/" + 1L + "/history"))
                .andExpect(status().isOk());
        verify(purchaseCatalog, times(1)).findPurchasesByPartnerId(1L);
    }

    @Test
    void getPurchaseTest() throws Exception {
        mockMvc.perform(get(BASE_URI + "/purchase/" + 1L))
                .andExpect(status().isOk());
        verify(purchaseCatalog, times(1)).findPurchaseById(1L);
    }

    @Test
    void customerHistoryTest() throws Exception {
        mockMvc.perform(get(BASE_URI + "/customer/test@example.com/history"))
                .andExpect(status().isOk());
        verify(purchaseCatalog, times(1)).findPurchasesByCustomerEmail("test@example.com");
    }

    @Test
    void getByCustomerAndPartnerTest() throws Exception {
        mockMvc.perform(get(BASE_URI + "/purchase")
                .param("customerEmail", "test@example.com")
                .param("partnerId", "1"))
                .andExpect(status().isOk());
        verify(purchaseCatalog, times(1)).findPurchasesByCustomerAndPartner("test@example.com", 1L);
    }
}
