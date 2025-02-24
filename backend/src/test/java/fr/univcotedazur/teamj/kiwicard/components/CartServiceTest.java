package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.BaseUnitTest;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyExistingCustomerException;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // you can make test non transactional to be sure that transactions are properly handled in
        // controller methods (if you are actually testing controller methods!)
// @Transactional
// @Commit // default @Transactional is ROLLBACK (no need for the @AfterEach
class CartServiceTest extends BaseUnitTest {

    @Autowired
    private ICustomerRepository ICustomerRepository;

    private Long johnId;

    @BeforeEach
    void setUp() throws AlreadyExistingCustomerException {
    }

    @AfterEach
    void cleaningUp()  {
    }


}
