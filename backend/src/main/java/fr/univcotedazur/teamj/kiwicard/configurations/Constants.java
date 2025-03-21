package fr.univcotedazur.teamj.kiwicard.configurations;

import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String HAPPY_KIDS_ITEM_NAME = "HappyKids";
}

