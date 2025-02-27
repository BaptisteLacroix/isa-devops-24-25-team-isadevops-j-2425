package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByEmail(String customerClementEmail);
}
