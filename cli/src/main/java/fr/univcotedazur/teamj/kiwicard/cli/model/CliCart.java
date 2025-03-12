package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.util.List;
import java.util.Set;

public record CliCart(long cartId, CliPartner partner, Set<CliCartItem> items, List<CliPerk> perksList) {
}
