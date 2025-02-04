package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.teamj.kiwicard.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // you can make test non transactional to be sure that transactions are properly handled in
        // controller methods (if you are actually testing controller methods!)
// @Transactional
// @Commit // default @Transactional is ROLLBACK (no need for the @AfterEach
class CartServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Long johnId;

    @BeforeEach
    void setUp() throws AlreadyExistingCustomerException {
    }

    @AfterEach
    void cleaningUp()  {
    }


}
