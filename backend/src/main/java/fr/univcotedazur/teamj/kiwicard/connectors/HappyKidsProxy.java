package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsRequestDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IHappyKids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class HappyKidsProxy implements IHappyKids {

    private final WebClient webClient;

    /**
     * Contruit un proxy pour l'API HappyKids avec l'URL de base spécifiée.
     *
     * @param happyKidsBaseUrl L'URL de base de l'API HappyKids.
     */
    @Autowired
    public HappyKidsProxy(@Value("${happykids.host.baseurl}") String happyKidsBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(happyKidsBaseUrl)
                .build();
    }

    /**
     * Calcule le montant de la réduction à appliquer à un article du panier.
     *
     * @param itemPrice         L'article du panier pour lequel calculer la réduction.
     * @param discountRate Le taux de réduction à appliquer.
     * @return Le DTO contenant le montant de la réduction.
     * @throws UnreachableExternalServiceException Si le service externe est injoignable.
     */
    @Override
    public HappyKidsDiscountDTO computeDiscount(double itemPrice, double discountRate) throws UnreachableExternalServiceException {
        try {
            HappyKidsRequestDTO happyKidsRequestDTO = new HappyKidsRequestDTO(itemPrice, discountRate);
            // Call the external service
            return webClient.post()
                    .uri("/perks")
                    .bodyValue(happyKidsRequestDTO)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.createException().flatMap(Mono::error))
                    .bodyToMono(HappyKidsDiscountDTO.class)
                    .block();
        } catch (Exception e) {
            throw new UnreachableExternalServiceException();
        }
    }
}
