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

    @Autowired
    public CardEditorProxy(@Value("${cardeditor.host.baseurl}") String cardEditorBaseUrl) {
        this.cardEditorBaseUrl = cardEditorBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(cardEditorBaseUrl)
                .build();
    }

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
            throw new UnreachableExternalServiceException();
        }
    }


}
