package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.Payment;
import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.purchase.IPurchaseFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MonitoringController.class)
public class MonitoringControllerTest extends BaseUnitTest {

    private final String BASE_URI = "/monitoring";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    public IPurchaseFinder purchaseCatalog;

    @BeforeEach
    public void setup() {
        var mockPurchase = new Purchase(mock(Payment.class), mock(Cart.class));
        try {
            when(purchaseCatalog.findPurchasesByPartnerId(1L)).thenReturn(List.of(mockPurchase));
        } catch (UnknownPartnerIdException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getPartnerHistoryIntegrationTest() throws Exception {
        mockMvc.perform(get(BASE_URI + "/partnerHistory/" + 1L))
                .andExpect(status().isOk());
        verify(purchaseCatalog, times(1)).findPurchasesByPartnerId(1L);
    }


}
