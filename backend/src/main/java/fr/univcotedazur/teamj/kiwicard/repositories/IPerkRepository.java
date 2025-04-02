package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.dto.PerkCountDTO;
import fr.univcotedazur.teamj.kiwicard.entities.perks.AbstractPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface IPerkRepository extends JpaRepository<AbstractPerk, Long> {
    @Query("""
    select new fr.univcotedazur.teamj.kiwicard.dto.PerkCountDTO(type(perk), count(perk))
    from Purchase p
      join p.cart c
      join c.perksToUse perk
    where p.partner.partnerId = :partnerId
    group by type(perk)
    """)
    List<PerkCountDTO> countByTypeForPartner(@Param("partnerId") long partnerId);
}


