Feature: User Registration

  Background: Initialize the database with simple data
    Given [User Registration] a simple dataset

  Scenario: Register a new customer successfully
    When I register a new customer with surname "Doe", firstname "John", email "john.doe@example.com" and address "123 Main St"
    Then the registration should be successful
    And I should receive a confirmation message "Client enregistré avec succès. Vous êtes maintenant connecté en tant que : john.doe@example.com"

  Scenario: Register a customer with an existing email
    Given I have a customer with surname "Doe", firstname "John", email "john.doe@example.com" and address "123 Main St" already registered
    When I try to register a new customer with the same surname "Doe", firstname "John", email "john.doe@example.com" and address "123 Main St"
    Then I should receive an error message "L'email john.doe@example.com est déjà utilisé"
