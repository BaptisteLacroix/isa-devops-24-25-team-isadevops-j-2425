package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.CardCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.CardCreation;

public class CardEditorProxy  implements CardCreation {
    @Override
    public CardDTO orderACard(CardCreationDTO cardInfo) throws UnreachableExternalServiceException {
        return null;
    }
}
