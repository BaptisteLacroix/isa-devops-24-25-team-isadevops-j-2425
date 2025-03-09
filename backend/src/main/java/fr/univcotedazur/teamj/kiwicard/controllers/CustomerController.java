package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.dto.NumberOfVfpStatusDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerRegistration;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.IVfpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/customers")
public class CustomerController {

    private final ICustomerFinder customerFinder;
    private final ICustomerRegistration customerRegistration;
    private final IVfpStatus vfpStatus;

    public CustomerController(ICustomerFinder customerFinder, ICustomerRegistration customerRegistration, IVfpStatus vfpStatus) {
        this.customerFinder = customerFinder;
        this.customerRegistration = customerRegistration;
        this.vfpStatus = vfpStatus;
    }

    @PostMapping("")
    public void createCustomer(@RequestBody CustomerSubscribeDTO customer) throws UnreachableExternalServiceException, AlreadyUsedEmailException {
        customerRegistration.register(customer);
    }

    @GetMapping("")
    public CustomerDTO findCustomerByEmailOrByCardNumber(@RequestParam(required = false) String email, @RequestParam(required = false) String cardNumber) throws UnknownCustomerEmailException, UnknownCardNumberException {
        if (email == null && cardNumber == null) {
            throw new IllegalArgumentException("Either email or cardNumber must be provided, but not both.");
        }
        if (email != null) {
            return new CustomerDTO(customerFinder.findCustomerByEmail(email));
        } else {
            return customerFinder.findCustomerByCardNum(cardNumber);
        }
    }

    @GetMapping("/")
    public void findAllCustomers() {
        customerFinder.findAll();
    }


    @PutMapping("/refresh-vfp-status")
    public ResponseEntity<NumberOfVfpStatusDTO> updateVfpStatus() {
        return ResponseEntity.ok(new NumberOfVfpStatusDTO(vfpStatus.refreshVfpStatus()));
    }
}
