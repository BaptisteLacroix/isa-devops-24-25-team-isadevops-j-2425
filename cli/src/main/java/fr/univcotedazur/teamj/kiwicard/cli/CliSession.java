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
     * Preprocess the partner id to check if it is a valid number, and if it is equal to "sessionValue", replace it with the logged in partner id
     * @param partnerId the partner id to preprocess
     * @return the preprocessed partner id, or null if the partner id is not a valid number
     */
    public String tryInjectingPartnerId(String partnerId) {
        if (partnerId.equals(LOGGED_IN_ID_PLACEHOLDER)) {
            partnerId = String.valueOf(this.getLoggedInPartnerId());
        }
        try {
            Long.parseLong(partnerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid partner id");
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
