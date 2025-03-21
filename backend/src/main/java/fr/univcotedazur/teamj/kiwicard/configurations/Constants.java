package fr.univcotedazur.teamj.kiwicard.configurations;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    @Value("${happykids.item.name}")
    public static String HAPPY_KIDS_ITEM_NAME;

    @PostConstruct
    public void init() {
        Cart.setHappyKidsItemName(HAPPY_KIDS_ITEM_NAME);
    }
}

