package fr.univcotedazur.teamj.kiwicard.connectors;

import fr.univcotedazur.teamj.kiwicard.dto.CardCreationDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CardDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.ICardCreation;

public class CardEditorProxy  implements ICardCreation {

    @Override
    public CardDTO orderACard(String email, String address) throws UnreachableExternalServiceException {
        CardCreationDTO cardCreationDto = new CardCreationDTO(email, address);
        return null;
    }
}
