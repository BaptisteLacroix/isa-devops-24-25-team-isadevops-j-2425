package fr.univcotedazur.teamj.kiwicard;

import org.springframework.test.context.ActiveProfiles;

/**
 * Classe de base pour les tests unitaires. Cette classe permet de définir un profil de test pour éviter de charger les
 * lancer les CommandLineRunners de l'application. Toutes les classes de test unitaire doivent hériter de cette classe.
 */
@ActiveProfiles("test")
public abstract class BaseUnitTest {
}
