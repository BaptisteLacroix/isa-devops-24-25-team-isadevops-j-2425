package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/customers")
public class CustomerController {

    private final CustomerCatalog customerCatalog;

    public CustomerController(CustomerCatalog customerCatalog) {
        this.customerCatalog = customerCatalog;
    }

    @PostMapping("")
    public void createCustomer(@RequestBody CustomerSubscribeDTO customer) throws UnreachableExternalServiceException, AlreadyUsedEmailException {
        customerCatalog.register(customer);
    }

    @GetMapping("")
    public CustomerDTO findCustomerByEmailOrByCardNumber(@RequestParam String email, @RequestParam String cardNumber) throws UnknownCustomerEmailException, UnknownCardNumberException {
        if (email != null) {
            return customerCatalog.findCustomerByEmail(email);
        } else {
            return customerCatalog.findCustomerByCardNum(cardNumber);
        }
    }


    @GetMapping("/")
    public void findAllCustomers() {
        customerCatalog.findAll();
    }

}
