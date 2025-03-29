package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksModifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface IPerkManager extends IPerksCreator, IPerksFinder, IPerksModifier {
    @Transactional
    Map<String, Long> aggregatePartnerPerksUsageByType(long partnerId) throws UnknownPartnerIdException;
}
