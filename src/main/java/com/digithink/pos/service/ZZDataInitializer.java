package com.digithink.pos.service;

import javax.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.digithink.pos.erp.enumeration.ErpSyncJobType;
import com.digithink.pos.erp.model.ErpSyncJob;
import com.digithink.pos.erp.repository.ErpSyncJobRepository;
import com.digithink.pos.erp.service.ErpSyncCheckpointService;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.model.Location;
import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.PaymentMethodType;
import com.digithink.pos.model.enumeration.Role;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository.GeneralSetupRepository;
import com.digithink.pos.repository.ItemBarcodeRepository;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.ItemSubFamilyRepository;
import com.digithink.pos.repository.LocationRepository;
import com.digithink.pos.repository.PaymentMethodRepository;
import com.digithink.pos.repository.UserAccountRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ZZDataInitializer {

	private UserAccountRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private PaymentMethodRepository paymentMethodRepository;
	private CustomerRepository customerRepository;
	private ItemRepository itemRepository;
	private ItemFamilyRepository itemFamilyRepository;
	private ItemSubFamilyRepository itemSubFamilyRepository;
	private ItemBarcodeRepository itemBarcodeRepository;
	private LocationRepository locationRepository;
	private GeneralSetupRepository generalSetupRepository;
	private ErpSyncJobRepository erpSyncJobRepository;

	@PostConstruct
	public void init() {
		if (userRepository.count() == 0) {
			// Create the initial users
			initUsers();
		}

		if (paymentMethodRepository.count() == 0 || !paymentMethodRepository.findByCode("CLIENT_ESPECES").isPresent()) {
			// Reset payment methods to new configuration
			paymentMethodRepository.deleteAll();
			initPaymentMethods();
		}

		if (customerRepository.count() == 0) {
			// Create default customer
			initDefaultCustomer();
		}
//
//		if (itemFamilyRepository.count() == 0) {
//			// Create item families
//			initItemFamilies();
//		}
//
//		if (itemSubFamilyRepository.count() == 0) {
//			// Create item sub-families
//			initItemSubFamilies();
//		}
//
//		if (itemRepository.count() == 0) {
//			// Create items
//			initItems();
//		}
//
//		if (itemBarcodeRepository.count() == 0) {
//			// Create item barcodes
//			initItemBarcodes();
//		}
//
//		if (locationRepository.count() == 0) {
//			// Create locations
//			initLocations();
//		}

		if (generalSetupRepository.count() == 0) {
			// Create general setup records
			initGeneralSetup();
		}
		ensureErpSyncCheckpointConfigs();

		if (erpSyncJobRepository.count() == 0) {
			initErpSyncJobs();
		}
	}

	/**
	 * Create initial users: Admin, Responsible, POS User
	 */
	private void initUsers() {
		// System Administrator
		UserAccount admin = new UserAccount();
		admin.setUsername("admin");
		admin.setFullName("System Administrator");
		admin.setPassword(passwordEncoder.encode("Admin@123"));
		admin.setEmail("admin@hammai-group.tn");
		admin.setActive(true);
		admin.setRole(Role.ADMIN);
		admin.setCreatedBy("System");
		admin.setUpdatedBy("System");
		userRepository.save(admin);

		// Responsible
		UserAccount responsible = new UserAccount();
		responsible.setUsername("responsible");
		responsible.setFullName("Responsible Manager");
		responsible.setPassword(passwordEncoder.encode("Resp@123"));
		responsible.setEmail("responsible@hammai-group.tn");
		responsible.setActive(true);
		responsible.setRole(Role.RESPONSIBLE);
		responsible.setCreatedBy("System");
		responsible.setUpdatedBy("System");
		userRepository.save(responsible);

		// POS User / Cashier
		UserAccount posUser = new UserAccount();
		posUser.setUsername("cashier");
		posUser.setFullName("Cashier User");
		posUser.setPassword(passwordEncoder.encode("Cashier@123"));
		posUser.setEmail("cashier@hammai-group.tn");
		posUser.setActive(true);
		posUser.setRole(Role.POS_USER);
		posUser.setCreatedBy("System");
		posUser.setUpdatedBy("System");
		userRepository.save(posUser);
	}

	/**
	 * Create initial payment methods for Tunisia
	 */
	private void initPaymentMethods() {
		// Client Espèce - Order 1
		PaymentMethod clientCash = new PaymentMethod();
		clientCash.setCode("CLIENT_ESPECES");
		clientCash.setName("Client Espèce");
		clientCash.setType(PaymentMethodType.CLIENT_ESPECES);
		clientCash.setDescription("Paiement en espèces du client");
		clientCash.setActive(true);
		clientCash.setDisplayOrder(1);
		clientCash.setCreatedBy("System");
		clientCash.setUpdatedBy("System");
		paymentMethodRepository.save(clientCash);

		// Client TPE (Terminal de paiement électronique) - Order 2
		PaymentMethod clientTpe = new PaymentMethod();
		clientTpe.setCode("CLIENT_TPE");
		clientTpe.setName("Client TPE");
		clientTpe.setType(PaymentMethodType.CLIENT_TPE);
		clientTpe.setDescription("Paiement via terminal électronique (TPE)");
		clientTpe.setActive(true);
		clientTpe.setDisplayOrder(2);
		clientTpe.setCreatedBy("System");
		clientTpe.setUpdatedBy("System");
		paymentMethodRepository.save(clientTpe);

		// Ticket Restaurant - Order 3
		PaymentMethod mealTicket = new PaymentMethod();
		mealTicket.setCode("TICKET_RESTAURANT");
		mealTicket.setName("Ticket Restaurant");
		mealTicket.setType(PaymentMethodType.TICKET_RESTAURANT);
		mealTicket.setDescription("Paiement via ticket restaurant");
		mealTicket.setRequireTitleNumber(true);
		mealTicket.setActive(true);
		mealTicket.setDisplayOrder(3);
		mealTicket.setCreatedBy("System");
		mealTicket.setUpdatedBy("System");
		paymentMethodRepository.save(mealTicket);

		// Chèque Cadeau - Order 4
		PaymentMethod giftCheck = new PaymentMethod();
		giftCheck.setCode("CHEQUE_CADEAU");
		giftCheck.setName("Chèque Cadeau");
		giftCheck.setType(PaymentMethodType.CHEQUE_CADEAU);
		giftCheck.setDescription("Paiement par chèque cadeau");
		giftCheck.setRequireTitleNumber(true);
		giftCheck.setActive(true);
		giftCheck.setDisplayOrder(4);
		giftCheck.setCreatedBy("System");
		giftCheck.setUpdatedBy("System");
		paymentMethodRepository.save(giftCheck);

		// Client Chèque - Order 5
		PaymentMethod clientCheque = new PaymentMethod();
		clientCheque.setCode("CLIENT_CHEQUE");
		clientCheque.setName("Client Chèque");
		clientCheque.setType(PaymentMethodType.CLIENT_CHEQUE);
		clientCheque.setDescription("Paiement par chèque client");
		clientCheque.setRequireTitleNumber(true);
		clientCheque.setRequireDrawerName(true);
		clientCheque.setRequireIssuingBank(true);
		clientCheque.setActive(true);
		clientCheque.setDisplayOrder(5);
		clientCheque.setCreatedBy("System");
		clientCheque.setUpdatedBy("System");
		paymentMethodRepository.save(clientCheque);

		// Client Traite - Order 6
		PaymentMethod clientTraite = new PaymentMethod();
		clientTraite.setCode("CLIENT_TRAITE");
		clientTraite.setName("Client Traite");
		clientTraite.setType(PaymentMethodType.CLIENT_TRAITE);
		clientTraite.setDescription("Paiement par traite client");
		clientTraite.setRequireTitleNumber(true);
		clientTraite.setRequireDueDate(true);
		clientTraite.setRequireDrawerName(true);
		clientTraite.setRequireIssuingBank(true);
		clientTraite.setActive(true);
		clientTraite.setDisplayOrder(6);
		clientTraite.setCreatedBy("System");
		clientTraite.setUpdatedBy("System");
		paymentMethodRepository.save(clientTraite);

		// Dépôt en banque - Order 7
		PaymentMethod bankDeposit = new PaymentMethod();
		bankDeposit.setCode("DEPOT_BANQUE");
		bankDeposit.setName("Dépôt en banque");
		bankDeposit.setType(PaymentMethodType.DEPOT_BANQUE);
		bankDeposit.setDescription("Dépôt des fonds en banque");
		bankDeposit.setActive(true);
		bankDeposit.setDisplayOrder(7);
		bankDeposit.setCreatedBy("System");
		bankDeposit.setUpdatedBy("System");
		paymentMethodRepository.save(bankDeposit);

		// Return Voucher - Order 8
		PaymentMethod returnVoucher = new PaymentMethod();
		returnVoucher.setCode("RETURN_VOUCHER");
		returnVoucher.setName("Bon de Retour");
		returnVoucher.setType(PaymentMethodType.RETURN_VOUCHER);
		returnVoucher.setDescription("Bon de retour émis pour un retour de produit");
		returnVoucher.setActive(true);
		returnVoucher.setDisplayOrder(8);
		returnVoucher.setCreatedBy("System");
		returnVoucher.setUpdatedBy("System");
		paymentMethodRepository.save(returnVoucher);
	}

	/**
	 * Create initial customers for Hammai Group Tunisia
	 */
	private void initDefaultCustomer() {
		// Passenger customer (for POS tickets when no customer selected)
		Customer passengerCustomer = new Customer();
		passengerCustomer.setCustomerCode("PASSENGER");
		passengerCustomer.setName("Passenger Customer");
		passengerCustomer.setEmail("passenger@pos.local");
		passengerCustomer.setPhone("0000000000"); // Required field, use placeholder
		passengerCustomer.setAddress("");
		passengerCustomer.setCity("");
		passengerCustomer.setCountry("");
		passengerCustomer.setActive(true);
		passengerCustomer.setCreatedBy("System");
		passengerCustomer.setUpdatedBy("System");
		customerRepository.save(passengerCustomer);
	}
//
//	/**
//	 * Create initial item families
//	 */
//	private void initItemFamilies() {
//		ItemFamily electronics = new ItemFamily();
//		electronics.setCode("FAM_ELECTRONICS");
//		electronics.setName("Électronique");
//		electronics.setDescription("Produits électroniques et accessoires");
//		electronics.setDisplayOrder(1);
//		electronics.setCreatedBy("System");
//		electronics.setUpdatedBy("System");
//		itemFamilyRepository.save(electronics);
//
//		ItemFamily services = new ItemFamily();
//		services.setCode("FAM_SERVICES");
//		services.setName("Services");
//		services.setDescription("Prestations de service");
//		services.setDisplayOrder(2);
//		services.setCreatedBy("System");
//		services.setUpdatedBy("System");
//		itemFamilyRepository.save(services);
//
//		ItemFamily promotions = new ItemFamily();
//		promotions.setCode("FAM_PROMOTIONS");
//		promotions.setName("Promotions");
//		promotions.setDescription("Offres promotionnelles et packs");
//		promotions.setDisplayOrder(3);
//		promotions.setCreatedBy("System");
//		promotions.setUpdatedBy("System");
//		itemFamilyRepository.save(promotions);
//	}
//
//	/**
//	 * Create initial item sub-families
//	 */
//	private void initItemSubFamilies() {
//		ItemFamily electronics = itemFamilyRepository.findByCode("FAM_ELECTRONICS").orElse(null);
//		ItemFamily services = itemFamilyRepository.findByCode("FAM_SERVICES").orElse(null);
//		ItemFamily promotions = itemFamilyRepository.findByCode("FAM_PROMOTIONS").orElse(null);
//
//		if (electronics != null) {
//			ItemSubFamily smartphones = new ItemSubFamily();
//			smartphones.setCode("SUB_ELECTRONICS_MOBILE");
//			smartphones.setName("Smartphones");
//			smartphones.setDescription("Téléphones intelligents et mobiles");
//			smartphones.setDisplayOrder(1);
//			smartphones.setItemFamily(electronics);
//			smartphones.setCreatedBy("System");
//			smartphones.setUpdatedBy("System");
//			itemSubFamilyRepository.save(smartphones);
//
//			ItemSubFamily accessories = new ItemSubFamily();
//			accessories.setCode("SUB_ELECTRONICS_ACCESSORIES");
//			accessories.setName("Accessoires");
//			accessories.setDescription("Accessoires électroniques divers");
//			accessories.setDisplayOrder(2);
//			accessories.setItemFamily(electronics);
//			accessories.setCreatedBy("System");
//			accessories.setUpdatedBy("System");
//			itemSubFamilyRepository.save(accessories);
//		}
//
//		if (services != null) {
//			ItemSubFamily installations = new ItemSubFamily();
//			installations.setCode("SUB_SERVICES_INSTALL");
//			installations.setName("Installation");
//			installations.setDescription("Services d'installation et configuration");
//			installations.setDisplayOrder(1);
//			installations.setItemFamily(services);
//			installations.setCreatedBy("System");
//			installations.setUpdatedBy("System");
//			itemSubFamilyRepository.save(installations);
//		}
//
//		if (promotions != null) {
//			ItemSubFamily bundles = new ItemSubFamily();
//			bundles.setCode("SUB_PROMOTIONS_BUNDLES");
//			bundles.setName("Packs Promotionnels");
//			bundles.setDescription("Packs combinant plusieurs produits");
//			bundles.setDisplayOrder(1);
//			bundles.setItemFamily(promotions);
//			bundles.setCreatedBy("System");
//			bundles.setUpdatedBy("System");
//			itemSubFamilyRepository.save(bundles);
//		}
//	}
//
//	/**
//	 * Create initial items/products for Hammai Group Tunisia
//	 */
//	private void initItems() {
//		ItemFamily electronics = itemFamilyRepository.findByCode("FAM_ELECTRONICS").orElse(null);
//		ItemFamily servicesFamily = itemFamilyRepository.findByCode("FAM_SERVICES").orElse(null);
//		ItemFamily promotionsFamily = itemFamilyRepository.findByCode("FAM_PROMOTIONS").orElse(null);
//
//		ItemSubFamily smartphones = itemSubFamilyRepository.findByCode("SUB_ELECTRONICS_MOBILE").orElse(null);
//		ItemSubFamily accessories = itemSubFamilyRepository.findByCode("SUB_ELECTRONICS_ACCESSORIES").orElse(null);
//		ItemSubFamily installations = itemSubFamilyRepository.findByCode("SUB_SERVICES_INSTALL").orElse(null);
//		ItemSubFamily bundles = itemSubFamilyRepository.findByCode("SUB_PROMOTIONS_BUNDLES").orElse(null);
//
//		// Product 1
//		Item item1 = new Item();
//		item1.setItemCode("PROD001");
//		item1.setName("Produit Premium");
//		item1.setDescription("Produit de haute qualité premium");
//		item1.setType(ItemType.PRODUCT);
//		item1.setUnitPrice(250.00);
//		item1.setCostPrice(180.00);
//		item1.setStockQuantity(150);
//		item1.setMinStockLevel(20);
//		item1.setBarcode("1234567890123");
//		item1.setTaxable(true);
//		item1.setTaxRate(0.19); // 19% VAT in Tunisia
//		item1.setUnitOfMeasure("PIECE");
//		item1.setCategory("Électronique");
//		item1.setBrand("Hammai Brand");
//		item1.setItemFamily(electronics);
//		item1.setItemSubFamily(smartphones);
//		item1.setActive(true);
//		item1.setCreatedBy("System");
//		item1.setUpdatedBy("System");
//		itemRepository.save(item1);
//
//		// Product 2
//		Item item2 = new Item();
//		item2.setItemCode("PROD002");
//		item2.setName("Produit Standard");
//		item2.setDescription("Produit standard de qualité");
//		item2.setType(ItemType.PRODUCT);
//		item2.setUnitPrice(150.00);
//		item2.setCostPrice(100.00);
//		item2.setStockQuantity(300);
//		item2.setMinStockLevel(50);
//		item2.setBarcode("1234567890124");
//		item2.setTaxable(true);
//		item2.setTaxRate(0.19);
//		item2.setUnitOfMeasure("PIECE");
//		item2.setCategory("Électronique");
//		item2.setBrand("Hammai Brand");
//		item2.setItemFamily(electronics);
//		item2.setItemSubFamily(accessories);
//		item2.setActive(true);
//		item2.setCreatedBy("System");
//		item2.setUpdatedBy("System");
//		itemRepository.save(item2);
//
//		// Product 3
//		Item item3 = new Item();
//		item3.setItemCode("PROD003");
//		item3.setName("Produit Économique");
//		item3.setDescription("Produit économique pour tous");
//		item3.setType(ItemType.PRODUCT);
//		item3.setUnitPrice(75.00);
//		item3.setCostPrice(50.00);
//		item3.setStockQuantity(500);
//		item3.setMinStockLevel(100);
//		item3.setBarcode("1234567890125");
//		item3.setTaxable(true);
//		item3.setTaxRate(0.19);
//		item3.setUnitOfMeasure("PIECE");
//		item3.setCategory("Électronique");
//		item3.setBrand("Hammai Brand");
//		item3.setItemFamily(electronics);
//		item3.setItemSubFamily(accessories);
//		item3.setActive(true);
//		item3.setCreatedBy("System");
//		item3.setUpdatedBy("System");
//		itemRepository.save(item3);
//
//		// Service
//		Item service = new Item();
//		service.setItemCode("SERV001");
//		service.setName("Service Installation");
//		service.setDescription("Service d'installation professionnel");
//		service.setType(ItemType.SERVICE);
//		service.setUnitPrice(350.00);
//		service.setCostPrice(200.00);
//		service.setStockQuantity(999999); // Unlimited for services
//		service.setTaxable(true);
//		service.setTaxRate(0.19);
//		service.setUnitOfMeasure("SERVICE");
//		service.setCategory("Services");
//		service.setBrand("Hammai Services");
//		service.setItemFamily(servicesFamily);
//		service.setItemSubFamily(installations);
//		service.setActive(true);
//		service.setCreatedBy("System");
//		service.setUpdatedBy("System");
//		itemRepository.save(service);
//
//		// Package Deal
//		Item packageDeal = new Item();
//		packageDeal.setItemCode("PKG001");
//		packageDeal.setName("Pack Promo");
//		packageDeal.setDescription("Pack promotionnel spécial");
//		packageDeal.setType(ItemType.PACKAGE);
//		packageDeal.setUnitPrice(800.00);
//		packageDeal.setCostPrice(600.00);
//		packageDeal.setStockQuantity(50);
//		packageDeal.setMinStockLevel(10);
//		packageDeal.setTaxable(true);
//		packageDeal.setTaxRate(0.19);
//		packageDeal.setUnitOfMeasure("PACK");
//		packageDeal.setCategory("Offres Promotionnelles");
//		packageDeal.setBrand("Hammai Promo");
//		packageDeal.setItemFamily(promotionsFamily);
//		packageDeal.setItemSubFamily(bundles);
//		packageDeal.setActive(true);
//		packageDeal.setCreatedBy("System");
//		packageDeal.setUpdatedBy("System");
//		itemRepository.save(packageDeal);
//	}
//
//	/**
//	 * Create initial item barcodes
//	 */
//	private void initItemBarcodes() {
//		// Get items
//		Item item1 = itemRepository.findByItemCode("PROD001").orElse(null);
//		Item item2 = itemRepository.findByItemCode("PROD002").orElse(null);
//		Item item3 = itemRepository.findByItemCode("PROD003").orElse(null);
//		Item packageDeal = itemRepository.findByItemCode("PKG001").orElse(null);
//
//		if (item1 != null) {
//			// Primary barcode for item1
//			ItemBarcode barcode1 = new ItemBarcode();
//			barcode1.setItem(item1);
//			barcode1.setBarcode("1234567890123");
//			barcode1.setDescription("Primary barcode");
//			barcode1.setIsPrimary(true);
//			barcode1.setActive(true);
//			barcode1.setCreatedBy("System");
//			barcode1.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode1);
//
//			// Additional barcode for item1
//			ItemBarcode barcode1a = new ItemBarcode();
//			barcode1a.setItem(item1);
//			barcode1a.setBarcode("1234567890123-ALT");
//			barcode1a.setDescription("Alternative barcode");
//			barcode1a.setIsPrimary(false);
//			barcode1a.setActive(true);
//			barcode1a.setCreatedBy("System");
//			barcode1a.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode1a);
//		}
//
//		if (item2 != null) {
//			// Primary barcode for item2
//			ItemBarcode barcode2 = new ItemBarcode();
//			barcode2.setItem(item2);
//			barcode2.setBarcode("1234567890124");
//			barcode2.setDescription("Primary barcode");
//			barcode2.setIsPrimary(true);
//			barcode2.setActive(true);
//			barcode2.setCreatedBy("System");
//			barcode2.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode2);
//
//			// Additional barcodes for item2
//			ItemBarcode barcode2a = new ItemBarcode();
//			barcode2a.setItem(item2);
//			barcode2a.setBarcode("1234567890124-A");
//			barcode2a.setDescription("Alternative barcode A");
//			barcode2a.setIsPrimary(false);
//			barcode2a.setActive(true);
//			barcode2a.setCreatedBy("System");
//			barcode2a.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode2a);
//
//			ItemBarcode barcode2b = new ItemBarcode();
//			barcode2b.setItem(item2);
//			barcode2b.setBarcode("1234567890124-B");
//			barcode2b.setDescription("Alternative barcode B");
//			barcode2b.setIsPrimary(false);
//			barcode2b.setActive(true);
//			barcode2b.setCreatedBy("System");
//			barcode2b.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode2b);
//		}
//
//		if (item3 != null) {
//			// Primary barcode for item3
//			ItemBarcode barcode3 = new ItemBarcode();
//			barcode3.setItem(item3);
//			barcode3.setBarcode("1234567890125");
//			barcode3.setDescription("Primary barcode");
//			barcode3.setIsPrimary(true);
//			barcode3.setActive(true);
//			barcode3.setCreatedBy("System");
//			barcode3.setUpdatedBy("System");
//			itemBarcodeRepository.save(barcode3);
//		}
//
//		if (packageDeal != null) {
//			// Barcode for package deal
//			ItemBarcode pkgBarcode = new ItemBarcode();
//			pkgBarcode.setItem(packageDeal);
//			pkgBarcode.setBarcode("9876543210987");
//			pkgBarcode.setDescription("Package barcode");
//			pkgBarcode.setIsPrimary(true);
//			pkgBarcode.setActive(true);
//			pkgBarcode.setCreatedBy("System");
//			pkgBarcode.setUpdatedBy("System");
//			itemBarcodeRepository.save(pkgBarcode);
//		}
//	}

//	/**
//	 * Create initial locations/stores
//	 */
//	private void initLocations() {
//		// Main Store / Headquarters
//		Location mainStore = new Location();
//		mainStore.setLocationCode("LOC001");
//		mainStore.setName("Magasin Principal");
//		mainStore.setDescription("Magasin principal et siège social");
//		mainStore.setAddress("Zone Industrielle");
//		mainStore.setCity("Tunis");
//		mainStore.setState("Tunis");
//		mainStore.setCountry("Tunisie");
//		mainStore.setPostalCode("1000");
//		mainStore.setPhone("+216 71 234 567");
//		mainStore.setEmail("store1@hammai-group.tn");
//		mainStore.setContactPerson("Manager Principal");
//		mainStore.setIsDefault(true);
//		mainStore.setActive(true);
//		mainStore.setCreatedBy("System");
//		mainStore.setUpdatedBy("System");
//		locationRepository.save(mainStore);
//
//		// Branch Store 1
//		Location branch1 = new Location();
//		branch1.setLocationCode("LOC002");
//		branch1.setName("Succursale Sfax");
//		branch1.setDescription("Succursale dans la ville de Sfax");
//		branch1.setAddress("Avenue Habib Bourguiba");
//		branch1.setCity("Sfax");
//		branch1.setState("Sfax");
//		branch1.setCountry("Tunisie");
//		branch1.setPostalCode("3000");
//		branch1.setPhone("+216 74 345 678");
//		branch1.setEmail("store2@hammai-group.tn");
//		branch1.setContactPerson("Manager Sfax");
//		branch1.setIsDefault(false);
//		branch1.setActive(true);
//		branch1.setCreatedBy("System");
//		branch1.setUpdatedBy("System");
//		locationRepository.save(branch1);
//
//		// Branch Store 2
//		Location branch2 = new Location();
//		branch2.setLocationCode("LOC003");
//		branch2.setName("Succursale Ariana");
//		branch2.setDescription("Succursale dans la région d'Ariana");
//		branch2.setAddress("Avenue de la République");
//		branch2.setCity("Ariana");
//		branch2.setState("Ariana");
//		branch2.setCountry("Tunisie");
//		branch2.setPostalCode("2080");
//		branch2.setPhone("+216 71 456 789");
//		branch2.setEmail("store3@hammai-group.tn");
//		branch2.setContactPerson("Manager Ariana");
//		branch2.setIsDefault(false);
//		branch2.setActive(true);
//		branch2.setCreatedBy("System");
//		branch2.setUpdatedBy("System");
//		locationRepository.save(branch2);
//
//		// Warehouse
//		Location warehouse = new Location();
//		warehouse.setLocationCode("LOC004");
//		warehouse.setName("Entrepôt Central");
//		warehouse.setDescription("Entrepôt central pour stockage");
//		warehouse.setAddress("Zone Logistique");
//		warehouse.setCity("Ben Arous");
//		warehouse.setState("Ben Arous");
//		warehouse.setCountry("Tunisie");
//		warehouse.setPostalCode("2013");
//		warehouse.setPhone("+216 71 567 890");
//		warehouse.setEmail("warehouse@hammai-group.tn");
//		warehouse.setContactPerson("Responsable Entrepôt");
//		warehouse.setIsDefault(false);
//		warehouse.setActive(true);
//		warehouse.setCreatedBy("System");
//		warehouse.setUpdatedBy("System");
//		locationRepository.save(warehouse);
//	}

	/**
	 * Create initial general setup records
	 */
	private void initGeneralSetup() {
		// Get default location
		Location defaultLocation = locationRepository.findByIsDefaultTrue()
				.orElse(locationRepository.findAll().stream().findFirst().orElse(null));

		String locationValue = defaultLocation != null ? defaultLocation.getLocationCode() : "";

		// Location setup record
		GeneralSetup locationSetup = new GeneralSetup();
		locationSetup.setCode("DEFAULT_LOCATION");
		locationSetup.setValeur(locationValue);
		locationSetup.setDescription("Default location code for the system");
		locationSetup.setReadOnly(false);
		locationSetup.setActive(true);
		locationSetup.setCreatedBy("System");
		locationSetup.setUpdatedBy("System");
		generalSetupRepository.save(locationSetup);

		// Passenger customer setup record
		Customer passengerCustomer = customerRepository.findByCustomerCode("PASSENGER")
				.orElse(customerRepository.findAll().stream().findFirst().orElse(null));

		if (passengerCustomer != null) {
			String customerValue = String.valueOf(passengerCustomer.getId());
			GeneralSetup customerSetup = new GeneralSetup();
			customerSetup.setCode("PASSENGER_CUSTOMER");
			customerSetup.setValeur(customerValue);
			customerSetup.setDescription("Passenger customer ID for POS tickets when no customer is selected");
			customerSetup.setReadOnly(false);
			customerSetup.setActive(true);
			customerSetup.setCreatedBy("System");
			customerSetup.setUpdatedBy("System");
			generalSetupRepository.save(customerSetup);
		}

		// MAX_DAYS_FOR_RETURN
		if (!generalSetupRepository.findByCode("MAX_DAYS_FOR_RETURN").isPresent()) {
			GeneralSetup maxDaysSetup = new GeneralSetup();
			maxDaysSetup.setCode("MAX_DAYS_FOR_RETURN");
			maxDaysSetup.setValeur("10");
			maxDaysSetup.setDescription("Maximum number of days allowed for product returns");
			maxDaysSetup.setReadOnly(false);
			maxDaysSetup.setActive(true);
			maxDaysSetup.setCreatedBy("System");
			maxDaysSetup.setUpdatedBy("System");
			generalSetupRepository.save(maxDaysSetup);
		}

		// ENABLE_SIMPLE_RETURN
		if (!generalSetupRepository.findByCode("ENABLE_SIMPLE_RETURN").isPresent()) {
			GeneralSetup enableSimpleReturn = new GeneralSetup();
			enableSimpleReturn.setCode("ENABLE_SIMPLE_RETURN");
			enableSimpleReturn.setValeur("true");
			enableSimpleReturn.setDescription("Enable simple return (cash refund without voucher)");
			enableSimpleReturn.setReadOnly(false);
			enableSimpleReturn.setActive(true);
			enableSimpleReturn.setCreatedBy("System");
			enableSimpleReturn.setUpdatedBy("System");
			generalSetupRepository.save(enableSimpleReturn);
		}

		// RETURN_VOUCHER_VALIDITY_DAYS
		if (!generalSetupRepository.findByCode("RETURN_VOUCHER_VALIDITY_DAYS").isPresent()) {
			GeneralSetup voucherValidity = new GeneralSetup();
			voucherValidity.setCode("RETURN_VOUCHER_VALIDITY_DAYS");
			voucherValidity.setValeur("30");
			voucherValidity.setDescription("Number of days a return voucher remains valid");
			voucherValidity.setReadOnly(false);
			voucherValidity.setActive(true);
			voucherValidity.setCreatedBy("System");
			voucherValidity.setUpdatedBy("System");
			generalSetupRepository.save(voucherValidity);
		}

		if (!generalSetupRepository.findByCode("ERP_SYNC_TRACKING_LEVEL").isPresent()) {
			GeneralSetup erpTracking = new GeneralSetup();
			erpTracking.setCode("ERP_SYNC_TRACKING_LEVEL");
			erpTracking.setValeur("ERRORS_ONLY");
			erpTracking.setDescription("ERP communication tracking level (ERRORS_ONLY | ERRORS_AND_WARNINGS | ALL)");
			erpTracking.setReadOnly(false);
			erpTracking.setActive(true);
			erpTracking.setCreatedBy("System");
			erpTracking.setUpdatedBy("System");
			generalSetupRepository.save(erpTracking);
		}
	}

	private void ensureErpSyncCheckpointConfigs() {
		ErpSyncCheckpointService.getCheckpointDescriptions().forEach((code, description) -> {
			if (generalSetupRepository.findByCode(code).isPresent()) {
				return;
			}
			GeneralSetup checkpoint = new GeneralSetup();
			checkpoint.setCode(code);
			checkpoint.setValeur("");
			checkpoint.setDescription(description);
			checkpoint.setReadOnly(true);
			checkpoint.setActive(true);
			checkpoint.setCreatedBy("System");
			checkpoint.setUpdatedBy("System");
			generalSetupRepository.save(checkpoint);
		});
	}

	private void initErpSyncJobs() {
		createErpJob("0 0 2 * * *", ErpSyncJobType.IMPORT_ITEM_FAMILIES, "Daily import of item families", false);
		createErpJob("0 10 2 * * *", ErpSyncJobType.IMPORT_ITEM_SUBFAMILIES, "Daily import of item subfamilies", false);
		createErpJob("0 0 * * * *", ErpSyncJobType.IMPORT_ITEMS, "Hourly import of items", false);
		createErpJob("0 10 * * * *", ErpSyncJobType.IMPORT_ITEM_BARCODES, "Hourly import of item barcodes", false);
		createErpJob("0 20 2 * * *", ErpSyncJobType.IMPORT_LOCATIONS, "Daily import of locations", false);
		createErpJob("0 30 * * * *", ErpSyncJobType.IMPORT_CUSTOMERS, "Hourly import of customers", false);
		createErpJob("0 0 0 1 1 *", ErpSyncJobType.EXPORT_CUSTOMERS,
				"Template job for exporting customers (disabled by default)", false);
		createErpJob("0 0 0 1 1 *", ErpSyncJobType.EXPORT_TICKETS,
				"Template job for exporting tickets (disabled by default)", false);
	}

	private void createErpJob(String cron, ErpSyncJobType type, String description, boolean enabled) {
		if (erpSyncJobRepository.findByJobType(type).isPresent()) {
			return;
		}
		ErpSyncJob job = new ErpSyncJob();
		job.setJobType(type);
		job.setCronExpression(cron);
		job.setDescription(description);
		job.setEnabled(enabled);
		job.setCreatedBy("System");
		job.setUpdatedBy("System");
		erpSyncJobRepository.save(job);
	}
}
