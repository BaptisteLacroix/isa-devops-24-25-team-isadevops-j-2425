package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.components.CustomerCatalog;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(path = "/customers")
public class CustomerController {

    private final CustomerCatalog customerCatalog;

    public CustomerController(CustomerCatalog customerCatalog) {
        this.customerCatalog = customerCatalog;
    }

    @PostMapping("/")
    public void createCustomer(@RequestBody CustomerSubscribeDTO customer) throws UnreachableExternalServiceException, AlreadyUsedEmailException {
        customerCatalog.register(customer);
    }

    @PostMapping("/find-by-email")
    public void findCustomerByEmail(@RequestBody String email) throws UnknownCustomerEmailException {
        customerCatalog.findCustomerByEmail(email);
    }

    @PostMapping("/find-by-card-number")
    public void findCustomerByCardNumber(@RequestBody String cardNumber) throws UnknownCardNumberException {
        customerCatalog.findCustomerByCardNum(cardNumber);
    }

    @GetMapping("/")
    public void findAllCustomers() {
        customerCatalog.findAll();
    }

}
