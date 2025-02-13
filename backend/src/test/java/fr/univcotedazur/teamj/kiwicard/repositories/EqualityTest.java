package fr.univcotedazur.teamj.kiwicard.repositories;

import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EqualityTest {

    private Customer john;

    @BeforeEach
    void setup() {
        john = new Customer("john", "john@gmail.com");
    }

    @Test
    void testCustomerEquals() {
        assertEquals(john, john);
        Customer otherJohn = new Customer("john", "john@gmail.com");
        assertEquals(john, otherJohn);
        assertEquals(otherJohn, john);
    }

    @Test
    void testCustomerNotEquals() {
        assertEquals(john, john);
        Customer otherJohn = new Customer("john", "john06@gmail.com");
        assertNotEquals(john, otherJohn);
        assertNotEquals(otherJohn, john);
    }

}
