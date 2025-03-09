package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ICustomerRepositoryTest {

    @Autowired
    ICustomerRepository customerRepository;
    LocalDateTime lastWeekDate = LocalDateTime.of(2025, 3, 1, 0, 0);

    @Test
    @Sql(scripts = {"/sql/test_refresh_vfp_status_fail.sql","/sql/test_refresh_vfp_status_limit_case.sql"})
    void refreshVfpStatusNoCustomerFound() {
        assertEquals(0, customerRepository.refreshVfpStatus(2, lastWeekDate));
        Customer alice = customerRepository.findByEmail("alice@gmail.com").orElseThrow();
        assertFalse(alice.isVfp());
        Customer bob = customerRepository.findByEmail("bob@gmail.com").orElseThrow();
        assertFalse(bob.isVfp());
    }

    @Test
    @Sql(scripts ={"/sql/test_refresh_vfp_status_success.sql","/sql/test_refresh_vfp_status_limit_case.sql"})
    void refreshVfpStatusTwoCustomerFound() {
        assertEquals(2, customerRepository.refreshVfpStatus(2, lastWeekDate));
        Customer alice = customerRepository.findByEmail("alice@gmail.com").orElseThrow();
        assertTrue(alice.isVfp());
        Customer john = customerRepository.findByEmail("john@gmail.com").orElseThrow();
        assertTrue(john.isVfp());
        Customer bob = customerRepository.findByEmail("bob@gmail.com").orElseThrow();
        assertFalse(bob.isVfp());
    }
}
