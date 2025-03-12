package fr.univcotedazur.teamj.kiwicard.cli.model;

import java.util.List;

public record CliCart (long cartId, CliPartner partner, List<CliPerk> perksList, List<CliItem> items) {
}
