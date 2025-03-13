package fr.univcotedazur.teamj.kiwicard.cli;

import org.springframework.stereotype.Component;

import static fr.univcotedazur.teamj.kiwicard.cli.constants.Constants.LOGGED_IN_ID_PLACEHOLDER;

@Component
public class CliSession {

    private String loggedInCustomerEmail;
    private Long loggedInPartnerId;

    public String getLoggedInCustomerEmail() {
        return loggedInCustomerEmail;
    }

    public void logIn(String loggedInCustomerEmail) {
        this.loggedInCustomerEmail = loggedInCustomerEmail;
    }

    public Long getLoggedInPartnerId() {
        return loggedInPartnerId;
    }

    public void logIn(long loggedInPartnerId) {
        this.loggedInPartnerId = loggedInPartnerId;
    }

    /**
     * Preprocess the partner partnerId to check if it is a valid number, and if it is equal to "sessionValue", replace it with the logged in partner partnerId
     * @param partnerId the partner partnerId to preprocess
     * @return the preprocessed partner partnerId, or null if the partner partnerId is not a valid number
     */
    public String tryInjectingPartnerId(String partnerId) {
        if (partnerId.equals(LOGGED_IN_ID_PLACEHOLDER)) {
            partnerId = String.valueOf(this.getLoggedInPartnerId());
        }
        try {
            Long.parseLong(partnerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identifiant de partenaire invalide");
        }
        return partnerId;
    }

    /**
     * Preprocess the customer email to check if it is equal to "sessionValue", replace it with the logged in customer email
     * @param customerEmail the customer email to preprocess
     * @return the preprocessed customer email
     */
    public String tryInjectingCustomerEmail(String customerEmail) {
        if (customerEmail.equals(LOGGED_IN_ID_PLACEHOLDER)) {
            customerEmail = this.getLoggedInCustomerEmail();
        }
        return customerEmail;
    }

}
