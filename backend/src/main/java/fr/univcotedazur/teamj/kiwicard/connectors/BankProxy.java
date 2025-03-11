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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * A proxy class that interacts with the external bank service to handle payment requests.
 * It uses WebClient to send HTTP requests to the bank's payment service and retrieve responses.
 * If any errors occur while communicating with the external service, an exception is thrown.
 */
@Component
public class BankProxy implements IBank {

    private static final Logger LOG = LoggerFactory.getLogger(BankProxy.class);

    private final String bankHostandPort;
    private final WebClient webClient;

    /**
     * Constructs a BankProxy instance with the base URL of the bank service and a WebClient builder.
     *
     * @param bankHostandPort The base URL of the bank's service (e.g., "http://localhost:8080").
     * @param webClientBuilder A WebClient.Builder instance used to build the WebClient with the base URL.
     */
    @Autowired
    public BankProxy(@Value("${bank.host.baseurl}") String bankHostandPort, WebClient.Builder webClientBuilder) {
        this.bankHostandPort = bankHostandPort;
        this.webClient = webClientBuilder.baseUrl(this.bankHostandPort).build();
    }

    /**
     * Sends a payment request to the external bank service and retrieves the payment result.
     *
     * @param paymentInfo A PaymentRequestDTO containing the payment information to be processed.
     * @return            A PaymentDTO representing the result of the payment request.
     * @throws UnreachableExternalServiceException If there is an error while contacting the bank service
     *                                              or processing the payment request.
     */
    @Override
    public PaymentDTO askPayment(PaymentRequestDTO paymentInfo) throws UnreachableExternalServiceException {
        try {
            // Sends the payment request to the external service
            Mono<PaymentDTO> paymentResponse = this.webClient.post()
                    .uri("/payments")
                    .bodyValue(paymentInfo)
                    .retrieve()
                    .bodyToMono(PaymentDTO.class);

            // Blocks to wait for the response and returns the result
            return paymentResponse.block();
        } catch (WebClientResponseException ex) {
            // Logs error if there is an issue while contacting the bank service
            LOG.error("Error while contacting the bank service", ex);
            throw new UnreachableExternalServiceException();
        }
    }
}
