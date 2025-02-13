package fr.univcotedazur.teamj.kiwicard.components;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // default behavior : rollback DB operations after each test (even if it fails)
@Commit // test-specific annotation to change default behaviour to Commit on all tests (could be applied on a method
        // This annotation obliges us to clean the DB (removing the 2 customers) but it is only here for illustration
        // The "rollback" policy should be privileged unless some specific testing context appears
class CashierTest {

    @BeforeEach
    void setUpContext() {
    }

    @AfterEach
    void cleanUpContext() {
    }

   /*@Test
    void identifyPaymentError() {
    }*/

}
