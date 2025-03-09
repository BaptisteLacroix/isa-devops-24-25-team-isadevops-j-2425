package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ICustomerRepositoryTest {

    @Autowired
    ICustomerRepository customerRepository;

    LocalDateTime currentDate = LocalDateTime.of(2025, 3, 10, 0, 0);
    LocalDateTime lastWeekDate = currentDate.minusWeeks(1);

    @Test
    @Sql("/sql/test_refresh_vfp_status_does_nothing.sql")
    void refreshVfpStatusNoStatusChanged() {
        customerRepository.refreshVfpStatus(2, lastWeekDate, currentDate);
        Customer alice = customerRepository.findByEmail("alice@gmail.com").orElseThrow();
        assertFalse(alice.isVfp());
        Customer bob = customerRepository.findByEmail("bob@gmail.com").orElseThrow();
        assertTrue(bob.isVfp());
    }

    @Test
    @Sql("/sql/test_refresh_vfp_status_updated.sql")
    void refreshVfpStatusStatusChanged() {
        customerRepository.refreshVfpStatus(2, lastWeekDate, currentDate);
        Customer alice = customerRepository.findByEmail("alice@gmail.com").orElseThrow();
        assertFalse(alice.isVfp());
        Customer john = customerRepository.findByEmail("john@gmail.com").orElseThrow();
        assertTrue(john.isVfp());
    }
}
