package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.interfaces.CustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.CustomerRegistration;
import org.springframework.stereotype.Service;

@Service
public class CustomerCatalog implements CustomerRegistration, CustomerFinder {

}
