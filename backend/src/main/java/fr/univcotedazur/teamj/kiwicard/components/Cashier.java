package fr.univcotedazur.teamj.kiwicard.components;

import fr.univcotedazur.teamj.kiwicard.dto.CartDTO;
import fr.univcotedazur.teamj.kiwicard.dto.CustomerDTO;
import fr.univcotedazur.teamj.kiwicard.dto.PaymentDTO;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnreachableExternalServiceException;
import fr.univcotedazur.teamj.kiwicard.interfaces.IPayment;
import org.springframework.stereotype.Component;

@Component
public class Cashier implements IPayment {
    @Override
    public PaymentDTO makePay(CustomerDTO customerDTO) throws UnreachableExternalServiceException {
        return null;
    }
}
