package fr.univcotedazur.teamj.kiwicard;

import fr.univcotedazur.teamj.kiwicard.dto.CustomerSubscribeDTO;
import fr.univcotedazur.teamj.kiwicard.dto.ItemDTO;
import fr.univcotedazur.teamj.kiwicard.entities.Cart;
import fr.univcotedazur.teamj.kiwicard.entities.CartItem;
import fr.univcotedazur.teamj.kiwicard.entities.Customer;
import fr.univcotedazur.teamj.kiwicard.entities.Item;
import fr.univcotedazur.teamj.kiwicard.entities.Partner;
import fr.univcotedazur.teamj.kiwicard.entities.Payment;
import fr.univcotedazur.teamj.kiwicard.entities.perks.TimedDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.entities.perks.VfpDiscountInPercentPerk;
import fr.univcotedazur.teamj.kiwicard.exceptions.UnknownPartnerIdException;
import fr.univcotedazur.teamj.kiwicard.interfaces.partner.IPartnerManager;
import fr.univcotedazur.teamj.kiwicard.repositories.ICustomerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPartnerRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPerkRepository;
import fr.univcotedazur.teamj.kiwicard.repositories.IPurchaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Profile("!test")
@Component
public class DataInsertionUseCaseRunner implements CommandLineRunner {

    private final ICustomerRepository customerRepository;
    private final IPartnerRepository partnerRepository;
    private final IPerkRepository perkRepository;
    private final IPurchaseRepository purchaseRepository;
    private final IPartnerManager partnerManager;
    private String customerRoxaneEmail;
    private String customerAntoineMEmail;
    private String customerAntoineFEmail;
    private String customerBaptisteEmail;
    private String customerClementEmail;
    private final boolean deleteAllData = true;
    private String customerEmail;

    public DataInsertionUseCaseRunner(ICustomerRepository customerRepository, IPartnerRepository partnerRepository, IPerkRepository perkRepository, IPurchaseRepository purchaseRepository, IPartnerManager partnerManager) {
        this.customerRepository = customerRepository;
        this.partnerRepository = partnerRepository;
        this.perkRepository = perkRepository;
        this.purchaseRepository = purchaseRepository;
        this.partnerManager = partnerManager;
    }


    @Override
    @Transactional
    public void run(String... args) throws UnknownPartnerIdException {
        tryToInsert();
        tryToRetrieve();
    }

    private void tryToRetrieve() {
        Customer customer = customerRepository.findByEmail(customerClementEmail).orElse(null);
        assert customer != null;
        System.out.println("Customer name: " + customer.getFirstName());
        Cart cart = customer.getCart();
        System.out.println("Cart partner: " + cart.getPartner().getName());
        System.out.println("Cart items: ");
        for (CartItem item : cart.getItemList()) {
            System.out.println("Item: " + item.getItem().getLabel() + " - Quantity: " + item.getQuantity());
        }
    }

    private void tryToInsert() throws UnknownPartnerIdException {

        // Customers
        CustomerSubscribeDTO customerSubscribeDTOAlice = new CustomerSubscribeDTO("alice.bob@gmail.com",
                "Alice",
                "bob",
                "blabliblou");
        Customer customerAlice = new Customer(
                customerSubscribeDTOAlice, "1234567890"
        );

        CustomerSubscribeDTO customerSubscribeDTOClement = new CustomerSubscribeDTO(
                "clement@armeedeterre.fr",
                "Clement",
                "lfv",
                "2 avenue des militaires, Callas"
        );
        Customer customerClement = new Customer(
                customerSubscribeDTOClement, "1234567891"
        );

        CustomerSubscribeDTO customerSubscribeDTOAntoineF = new CustomerSubscribeDTO(
                "antoine@fitnesspark.fr",
                "Antoine",
                "fadda",
                "3 rue des arcsitecte, Draguignan"
        );
        Customer customerAntoineF = new Customer(
                customerSubscribeDTOAntoineF, "1234567892"
        );


        CustomerSubscribeDTO customerSubscribeDTOAntoineM = new CustomerSubscribeDTO(
                "antoine@seancepull.fr",
                "Antoine",
                "maistre",
                "4 rue des pectoraux, Nice"
        );
        Customer customerAntoineM = new Customer(
                customerSubscribeDTOAntoineM, "1234567893"
        );


        CustomerSubscribeDTO customerSubscribeDTOBaptiste = new CustomerSubscribeDTO(
                "baptiste@tabarnak.fr",
                "Baptiste",
                "xxx",
                "5 rue des anonymes, St Laurent du Var"
        );
        Customer customerBaptiste = new Customer(
                customerSubscribeDTOBaptiste, "1234567894"
        );

        CustomerSubscribeDTO customerSubscribeDTORoxane = new CustomerSubscribeDTO(
                "roxane@princesse.fr",
                "Roxane",
                "Roxx",
                "Place du capitole, Toulouse"
        );
        Customer customerRoxane = new Customer(
                customerSubscribeDTORoxane, "1234567895"
        );


        customerRepository.save(customerAlice);
        customerRepository.save(customerClement);
        customerRepository.save(customerAntoineF);
        customerRepository.save(customerAntoineM);
        customerRepository.save(customerBaptiste);
        customerRepository.save(customerRoxane);

        // Partners
        Partner partnerBoulange = new Partner(
                "Boulange",
                "14 rue du paindemie, Draguignan"
        );

        Partner partnerFleuriste = new Partner(
                "Fleuriste",
                "13 rue des roses, Lorgues"
        );

        Partner partnerBoucherie = new Partner(
                "Boucherie",
                "12 rue des viandes, Le Luc"
        );

        Partner partnerPoissonnerie = new Partner(
                "Poissonnerie",
                "11 rue des poissons, Saint-Tropez"
        );

        Partner partnerFromagerie = new Partner(
                "Fromagerie",
                "10 rue des fromages, Sainte-Maxime"
        );


        partnerRepository.save(partnerBoulange);
        partnerRepository.save(partnerFleuriste);
        partnerRepository.save(partnerBoucherie);
        partnerRepository.save(partnerPoissonnerie);
        partnerRepository.save(partnerFromagerie);

        // Cart with Partner
        Cart cartBoulange = new Cart();
        cartBoulange.setPartner(partnerBoulange);

        Cart cartFleuriste = new Cart();
        cartFleuriste.setPartner(partnerFleuriste);

        Cart cartBoucherie = new Cart();
        cartBoucherie.setPartner(partnerBoucherie);

        Cart cartPoissonnerie = new Cart();
        cartPoissonnerie.setPartner(partnerPoissonnerie);

        Cart cartFromagerie = new Cart();
        cartFromagerie.setPartner(partnerFromagerie);

        // Customer with Cart
        customerAntoineM.setCart(cartBoulange);
        customerRepository.save(customerAntoineM);
        customerAntoineMEmail = customerAntoineM.getEmail();
        cartBoulange = customerAntoineM.getCart();

        customerRoxane.setCart(cartFleuriste);
        customerRepository.save(customerRoxane);
        customerRoxaneEmail = customerRoxane.getEmail();
        cartFleuriste = customerRoxane.getCart();

        customerAntoineF.setCart(cartBoucherie);
        customerRepository.save(customerAntoineF);
        customerAntoineFEmail = customerAntoineF.getEmail();
        cartBoucherie = customerAntoineF.getCart();

        customerBaptiste.setCart(cartPoissonnerie);
        customerRepository.save(customerBaptiste);
        customerBaptisteEmail = customerBaptiste.getEmail();
        cartPoissonnerie = customerBaptiste.getCart();

        customerClement.setCart(cartFromagerie);
        customerRepository.save(customerClement);
        customerClementEmail = customerClement.getEmail();
        cartFromagerie = customerClement.getCart();


        // Item Boulange
        ItemDTO itemDTOcroissant = new ItemDTO("croissant", 1.0);
        ItemDTO itemDTO2baguette = new ItemDTO("baguette", 1.2);
        ItemDTO itemDTO3choco = new ItemDTO("chocolatine", 1.5);
        ItemDTO itemDTO4raisin = new ItemDTO("pain au raisin", 1.8);

        partnerManager.addItemToPartnerCatalog(partnerBoulange.getPartnerId(), itemDTOcroissant);
        partnerManager.addItemToPartnerCatalog(partnerBoulange.getPartnerId(), itemDTO2baguette);
        partnerManager.addItemToPartnerCatalog(partnerBoulange.getPartnerId(), itemDTO3choco);
        partnerManager.addItemToPartnerCatalog(partnerBoulange.getPartnerId(), itemDTO4raisin);

        Item croissant = partnerManager.findAllPartnerItems(partnerBoulange.getPartnerId()).getFirst();
        Item baguette = partnerManager.findAllPartnerItems(partnerBoulange.getPartnerId()).get(1);
        Item chocolatine = partnerManager.findAllPartnerItems(partnerBoulange.getPartnerId()).get(2);
        Item raisin = partnerManager.findAllPartnerItems(partnerBoulange.getPartnerId()).get(3);

        // Item Fleuriste
        ItemDTO itemDTOrose = new ItemDTO("rose", 1.0);
        ItemDTO itemDTO2tulipe = new ItemDTO("tulipe", 1.2);
        ItemDTO itemDTO3muguet = new ItemDTO("muguet", 1.5);
        ItemDTO itemDTO4orchidee = new ItemDTO("orchidee", 1.8);

        partnerManager.addItemToPartnerCatalog(partnerFleuriste.getPartnerId(), itemDTOrose);
        partnerManager.addItemToPartnerCatalog(partnerFleuriste.getPartnerId(), itemDTO2tulipe);
        partnerManager.addItemToPartnerCatalog(partnerFleuriste.getPartnerId(), itemDTO3muguet);
        partnerManager.addItemToPartnerCatalog(partnerFleuriste.getPartnerId(), itemDTO4orchidee);

        Item rose = partnerManager.findAllPartnerItems(partnerFleuriste.getPartnerId()).getFirst();
        Item tulipe = partnerManager.findAllPartnerItems(partnerFleuriste.getPartnerId()).get(1);
        Item muguet = partnerManager.findAllPartnerItems(partnerFleuriste.getPartnerId()).get(2);
        Item orchidee = partnerManager.findAllPartnerItems(partnerFleuriste.getPartnerId()).get(3);

        // Item Boucherie
        ItemDTO itemDTOsteak = new ItemDTO("steak", 1.0);
        ItemDTO itemDTO2saucisse = new ItemDTO("saucisse", 1.2);
        ItemDTO itemDTO3jambon = new ItemDTO("jambon", 1.5);
        ItemDTO itemDTO4poulet = new ItemDTO("poulet", 1.8);

        partnerManager.addItemToPartnerCatalog(partnerBoucherie.getPartnerId(), itemDTOsteak);
        partnerManager.addItemToPartnerCatalog(partnerBoucherie.getPartnerId(), itemDTO2saucisse);
        partnerManager.addItemToPartnerCatalog(partnerBoucherie.getPartnerId(), itemDTO3jambon);
        partnerManager.addItemToPartnerCatalog(partnerBoucherie.getPartnerId(), itemDTO4poulet);

        Item steak = partnerManager.findAllPartnerItems(partnerBoucherie.getPartnerId()).getFirst();
        Item saucisse = partnerManager.findAllPartnerItems(partnerBoucherie.getPartnerId()).get(1);
        Item jambon = partnerManager.findAllPartnerItems(partnerBoucherie.getPartnerId()).get(2);
        Item poulet = partnerManager.findAllPartnerItems(partnerBoucherie.getPartnerId()).get(3);

        // Item Poissonnerie
        ItemDTO itemDTOsaumon = new ItemDTO("saumon", 1.0);
        ItemDTO itemDTO2cabillaud = new ItemDTO("cabillaud", 1.2);
        ItemDTO itemDTO3sardine = new ItemDTO("sardine", 1.5);
        ItemDTO itemDTO4thon = new ItemDTO("thon", 1.8);

        partnerManager.addItemToPartnerCatalog(partnerPoissonnerie.getPartnerId(), itemDTOsaumon);
        partnerManager.addItemToPartnerCatalog(partnerPoissonnerie.getPartnerId(), itemDTO2cabillaud);
        partnerManager.addItemToPartnerCatalog(partnerPoissonnerie.getPartnerId(), itemDTO3sardine);
        partnerManager.addItemToPartnerCatalog(partnerPoissonnerie.getPartnerId(), itemDTO4thon);

        Item saumon = partnerManager.findAllPartnerItems(partnerPoissonnerie.getPartnerId()).getFirst();
        Item cabillaud = partnerManager.findAllPartnerItems(partnerPoissonnerie.getPartnerId()).get(1);
        Item sardine = partnerManager.findAllPartnerItems(partnerPoissonnerie.getPartnerId()).get(2);
        Item thon = partnerManager.findAllPartnerItems(partnerPoissonnerie.getPartnerId()).get(3);

        // Item Fromagerie
        ItemDTO itemDTOcamembert = new ItemDTO("camembert", 1.0);
        ItemDTO itemDTO2roquefort = new ItemDTO("roquefort", 1.2);
        ItemDTO itemDTO3brie = new ItemDTO("brie", 1.5);
        ItemDTO itemDTO4comte = new ItemDTO("comte", 1.8);

        partnerManager.addItemToPartnerCatalog(partnerFromagerie.getPartnerId(), itemDTOcamembert);
        partnerManager.addItemToPartnerCatalog(partnerFromagerie.getPartnerId(), itemDTO2roquefort);
        partnerManager.addItemToPartnerCatalog(partnerFromagerie.getPartnerId(), itemDTO3brie);
        partnerManager.addItemToPartnerCatalog(partnerFromagerie.getPartnerId(), itemDTO4comte);

        Item camembert = partnerManager.findAllPartnerItems(partnerFromagerie.getPartnerId()).getFirst();
        Item roquefort = partnerManager.findAllPartnerItems(partnerFromagerie.getPartnerId()).get(1);
        Item brie = partnerManager.findAllPartnerItems(partnerFromagerie.getPartnerId()).get(2);
        Item comte = partnerManager.findAllPartnerItems(partnerFromagerie.getPartnerId()).get(3);


        // CartItem with cart and item and quantity Boulange
        CartItem cartItem = new CartItem();

        cartItem.setItem(croissant);
        cartItem.setQuantity(2);
        cartBoulange.addItem(cartItem);

        cartItem.setItem(baguette);
        cartItem.setQuantity(1);
        cartBoulange.addItem(cartItem);

        cartItem.setItem(chocolatine);
        cartItem.setQuantity(3);
        cartBoulange.addItem(cartItem);

        customerRepository.save(customerAntoineM);

        // CartItem with cart and item and quantity Fleuriste
        CartItem cartItemFleuriste = new CartItem();

        cartItemFleuriste.setItem(rose);
        cartItemFleuriste.setQuantity(2);
        cartFleuriste.addItem(cartItemFleuriste);

        cartItemFleuriste.setItem(tulipe);
        cartItemFleuriste.setQuantity(1);
        cartFleuriste.addItem(cartItemFleuriste);

        cartItemFleuriste.setItem(muguet);
        cartItemFleuriste.setQuantity(3);
        cartFleuriste.addItem(cartItemFleuriste);

        customerRepository.save(customerRoxane);

        // CartItem with cart and item and quantity Boucherie
        CartItem cartItemBoucherie = new CartItem();

        cartItemBoucherie.setItem(steak);
        cartItemBoucherie.setQuantity(2);
        cartBoucherie.addItem(cartItemBoucherie);

        cartItemBoucherie.setItem(saucisse);
        cartItemBoucherie.setQuantity(1);
        cartBoucherie.addItem(cartItemBoucherie);

        cartItemBoucherie.setItem(jambon);
        cartItemBoucherie.setQuantity(3);
        cartBoucherie.addItem(cartItemBoucherie);

        customerRepository.save(customerAntoineF);

        // CartItem with cart and item and quantity Poissonnerie
        CartItem cartItemPoissonnerie = new CartItem();

        cartItemPoissonnerie.setItem(saumon);
        cartItemPoissonnerie.setQuantity(2);
        cartPoissonnerie.addItem(cartItemPoissonnerie);

        cartItemPoissonnerie.setItem(cabillaud);
        cartItemPoissonnerie.setQuantity(1);
        cartPoissonnerie.addItem(cartItemPoissonnerie);

        cartItemPoissonnerie.setItem(sardine);
        cartItemPoissonnerie.setQuantity(3);
        cartPoissonnerie.addItem(cartItemPoissonnerie);

        customerRepository.save(customerBaptiste);

        // CartItem with cart and item and quantity Fromagerie

        CartItem cartItemFromagerie = new CartItem();

        cartItemFromagerie.setItem(camembert);
        cartItemFromagerie.setQuantity(2);
        cartFromagerie.addItem(cartItemFromagerie);

        cartItemFromagerie.setItem(roquefort);
        cartItemFromagerie.setQuantity(1);
        cartFromagerie.addItem(cartItemFromagerie);

        cartItemFromagerie.setItem(brie);
        cartItemFromagerie.setQuantity(3);
        cartFromagerie.addItem(cartItemFromagerie);

        customerRepository.save(customerClement);



        // Perk (Vfp discount in %)
        VfpDiscountInPercentPerk perk = new VfpDiscountInPercentPerk(0.05);
        TimedDiscountInPercentPerk perk2 = new TimedDiscountInPercentPerk(LocalTime.now(), 20);

        perkRepository.save(perk);
        perkRepository.save(perk2);
        cartBoulange.addPerkToUse(perk);
        cartFleuriste.addPerkToUse(perk2);

        // Payment
        Payment payment = new Payment(40, LocalDateTime.now());

//        // Purchase
//        Purchase purchase = new Purchase(payment, cart);
//        purchaseRepository.save(purchase);
//        customer.addPurchase(purchase);
//        customer.removeCart();
//        customerRepository.save(customer);
    }
}
