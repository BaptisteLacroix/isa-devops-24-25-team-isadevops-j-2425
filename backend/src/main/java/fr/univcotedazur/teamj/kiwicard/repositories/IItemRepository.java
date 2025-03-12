package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IItemRepository extends JpaRepository<Item, Long> {
}
