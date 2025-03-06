package fr.univcotedazur.teamj.kiwicard.interfaces.partner;

import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksCreator;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.perks.IPerksModifier;

public interface IPerkManager extends IPerksCreator, IPerksFinder, IPerksModifier {
}
