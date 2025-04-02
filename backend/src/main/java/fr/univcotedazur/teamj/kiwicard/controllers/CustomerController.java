package fr.univcotedazur.teamj.kiwicard.controllers;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.AlreadyUsedEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCardNumberException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownCustomerEmailException;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerFinder;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.ICustomerRegistration;
import fr.univcotedazur.teamj.kiwicard.interfaces.customer.IVfpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


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

    /**
     * Enregistre un nouveau client dans la base de données et lui attribue une carte
     *
     * @param customer les informations du client à enregistrer
     * @throws UnreachableExternalServiceException si le service externe est injoignable
     * @throws AlreadyUsedEmailException           si l'adresse email est déjà utilisée
     */
    @PostMapping("")
    public ResponseEntity<Void> createCustomer(@RequestBody CustomerSubscribeDTO customer) throws UnreachableExternalServiceException, AlreadyUsedEmailException {
        CustomerDTO customerDTO = customerRegistration.register(customer);
        return ResponseEntity.created(URI.create("/customers/" + customerDTO.email())).build();
    }

    /**
     * Récupère un client par son adresse email ou son numéro de carte
     * @param email l'adresse email du client
     * @param cardNumber le numéro de carte du client
     * @return le DTO du client
     * @throws UnknownCustomerEmailException si l'adresse email est inconnue
     * @throws UnknownCardNumberException si le numéro de carte est inconnu
     */
    @GetMapping("")
    public ResponseEntity<CustomerDTO> findCustomerByEmailOrByCardNumber(@RequestParam(required = false) String email, @RequestParam(required = false) String cardNumber) throws UnknownCustomerEmailException, UnknownCardNumberException {
        if (email == null && cardNumber == null) {
            throw new IllegalArgumentException("Either email or cardNumber must be provided, but not both.");
        }
        if (email != null) {
            return ResponseEntity.ok().body(customerFinder.findCustomerDTOByEmail(email));
        } else {
            return ResponseEntity.ok().body(customerFinder.findCustomerByCardNum(cardNumber));
        }
    }

    /**
     * Récupère tous les clients
     */
    @GetMapping("/")
    public ResponseEntity<List<CustomerDTO>> findAllCustomers() {
        return ResponseEntity.ok().body(customerFinder.findAll());
    }

    /**
     * Met à jour le statut VFP
     */
    @PutMapping("/refresh-vfp-status")
    public ResponseEntity<Void> updateVfpStatus() {
        vfpStatus.refreshVfpStatus();
        return ResponseEntity.noContent().build();
    }
}
