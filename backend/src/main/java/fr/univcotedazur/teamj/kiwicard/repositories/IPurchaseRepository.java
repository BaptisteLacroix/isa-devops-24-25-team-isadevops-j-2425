package fr.univcotedazur.teamj.kiwicard.repositories;


import fr.univcotedazur.teamj.kiwicard.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPurchaseRepository extends JpaRepository<Purchase, Long> {
}
