package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.HappyKidsDiscountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.exceptions.ClosedTimeException;
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
    private final String happyKidsBaseUrl;

    @Autowired
    public HappyKidsProxy(@Value("${happykids.host.baseurl}") String happyKidsBaseUrl) {
        this.happyKidsBaseUrl = happyKidsBaseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(happyKidsBaseUrl)
                .build();
    }

    @Override
    public HappyKidsDiscountDTO computeDiscount(CartItem item, double discountRate) throws ClosedTimeException, UnreachableExternalServiceException {
        HappyKidsDiscountDTO happyKidsDiscountDto = new HappyKidsDiscountDTO(item.getPrice());
        try {
            // Call the external service
            return webClient.post()
                    .uri("/perks")
                    .bodyValue(happyKidsDiscountDto)
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
