package fr.univcotedazur.teamj.kiwicard.configurations;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class HappyKidsProperties {

    @Value("${happykids.item.name}")
    private String itemName;

    @PostConstruct
    public void init() {
        Cart.setHappyKidsItemName(itemName);
    }
}

