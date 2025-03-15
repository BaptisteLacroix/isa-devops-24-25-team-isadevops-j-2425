package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.CardCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.ICardCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CardEditorProxy implements ICardCreation {

    private final WebClient webClient;
    private final String cardEditorBaseUrl;


    /**
     * Constructeur de la classe CardEditorProxy
     *
     * @param cardEditorBaseUrl l'url de base du service externe
     */
    @Autowired
    public CardEditorProxy(@Value("${cardeditor.host.baseurl}") String cardEditorBaseUrl) {
        this.cardEditorBaseUrl = cardEditorBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(cardEditorBaseUrl)
                .build();
    }

    /**
     * Commander une carte pour un client à partir de son email et de son adresse
     *
     * @param email   l'email du client
     * @param address l'adresse du client
     * @return la carte commandée
     * @throws UnreachableExternalServiceException si le service externe est injoignable
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CardDTO orderACard(String email, String address) throws UnreachableExternalServiceException {
        CardCreationDTO cardCreationDto = new CardCreationDTO(email, address);
        try {
            // Call the external service
            return webClient.post()
                    .uri("/cards")
                    .bodyValue(cardCreationDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.createException().flatMap(Mono::error))
                    .bodyToMono(CardDTO.class)
                    .block();
        } catch (Exception e) {
            throw new UnreachableExternalServiceException(cardEditorBaseUrl + "\n" + e.getMessage());
        }
    }


}
