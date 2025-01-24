# RAPPORT STEP 1

# 1 - Hypothèses

## Utilisation d'un avantage

Le client demande à utiliser un avantage avant de procéder au paiement. Pour cela, la partenaire va alors vérifier la
possibilité de l’utiliser. S’il peut effectivement l’utiliser, alors l’avantage sera activé. Enfin le paiement sera
réalisé.

## Carte Avantage

Lors de la commande de la carte à l'enregistrement d'un client, celle-ci sera livré chez le client, il n’y a pas de
carte en ligne.

# 2 - Use case

## Acteurs primaires

- Client (Pierre)
- Partenaire (Laurence & Laura)

## Acteurs secondaires

- HappyKids (service de crèche, un partenaire)
- Banque (service de banque)
- Card Service (service permettant de commander et faire livrer une carte multi-fidélité)

## Diagramme de cas d’utilisation

![Diagramme de use case](/ressources_uml/Diagramme_de_use_case.png)

## Explication des découpages

- **Consulter historique** et **Consulter statistique** sont 2 cas différents car le **Client** n’a pas de statistiques
  à visualiser
- **Payer** utilise **Réaliser paiement** qui est de la responsabilité de la banque
- **Vérifier avantage** utilise **Calculer réduction** qui est de la responsabilité d’**HappyKids**

# 3 - Composants

![diagramme de composants](/ressources_uml/Diagrame_de_composant.png)

## Customer Catalog

Celui-ci est chargé de gérer la création des clients, il permet aussi de les retrouver grâce à leur email.

## Cart Service

Chargé de créer un Cart stocké temporairement dans le client qui l’initie, avant qu’il soit sauvegardé comme un Purchase
une fois le paiement en caisse effectué. Il permet aussi de retrouver un panier pour lui appliquer avantage.

## Cashier

Celui-ci gère les paiements, utilise le Purchase Catalog pour sauvegarder les Carts payés comme un Purchase.

## Purchase Catalog

Chargé de la création et de la sauvegarde de Purchase. Il permet aussi de retrouver et modifier un achat si celui-ci est
compté comme une condition dans l’utilisation d’un avantage.

## Perks Service

Il permet de consommer un avantage si les conditions d’utilisation de celui-ci sont réunis.

## Perks Catalog

Celui-ci permet de créer un avantage, d’en modifier ou d’en rechercher. Il est aussi possible récupérer les avantages
utilisables pour un Customer et un Partner donnés.

## Personal Monitoring Service

Celui-ci permet d’accéder à l’historique des avantages obtenu par un utilisateur, et permet aussi aux partenaires
d’obtenir la liste des avantages dont ses clients ont bénéficié avec un niveau de détail adapté

# 4 - Interfaces

# Customer Service

## Customer Registration

```java
/**
 * Gestion de création de compte et la connexion d'un client
 */
public interface CustomerRegistration {
    CustomerDTO register(String surname, String firstname, String email, String address) throws
            AlreadyUsedEMmailException;
}
```

## Customer Finder

```java
/**
 * Recherche et récupération de client
 */
public interface CustomerFinder {
    Optional<CustomerDTO> findCustomerByEmail(string customerEMail) throws UnknownCustomerEmailException;

    Optional<CustomerDTO> findCustomerByCartNum(string cardNumber) throws UnknownCardNumberException;

    List<CustomerDTO> findAll();
}
```

## Customer Cart Saver

```java
/**
 * Permet d'enregistrer le panier d'un client temporairement (jusqu'à ce qu'il soit payer)
 */
public interface CustomerCartSaver {
    void setCart(string customerEMail, CartDTO cart) throws UnknownCustomerEmailException;

    void emptyCart(string customerEMail) throws UnknownCustomerEmailException;
}
```

# Cart Handler

## Cart Creator

```java
/**
 * Création de panier lors d'un achat chez un partenaire
 */
public interface CartCreator {
    CartDTO createCart(string customerEmail, List<ItemDTO> items) throws UnknownCustomerEMailException;
}
```

## Cart Modifier

```java
/**
 * Modification de panier lros de l'application d'un avantage
 */
public interface CartModifier {
    CartDTO updateCart(string cartOwnerEmail, CartDTO newCart, UsedPerkDTO usedPerk) throws UnknownCustomerEmailException;

    PurchaseDTO validateCart(string cartOwnerEmail) throws UnknownCustomerEmailException;
}
```

## Cart Finder

```java
/**
 * Recherche et récupération de panier
 */
public interface CartFinder {
    Optional<CartDTO> findCustomerCart(string cartOwnerEmail) throws UnknownCustomerEmailException;
}
```

# Cashier

## Payment

```java
/**
 * Création et réglement d'un paiment
 */
public interface Payment {
    PaymentDTO makePay(CartDTO cartToPay) throws UnreachableExternalServiceException;
}
```

# Purchase Catalog

## Purchase Creator

```java
/**
 * Création d'achat lors du règlement d'un panier
 */
public interface PurchaseCreator {
    PurchaseDTO createPurchase(CartDTO cart, PaymentDTO payment);
}
```

## Purchase Consumer

```java
/**
 * Modifie un achat ou un item acheté pour le tagger comme "consommé" dans 
 * l'utilisation d'un avantage. Les éléments consommables étendent la classe PerkConsumable
 */
public interface PurchaseConsumer {
    void consumeNLastPurchaseoOfCustomer(int nbPurchasesToConsume,
                                         string customerEmail) throws UnknownCustomerEmailException;

    void consumeNLastPurchaseOfCustomerInPartner(int nbPurchasesToConsume,
                                                 string customerEmail,
                                                 long partnerId);

    void consumeNLastItemsOfCustomerInPartner(long itemId, int nbItemsConsumed,
                                              string customerEmail) throws UnknownCustomerEmailException;
}
```

## Purchase Finder

```java
/**
 * Recherche et récupération d'achat lors de l'application d'avantage
 */
public interface PurchaseFinder {
    Optional<PurchaseDTO> findPurchaseById(long purchaseId) throws UnknownPartnerIdException;

    List<PurchaseDTO> findPurchasesByCustomerAndPartner(string customerEmail, long partnerId) throws UnknownCustomerEmailException, UnknownPartnerIdException;
}
```

# Perks Handler

## Perks Consumer

```java
/**
 * Application d'un avantage sur un panier
 */
public interface PerksConsumer {
    boolean applyPerk(long perkId, string cartOwnerEmail) throws UnknownPerkIdException, UnknownCustomerEmailException;

    List<PerkDTO> findConsumablePerksForConsumerAtPartner(string consumerEmail, long partnerId) throws UnknownCartIdException, UnknownPartnerIdException;
}
```

# Perks Catalog

## Perks Finder

```java
/**
 * Recherche et récupération d'avantage
 */
public interface PerksFinder {
    Optional<PerkDTO> findPerkById(long perkId) throws UnknownPerkIdException;

    List<PerkDTO> findPerkByPartner(long partnerId) throws UnknownPartnerIdException;

    List<PerkDTO> findAllPerks();
}
```

## Perks Creator

```java
/**
 * Permet aux partenaires de créer des avantages
 */
public interface PerksCreator {
    PerkDTO createPerk(PerkDTO perkToCreate);
}
```

## Perks Modifier

```java
/**
 * Modification ou suppression d'un avatange
 */
public interface PerksModifier {
    void udpatePerk(long perkId, PerkDTO newPerk) throws UnknownPerkIdException;

    void deletePerk(long perkId) throws UnknownPerkIdException;
}
```

# Personal Monitoring

## Customer History

```java
/**
 * Consultation de l'historique des avantage utiliser par et pour utilisateur
 */
public interface HistoryExplorator {
    HistoryDTO getCustomerHistory(string customerEmail) throws UnknownCustomerEmailException;
}
```

## Partner History

```java
/**
 * Consultation de l'historique des avantages utilisés chez un partenaire
 */
public interface HistoryExplorator {
    HistoryDTO getPartnerHistory(long partnerId) throws UnknownPartnerIdException;
}
```

## Statistics Explorator

```java
/**
 * Consultation des statistiques d'un partenaire
 */
public interface StatisticsExplorator {
    StatisticsDTO getStatisticsFromUserAndPartner(long partnerId, string customerEmail) throws UnknownPartnerIdException, UnknownCustomerEmailException;

    StatisticsDTO getStatisticsFromPartnerAndDate(long partnerId, LocalDateTime date) throws UnknownPartnerIdException;
}
```

# Card Editor Proxy

## Card Creation

```java
/**
 * Demande de création et d'envoi de carte multi-fidélité
 */
public interface CardCreation {
    CardDTO orderACard(CardCreationDTO cardInfo) throws UnreachableExternalServiceException;
}
```

# Bank Proxy

## Bank

```java
/**
 * Création et règlement d'un paiement
 */
public interface Bank {
    PaymentDTO askPayment(PaymentRequestDTO paymentInfo) throws UnreachableExternalServiceException;
}
```

# HappyKids Proxy

## HappyKids

```java
/**
 * Vérification de la disponibilité d'une réduction chez HappyKids
 */
public interface HappyKids {
    HappyKidsDiscountDTO computeDiscount(LocalDateTime wantedSlot) throws ClosedTimeException;
}
```

# 5 - Objet métier

![diagramme de classe](/ressources_uml/diagramme%20de%20classe.png)

## Justification

### Customer

Permet de stocker les informations propres à un client. Le nom, le prénom, l’adresse, et l’email sont renseignés lors de
l’enregistrement du client. Le numéro de carte est donné par le CardEditorProxy lorsque la carte multi-fidélité physique
a été commandée avec succès au service externe de gestion de carte. Le statut VFP est par défaut à false et il est mis à
jour chaque lundi matin à 00h00 en fonction des achats effectués dans le courant de la semaine précédente.

### Purchase

Un Purchase (achat) a un but de suivi d’historique. Il enregistre le Cart acheté et le Payment correspondant.

### Payment

Le Payment stocke le montant payé et l’horodatage du paiement.

### Cart

Permet de représenter le panier d’un Customer (client) créé chez un Partner (partenaire). Il stocke une liste de
CartItem que le client va acheter, et une référence vers le Partner chez qui il est constitué.

### CartItem

Représente un Item vendu par un Partner, et lui associe sa quantité. Il porte aussi les informations propres à certaine
instance d’achat comme l’heure de début et de fin d’un créneau de garde réservé chez HappyKids.

### Item

Permet de représenter un bien ou un service vendu par un Partner.

### Partner

Représente les informations d’un commerçant Partner de l’opération carte multi-fidélité. Il stocke les Perks (avantages)
proposés par ce Partner, les biens ou services mis en vente et aussi les achats qui ont été réalisés chez lui.

### PerkConsumable

Classe abstraite étendue par les classes pouvant être utilisées dans le calcul d’une réduction liée à un avantage.

### AbstractPerk

Permet de représenter de manière abstraite l’avantage proposé par un Partner.

### VfpDiscountInPercent

Un type d’avantage offrant une réduction en pourcentage du montant total de l’achat pour les customers ayant le statut
VFP.

### TimedDiscountInPercent

Un type d’avantage offrant une réduction en pourcentage du montant total de l’achat après une certaine heure de la
journée.

### NPurchasedMGiftedPerk

Un type d’avantage offrant une certaine quantité d’un Item après une certaine quantité achetée. Les items comptés dans
ce type de réduction sont cherchés dans les achats précédemment réalisés chez le Partner proposant cet avantage puis
marqué comme consommé dans un avantage.

# 6 - Scénario

## 1. **Inscription d’un client:**

    1. CLI Application → Customer Controller → Customer Catalog → Card Editor Proxy → Card Service.
    2. Retour positif de Card Service →Card Editor Proxy →  Customer Catalog  → Customer Repository 
    3. Retour de Customer Repository → Customer Catalog → Customer Controller → CLI Application

## 2. **Remplissage d’un panier par le partenaire :**

    1. CLI Application → Cart Controller → Cart Handler → Customer Catalog → Customer Repository
    2. Retour de Customer Repository → Customer Catalog → Cart Handler → Cart Controller → CLI Application

## 3. **Paiement d’un panier par le client :**

    1. CLI Application → Cart Controller → Cart Handler → Cashier → Bank Proxy → Bank Service
    2. Retour positif de Bank Service → Bank Proxy → Cashier → Purchase Registry→ Purchase Repository
    3. Retour de Purchase Repository → Purchase Registry→ Cashier → Cart Handler → Cart Controller → CLI Application

## 4. **Consulter un avantage par un client:**

    1. Perks Repository → Perks Registry→ Perks Controller → CLI Application
    2. CLI Application → Perks Controller → Perks Registry→ Perks Repository

## 5. **Vérifier un avantage par le partenaire :**

    1. CLI Application → Perks Controller → Perks Handler → Perks Registry→ Perks Repository 
    2. Retour de Perks Repository → Perks Registry→ Perks Handler → Cart Handler → Customer Catalog → Customer Repository 
    3. Retour de Customer Repository → Customer Catalog → Cart Handler → Perks Handler → Perks Controller → CLI Application

## 6. **Utiliser un avantage par le client  :**

    1. CLI Application → Perks Controller → Perks Handler → Cart Handler → Customer Catalog → Customer Repository 
    2. Retour de Customer Repository → Customer Catalog → Cart Handler → Perks Handler → Perks Controller → CLI Application