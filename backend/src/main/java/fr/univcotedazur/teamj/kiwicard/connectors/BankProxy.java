package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.connectors.externaldto.PaymentRequestDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IBank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BankProxy implements IBank {

    private static final Logger LOG = LoggerFactory.getLogger(BankProxy.class);

    private final String bankHostandPort;

    private WebClient webClient;

    @Autowired
    public BankProxy(@Value("${bank.host.baseurl}") String bankHostandPort) {
        this.bankHostandPort = bankHostandPort;
//        this.webClient = WebClient.builder()
//                .baseUrl(this.bankHostandPort)
//                .build();
    }

    @Override
    public PaymentDTO askPayment(PaymentRequestDTO paymentInfo) throws UnreachableExternalServiceException {
        return null;
    }


}
