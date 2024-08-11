package com.digithink.business_management.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.digithink.business_management.model.Company;
import com.digithink.business_management.model.Permission;
import com.digithink.business_management.model.Role;
import com.digithink.business_management.model.UserAccount;
import com.digithink.business_management.model.configuration.Currency;
import com.digithink.business_management.model.configuration.ItemDiscountGroup;
import com.digithink.business_management.model.configuration.ItemUnitOfMeasure;
import com.digithink.business_management.model.configuration.SeriesHeader;
import com.digithink.business_management.model.configuration.SeriesLine;
import com.digithink.business_management.model.configuration.UnitOfMeasure;
import com.digithink.business_management.model.enumeration.AutomaticCostAdjustment;
import com.digithink.business_management.model.enumeration.AverageCostPeriod;
import com.digithink.business_management.model.enumeration.DefaultAccountingDate;
import com.digithink.business_management.model.enumeration.DefaultQuantityToReceive;
import com.digithink.business_management.model.enumeration.DefaultQuantityToShip;
import com.digithink.business_management.model.enumeration.GLAccountCategory;
import com.digithink.business_management.model.enumeration.GLAccountType;
import com.digithink.business_management.model.enumeration.GLDebitOrCredit;
import com.digithink.business_management.model.enumeration.GLManagementOrBalance;
import com.digithink.business_management.model.enumeration.PermissionAction;
import com.digithink.business_management.model.enumeration.PermissionPage;
import com.digithink.business_management.model.enumeration.SeriesHeaderType;
import com.digithink.business_management.model.enumeration.StockEvaluationMode;
import com.digithink.business_management.model.general_ledger.GeneralLedgerAccount;
import com.digithink.business_management.model.general_ledger.GeneralLedgerSetup;
import com.digithink.business_management.model.inventory.Location;
import com.digithink.business_management.model.posting_group.GeneralBusinessPostingGroup;
import com.digithink.business_management.model.posting_group.GeneralProductPostingGroup;
import com.digithink.business_management.model.posting_group.InventoryPostingGroup;
import com.digithink.business_management.model.posting_group.VatBusinessPostingGroup;
import com.digithink.business_management.model.posting_group.VatProductPostingGroup;
import com.digithink.business_management.model.posting_setup.GeneralPostingSetup;
import com.digithink.business_management.model.posting_setup.InventoryPostingSetup;
import com.digithink.business_management.model.posting_setup.VatPostingSetup;
import com.digithink.business_management.model.setup.InventorySetup;
import com.digithink.business_management.model.setup.PurchaseSetup;
import com.digithink.business_management.model.setup.SalesSetup;
import com.digithink.business_management.repository.CompanyRepository;
import com.digithink.business_management.repository.PermissionRepository;
import com.digithink.business_management.repository.RoleRepository;
import com.digithink.business_management.repository.UserAccountRepository;
import com.digithink.business_management.service.configuration.CurrencyService;
import com.digithink.business_management.service.configuration.ItemDiscountGroupService;
import com.digithink.business_management.service.configuration.ItemUnitOfMeasureService;
import com.digithink.business_management.service.configuration.SeriesHeaderService;
import com.digithink.business_management.service.configuration.SeriesLineService;
import com.digithink.business_management.service.configuration.UnitOfMeasureService;
import com.digithink.business_management.service.general_ledger.GeneralLedgerAccountService;
import com.digithink.business_management.service.general_ledger.GeneralLedgerSetupService;
import com.digithink.business_management.service.inventory.LocationService;
import com.digithink.business_management.service.posting_group.GeneralBusinessPostingGroupService;
import com.digithink.business_management.service.posting_group.GeneralProductPostingGroupService;
import com.digithink.business_management.service.posting_group.InventoryPostingGroupService;
import com.digithink.business_management.service.posting_group.VatBusinessPostingGroupService;
import com.digithink.business_management.service.posting_group.VatProductPostingGroupService;
import com.digithink.business_management.service.posting_setup.GeneralPostingSetupService;
import com.digithink.business_management.service.posting_setup.InventoryPostingSetupService;
import com.digithink.business_management.service.posting_setup.VatPostingSetupService;
import com.digithink.business_management.service.setup.InventorySetupService;
import com.digithink.business_management.service.setup.PurchaseSetupService;
import com.digithink.business_management.service.setup.SalesSetupService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ZZDataInitializer {

	private UserAccountRepository userRepository;
	private PermissionRepository permissionRepository;
	private CompanyRepository companyRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private CurrencyService currencyService;
	private UnitOfMeasureService unitOfMeasureService;
	private ItemUnitOfMeasureService itemUnitOfMeasureService;
	private ItemDiscountGroupService discountGroupService;
	private InventoryPostingGroupService inventoryPostingGroupService;
	private VatProductPostingGroupService vatProductPostingGroupService;
	private VatBusinessPostingGroupService vatBusinessPostingGroupService;
	private GeneralProductPostingGroupService generalProductPostingGroupService;
	private GeneralBusinessPostingGroupService generalBusinessPostingGroupService;
	private GeneralLedgerAccountService generalLedgerAccountService;
	private GeneralPostingSetupService generalPostingSetupService;
	private VatPostingSetupService vatPostingSetupService;
	private InventoryPostingSetupService inventoryPostingSetupService;
	private LocationService locationService;
	private GeneralLedgerSetupService generalLedgerSetupService;
	private SeriesHeaderService seriesHeaderService;
	private SeriesLineService seriesLineService;
	private InventorySetupService inventorySetupService;
	private SalesSetupService salesSetupService;
	private PurchaseSetupService purchaseSetupService;

	@PostConstruct
	public void init() {
		if (companyRepository.count() == 0) {
			initSysAdminUser(initSysAdminRole());
			insertDefaultCompanies();
			insertDefaultCurrencies();
			insertDefaultUnitOfMeasures();
			insertDefaultItemUnitOfMeasures();
			insertDefaultItemDiscountGroups();
			insertDefaultInventoryPostingGroups();
			insertDefaultVatBusinessPostingGroups();
			insertDefaultVatProductPostingGroups();
			insertDefaultGeneralProductPostingGroups();
			insertDefaultGeneralBusinessPostingGroups();
			insertDefaultGeneralLedgerAccounts();
			insertDefaultGeneralPostingSetups();
			insertDefaultVatPostingSetups();
			insertDefaultInventoryPostingSetups();
			insertDefaultLocations();
			insertDefaultGeneralLedgerSetups();
			initSeriesHeaderAndSeriesLine();
			initInventorySetup();
			initSalesSetup();
			initPurchaseSetup();
		}
	}

	private void initPurchaseSetup() {
		PurchaseSetup purchaseSetup = new PurchaseSetup();
		purchaseSetup.setMandatoryExtDocNo(true);
		purchaseSetup.setCalculateInvoiceDiscount(true);
		purchaseSetup.setAllowVATDifference(true);
		purchaseSetup.setDefaultAccountingDate(DefaultAccountingDate.No_Date);
		purchaseSetup.setDefaultQuantityToReceive(DefaultQuantityToReceive.Remainder);
		purchaseSetup.setArchiveCurrencies(true);
		purchaseSetup.setArchiveOrders(true);
		purchaseSetup.setArchiveOpenOrders(true);
		purchaseSetup.setArchiveReturns(true);
//		purchaseSetup.setVendorNo("vendorNo");
//		purchaseSetup.setRequestQuoteNo("requestQuoteNo");
//		purchaseSetup.setOrderNo("orderNo");
//		purchaseSetup.setInvoiceNo("invoiceNo");
//		purchaseSetup.setRegisteredInvoiceNo("registeredInvoiceNo");
//		purchaseSetup.setCreditMemoNo("creditMemoNo");
//		purchaseSetup.setRegisteredCreditMemoNo("registeredCreditMemoNo");
//		purchaseSetup.setRegisteredReceiptNo("registeredReceiptNo");
//		purchaseSetup.setOpenOrderNo("openOrderNo");
//		purchaseSetup.setReturnNo("returnNo");
//		purchaseSetup.setRegisteredReturnShipmentNo("registeredReturnShipmentNo");
		purchaseSetupService.save(purchaseSetup);
	}

	private void initSalesSetup() {
		SalesSetup salesSetup = new SalesSetup();
		salesSetup.setCreditAlert(true);
		salesSetup.setOutOfStockAlert(true);
		salesSetup.setMandatoryExtDocNo(true);
		salesSetup.setCalculateInvoiceDiscount(true);
		salesSetup.setAllowVATDifference(true);
		salesSetup.setDefaultAccountingDate(DefaultAccountingDate.No_Date);
		salesSetup.setDefaultQuantityToShip(DefaultQuantityToShip.Balance);
		salesSetup.setArchiveCurrencies(true);
		salesSetup.setArchiveOrders(true);
		salesSetup.setArchiveOpenOrders(true);
		salesSetup.setArchiveReturns(true);
//		salesSetup.setCustomerNo("customerNo");
//		salesSetup.setCurrencyNo("currencyNo");
//		salesSetup.setOrderNo("orderNo");
//		salesSetup.setInvoiceNo("invoiceNo");
//		salesSetup.setRegisteredInvoiceNo("registeredInvoiceNo");
//		salesSetup.setCreditMemoNo("creditMemoNo");
//		salesSetup.setRegistredCreditMemoNo("registredCreditMemoNo");
//		salesSetup.setRegisteredShipmentNo("registeredShipmentNo");
//		salesSetup.setOpenOrderNo("openOrderNo");
//		salesSetup.setReturnNo("returnNo");
//		salesSetup.setRegisteredReturnReceiptNo("registeredReturnReceiptNo");
		salesSetupService.save(salesSetup);
	}

	private void initInventorySetup() {
		InventorySetup inventorySetup = new InventorySetup();
		inventorySetup.setIsLocationMondatory(true);
		inventorySetup.setTemNo("temNo");
		inventorySetup.setAutomaticCostAdjustment(AutomaticCostAdjustment.Month);
		inventorySetup.setAvoidNegativeStock(true);
//		inventorySetup.setTransferOrderNo("transferOrderNo");
//		inventorySetup.setRegisteredTransferShipmentNo("registeredTransferShipmentNo");
//		inventorySetup.setRegisteredTransferReceiptNo("registeredTransferReceiptNo");
//		inventorySetup.setInventoryNo("inventoryNo");
		inventorySetup.setDefaultStockEvaluationMode(StockEvaluationMode.FIFO);
		inventorySetup.setAverageCostPeriod(AverageCostPeriod.Month);
		inventorySetup.setItemNoGenerator(true);
		inventorySetup.setNumberOfDaysABCClassification(30);
		inventorySetup.setNumberOfDaysCBNCalculation(30);
		inventorySetup.setPercentClassA(70.0);
		inventorySetup.setPercentClassB(20.0);
		inventorySetup.setPercentClassC(10.0);
		inventorySetup.setPercentClassX(50.0);
		inventorySetup.setPercentClassY(30.0);
		inventorySetup.setPercentClassZ(20.0);
		inventorySetup.setMinimumCoverageClassAX(1.0);
		inventorySetup.setMaximumCoverageClassAX(5.0);
		inventorySetup.setMinimumCoverageClassBX(1.0);
		inventorySetup.setMaximumCoverageClassBX(5.0);
		inventorySetup.setMinimumCoverageClassCX(1.0);
		inventorySetup.setMaximumCoverageClassCX(5.0);
		inventorySetup.setMinimumCoverageClassAY(1.0);
		inventorySetup.setMaximumCoverageClassAY(5.0);
		inventorySetup.setMinimumCoverageClassBY(1.0);
		inventorySetup.setMaximumCoverageClassBY(5.0);
		inventorySetup.setMinimumCoverageClassCY(1.0);
		inventorySetup.setMaximumCoverageClassCY(5.0);
		inventorySetup.setMinimumCoverageClassAZ(1.0);
		inventorySetup.setMaximumCoverageClassAZ(5.0);
		inventorySetup.setMinimumCoverageClassBZ(1.0);
		inventorySetup.setMaximumCoverageClassBZ(5.0);
		inventorySetup.setMinimumCoverageClassCZ(1.0);
		inventorySetup.setMaximumCoverageClassCZ(5.0);
		inventorySetupService.save(inventorySetup);
	}

	private void initSeriesHeaderAndSeriesLine() {
		initSeriesHeaderWithLines("CLT", "Customer Number Series");
		initSeriesHeaderWithLines("CURR", "Currency Number Series");
		initSeriesHeaderWithLines("COMMANDE", "Order Number Series");
		initSeriesHeaderWithLines("INVOICE", "Invoice Number Series");
		initSeriesHeaderWithLines("REG-INV", "Registered Invoice Number Series");
		initSeriesHeaderWithLines("CRMM", "Credit Memo Number Series");
		initSeriesHeaderWithLines("REG-CRMM", "Registered Credit Memo Number Series");
		initSeriesHeaderWithLines("REG-SHPMT", "Registered Shipment Number Series");
		initSeriesHeaderWithLines("OP-COMMANDE", "Open Order Number Series");
		initSeriesHeaderWithLines("RETURN", "Return Number Series");
		initSeriesHeaderWithLines("REG-RET-REC", "Registered Return Receipt Number Series");
		initSeriesHeaderWithLines("TRANSH", "Transfer Order Number Series");
		initSeriesHeaderWithLines("MPOFF", "Registered Transfer Shipment Number Series");
		initSeriesHeaderWithLines("DFVVV", "Registered Transfer Receipt Number Series");
		initSeriesHeaderWithLines("INV", "Inventory Number Series");
		initSeriesHeaderWithLines("FRS", "Vendor Number Series");
		initSeriesHeaderWithLines("QUOT", "Request Quote Number Series");
		initSeriesHeaderWithLines("REG-RECC", "Registered Receipt Number Series");
		initSeriesHeaderWithLines("REG-RETR", "Registered Return Shipment Number Series");
	}

	private void initSeriesHeaderWithLines(String no, String description) {
		SeriesHeader seriesHeader = new SeriesHeader();
		seriesHeader.setNo(no);
		seriesHeader.setDescription(description);
		seriesHeader.setDefaultNo(true);
		seriesHeader.setManualNo(false);
		seriesHeader.setChronologicalOrder(true);
		seriesHeader.setType(SeriesHeaderType.Annual);
		seriesHeaderService.save(seriesHeader);

		SeriesLine seriesLine = new SeriesLine();
		seriesLine.setDocumentNo(no);
		seriesLine.setStartDate(LocalDate.now());
		seriesLine.setStartNo(no + "001");
		seriesLine.setEndNo(no + "999");
		seriesLine.setLastNoUsed(null);
		seriesLine.setLastDateUsed(null);
		seriesLineService.save(seriesLine);
	}

	private void insertDefaultGeneralLedgerSetups() {
		GeneralLedgerSetup defaultSetup1 = new GeneralLedgerSetup();
		defaultSetup1.setAllowPostingFrom(LocalDate.of(2024, 1, 1));
		defaultSetup1.setAllowPostingTo(LocalDate.of(2024, 12, 31));
		defaultSetup1.setInventoryRoundingPrecision(0.01);
		defaultSetup1.setStampDutyAmount(5.0);
		defaultSetup1.setLCYNo("TND");
		defaultSetup1.setSalesStampDutyAccount("SALES_STAMP");
		defaultSetup1.setPurchaseStampDutyAccount("PURCHASE_STAMP");

		generalLedgerSetupService.save(defaultSetup1);
	}

	private void insertDefaultLocations() {
		Location defaultLocation1 = new Location();
		defaultLocation1.setNo("LOC001");
		defaultLocation1.setName("Main Warehouse");
		defaultLocation1.setAddress("123 Main Street");
		defaultLocation1.setCity("Default City");
		defaultLocation1.setPhoneNo("123-456-7890");
		defaultLocation1.setContact("John Doe");
		defaultLocation1.setPostalCode("12345");
		defaultLocation1.setEmail("mainwarehouse@company.com");
		defaultLocation1.setHomePage("http://mainwarehouse.company.com");
		defaultLocation1.setUseAsInTransit(false);

		Location defaultLocation2 = new Location();
		defaultLocation2.setNo("LOC002");
		defaultLocation2.setName("Secondary Warehouse");
		defaultLocation2.setAddress("456 Secondary Street");
		defaultLocation2.setCity("Default City");
		defaultLocation2.setPhoneNo("098-765-4321");
		defaultLocation2.setContact("Jane Doe");
		defaultLocation2.setPostalCode("54321");
		defaultLocation2.setEmail("secondarywarehouse@company.com");
		defaultLocation2.setHomePage("http://secondarywarehouse.company.com");
		defaultLocation2.setUseAsInTransit(true);

		locationService.save(defaultLocation1);
		locationService.save(defaultLocation2);
	}

	private void insertDefaultInventoryPostingSetups() {
		InventoryPostingSetup defaultInventorySetup1 = new InventoryPostingSetup();
		defaultInventorySetup1.setLocationCode("LOC001");
		defaultInventorySetup1.setInvtPostingGroupCode("INVT_GRP_DEF");
		defaultInventorySetup1.setInventoryAccount("INV_ACC_DEFAULT");

		InventoryPostingSetup defaultInventorySetup2 = new InventoryPostingSetup();
		defaultInventorySetup2.setLocationCode("LOC_SECONDARY");
		defaultInventorySetup2.setInvtPostingGroupCode("INVT_GRP_SEC");
		defaultInventorySetup2.setInventoryAccount("INV_ACC_SEC");

		inventoryPostingSetupService.save(defaultInventorySetup1);
		inventoryPostingSetupService.save(defaultInventorySetup2);
	}

	private void insertDefaultVatPostingSetups() {
		VatPostingSetup defaultVatSetup1 = new VatPostingSetup();
		defaultVatSetup1.setVat(15.0);
		defaultVatSetup1.setVatBusPostingGroup("VAT_BUS_GRP_001");
		defaultVatSetup1.setVatProdPostingGroup("INV_GRP_001");
		defaultVatSetup1.setSalesVatAccount("SLS_VAT_ACC");
		defaultVatSetup1.setPurchaseVatAccount("PRCH_VAT_ACC");

		VatPostingSetup defaultVatSetup2 = new VatPostingSetup();
		defaultVatSetup2.setVat(20.0);
		defaultVatSetup2.setVatBusPostingGroup("VAT_BUS_GRP_002");
		defaultVatSetup2.setVatProdPostingGroup("INV_GRP_002");
		defaultVatSetup2.setSalesVatAccount("SLS_VAT_ACC2");
		defaultVatSetup2.setPurchaseVatAccount("PRCH_VAT_ACC2");

		vatPostingSetupService.save(defaultVatSetup1);
		vatPostingSetupService.save(defaultVatSetup2);
	}

	private void insertDefaultGeneralPostingSetups() {
		GeneralPostingSetup defaultSetup1 = new GeneralPostingSetup();
		defaultSetup1.setGenBusPostingGroup("VAT_BUS_GRP_001");
		defaultSetup1.setGenProdPostingGroup("VAT_PRD_GRP_001");
		defaultSetup1.setSalesAccount("SALES_ACC");
		defaultSetup1.setPurchaseAccount("PURCHASE_ACC");
		defaultSetup1.setSalesCreditMemoAccount("SLS_CRDT_MM_ACC");
		defaultSetup1.setPurchaseCreditMemoAccount("PRC_CRD_MM_ACC");

		GeneralPostingSetup defaultSetup2 = new GeneralPostingSetup();
		defaultSetup2.setGenBusPostingGroup("VAT_BUS_GRP_002");
		defaultSetup2.setGenProdPostingGroup("VAT_PRD_GRP_002");
		defaultSetup2.setSalesAccount("SALES_ACC");
		defaultSetup2.setPurchaseAccount("PURCHASE_ACC");
		defaultSetup2.setSalesCreditMemoAccount("SLS_CRDT_MM_ACC");
		defaultSetup2.setPurchaseCreditMemoAccount("PRC_CRD_MM_ACC");

		generalPostingSetupService.save(defaultSetup1);
		generalPostingSetupService.save(defaultSetup2);
	}

	private void insertDefaultGeneralLedgerAccounts() {
		GeneralLedgerAccount salesStampDutyAccount = new GeneralLedgerAccount();
		salesStampDutyAccount.setNo("SLS_STMP_DT_ACC");
		salesStampDutyAccount.setName("Sales Stamp Duty Account");
		salesStampDutyAccount.setAccountType(GLAccountType.Imputable);
		salesStampDutyAccount.setAccountCategory(GLAccountCategory.Revenue);
		salesStampDutyAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		salesStampDutyAccount.setDebitOrCredit(GLDebitOrCredit.Credit);
		salesStampDutyAccount.setBlocked(false);
		salesStampDutyAccount.setDirectImputation(true);

		GeneralLedgerAccount purchaseStampDutyAccount = new GeneralLedgerAccount();
		purchaseStampDutyAccount.setNo("PRCH_STMP_DT_ACC");
		purchaseStampDutyAccount.setName("Purchase Stamp Duty Account");
		purchaseStampDutyAccount.setAccountType(GLAccountType.Imputable);
		purchaseStampDutyAccount.setAccountCategory(GLAccountCategory.Expenses);
		purchaseStampDutyAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		purchaseStampDutyAccount.setDebitOrCredit(GLDebitOrCredit.Debit);
		purchaseStampDutyAccount.setBlocked(false);
		purchaseStampDutyAccount.setDirectImputation(true);

		GeneralLedgerAccount salesAccount = new GeneralLedgerAccount();
		salesAccount.setNo("SALES_ACC");
		salesAccount.setName("Sales Account");
		salesAccount.setAccountType(GLAccountType.Imputable);
		salesAccount.setAccountCategory(GLAccountCategory.Revenue);
		salesAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		salesAccount.setDebitOrCredit(GLDebitOrCredit.Credit);
		salesAccount.setBlocked(false);
		salesAccount.setDirectImputation(true);

		GeneralLedgerAccount purchaseAccount = new GeneralLedgerAccount();
		purchaseAccount.setNo("PURCHASE_ACC");
		purchaseAccount.setName("Purchase Account");
		purchaseAccount.setAccountType(GLAccountType.Imputable);
		purchaseAccount.setAccountCategory(GLAccountCategory.Expenses);
		purchaseAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		purchaseAccount.setDebitOrCredit(GLDebitOrCredit.Debit);
		purchaseAccount.setBlocked(false);
		purchaseAccount.setDirectImputation(true);

		GeneralLedgerAccount salesCreditMemoAccount = new GeneralLedgerAccount();
		salesCreditMemoAccount.setNo("SLS_CRDT_MM_ACC");
		salesCreditMemoAccount.setName("Sales Credit Memo Account");
		salesCreditMemoAccount.setAccountType(GLAccountType.Imputable);
		salesCreditMemoAccount.setAccountCategory(GLAccountCategory.Revenue);
		salesCreditMemoAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		salesCreditMemoAccount.setDebitOrCredit(GLDebitOrCredit.Credit);
		salesCreditMemoAccount.setBlocked(false);
		salesCreditMemoAccount.setDirectImputation(true);

		GeneralLedgerAccount purchaseCreditMemoAccount = new GeneralLedgerAccount();
		purchaseCreditMemoAccount.setNo("PRC_CRD_MM_ACC");
		purchaseCreditMemoAccount.setName("Purchase Credit Memo Account");
		purchaseCreditMemoAccount.setAccountType(GLAccountType.Imputable);
		purchaseCreditMemoAccount.setAccountCategory(GLAccountCategory.Expenses);
		purchaseCreditMemoAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		purchaseCreditMemoAccount.setDebitOrCredit(GLDebitOrCredit.Debit);
		purchaseCreditMemoAccount.setBlocked(false);
		purchaseCreditMemoAccount.setDirectImputation(true);

		GeneralLedgerAccount inventoryAccount = new GeneralLedgerAccount();
		inventoryAccount.setNo("INV_ACC");
		inventoryAccount.setName("Inventory Account");
		inventoryAccount.setAccountType(GLAccountType.Imputable);
		inventoryAccount.setAccountCategory(GLAccountCategory.Assets);
		inventoryAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		inventoryAccount.setDebitOrCredit(GLDebitOrCredit.Debit);
		inventoryAccount.setBlocked(false);
		inventoryAccount.setDirectImputation(true);

		GeneralLedgerAccount purchaseVatAccount = new GeneralLedgerAccount();
		purchaseVatAccount.setNo("PRCH_VAT_ACC");
		purchaseVatAccount.setName("Purchase VAT Account");
		purchaseVatAccount.setAccountType(GLAccountType.Imputable);
		purchaseVatAccount.setAccountCategory(GLAccountCategory.Expenses);
		purchaseVatAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		purchaseVatAccount.setDebitOrCredit(GLDebitOrCredit.Debit);
		purchaseVatAccount.setBlocked(false);
		purchaseVatAccount.setDirectImputation(true);

		GeneralLedgerAccount salesVatAccount = new GeneralLedgerAccount();
		salesVatAccount.setNo("SLS_VAT_ACC");
		salesVatAccount.setName("Sales VAT Account");
		salesVatAccount.setAccountType(GLAccountType.Imputable);
		salesVatAccount.setAccountCategory(GLAccountCategory.Revenue);
		salesVatAccount.setManagementOrBalance(GLManagementOrBalance.Management);
		salesVatAccount.setDebitOrCredit(GLDebitOrCredit.Credit);
		salesVatAccount.setBlocked(false);
		salesVatAccount.setDirectImputation(true);

		generalLedgerAccountService.save(salesStampDutyAccount);
		generalLedgerAccountService.save(purchaseStampDutyAccount);
		generalLedgerAccountService.save(salesAccount);
		generalLedgerAccountService.save(purchaseAccount);
		generalLedgerAccountService.save(salesCreditMemoAccount);
		generalLedgerAccountService.save(purchaseCreditMemoAccount);
		generalLedgerAccountService.save(inventoryAccount);
		generalLedgerAccountService.save(purchaseVatAccount);
		generalLedgerAccountService.save(salesVatAccount);
	}

	private void insertDefaultGeneralBusinessPostingGroups() {
		GeneralBusinessPostingGroup group1 = new GeneralBusinessPostingGroup();
		group1.setNo("GEN_BUS_GRP_001");
		group1.setDescription("General Business Posting Group 1");
		group1.setDefVatBusPostingGroup("VAT_PRD_GRP_001");

		GeneralBusinessPostingGroup group2 = new GeneralBusinessPostingGroup();
		group2.setNo("GEN_BUS_GRP_002");
		group2.setDescription("General Business Posting Group 2");
		group2.setDefVatBusPostingGroup("VAT_PRD_GRP_002");

		generalBusinessPostingGroupService.save(group1);
		generalBusinessPostingGroupService.save(group2);
	}

	private void insertDefaultGeneralProductPostingGroups() {
		GeneralProductPostingGroup group1 = new GeneralProductPostingGroup();
		group1.setNo("GEN_PRD_GRP_001");
		group1.setDescription("General Product Posting Group 1");
		group1.setDefVatProdPostingGroup("VAT_BUS_GRP_001");

		GeneralProductPostingGroup group2 = new GeneralProductPostingGroup();
		group2.setNo("GEN_PRD_GRP_002");
		group2.setDescription("General Product Posting Group 2");
		group2.setDefVatProdPostingGroup("VAT_BUS_GRP_002");

		generalProductPostingGroupService.save(group1);
		generalProductPostingGroupService.save(group2);
	}

	private void insertDefaultVatProductPostingGroups() {
		VatProductPostingGroup group1 = new VatProductPostingGroup();
		group1.setNo("VAT_PRD_GRP_001");
		group1.setDescription("VAT Product Posting Group 1");

		VatProductPostingGroup group2 = new VatProductPostingGroup();
		group2.setNo("VAT_PRD_GRP_002");
		group2.setDescription("VAT Product Posting Group 2");

		vatProductPostingGroupService.save(group1);
		vatProductPostingGroupService.save(group2);
	}

	private void insertDefaultVatBusinessPostingGroups() {
		VatBusinessPostingGroup group1 = new VatBusinessPostingGroup();
		group1.setNo("VAT_BUS_GRP_001");
		group1.setDescription("VAT Business Posting Group 1");

		VatBusinessPostingGroup group2 = new VatBusinessPostingGroup();
		group2.setNo("VAT_BUS_GRP_002");
		group2.setDescription("VAT Business Posting Group 2");

		vatBusinessPostingGroupService.save(group1);
		vatBusinessPostingGroupService.save(group2);
	}

	private void insertDefaultInventoryPostingGroups() {
		InventoryPostingGroup group1 = new InventoryPostingGroup();
		group1.setNo("INV_GRP_001");
		group1.setDescription("Inventory Posting Group 1");

		InventoryPostingGroup group2 = new InventoryPostingGroup();
		group2.setNo("INV_GRP_002");
		group2.setDescription("Inventory Posting Group 2");

		inventoryPostingGroupService.save(group1);
		inventoryPostingGroupService.save(group2);
	}

	private void insertDefaultItemDiscountGroups() {
		ItemDiscountGroup discountGroup1 = new ItemDiscountGroup();
		discountGroup1.setNo("DISCNT_GRP_001");
		discountGroup1.setDescription("Item Discount Group 1");

		ItemDiscountGroup discountGroup2 = new ItemDiscountGroup();
		discountGroup2.setNo("DISCNT_GRP_002");
		discountGroup2.setDescription("Item Discount Group 2");

		discountGroupService.save(discountGroup1);
		discountGroupService.save(discountGroup2);
	}

	private void insertDefaultUnitOfMeasures() {
		UnitOfMeasure unitOfMeasure1 = new UnitOfMeasure();
		unitOfMeasure1.setNo("UOM_001");
		unitOfMeasure1.setDescription("Unit of Measure 1");

		UnitOfMeasure unitOfMeasure2 = new UnitOfMeasure();
		unitOfMeasure2.setNo("UOM_002");
		unitOfMeasure2.setDescription("Unit of Measure 2");

		unitOfMeasureService.save(unitOfMeasure1);
		unitOfMeasureService.save(unitOfMeasure2);
	}

	private void insertDefaultItemUnitOfMeasures() {
		ItemUnitOfMeasure itemUnitOfMeasure1 = new ItemUnitOfMeasure();
		itemUnitOfMeasure1.setNo("ITEM_UOM_001");
		itemUnitOfMeasure1.setDescription("Item Unit of Measure 1");
		itemUnitOfMeasure1.setQtyPerUnitOfMeasure(1.0);

		ItemUnitOfMeasure itemUnitOfMeasure2 = new ItemUnitOfMeasure();
		itemUnitOfMeasure2.setNo("ITEM_UOM_002");
		itemUnitOfMeasure2.setDescription("Item Unit of Measure 2");
		itemUnitOfMeasure2.setQtyPerUnitOfMeasure(2.0);

		itemUnitOfMeasureService.save(itemUnitOfMeasure1);
		itemUnitOfMeasureService.save(itemUnitOfMeasure2);
	}

	private void insertDefaultCurrencies() {
		Currency defaultCurrency = new Currency();
		defaultCurrency.setNo("USD");
		defaultCurrency.setDescription("United States Dollar");

		Currency anotherCurrency = new Currency();
		anotherCurrency.setNo("EUR");
		anotherCurrency.setDescription("Euro");

		Currency anotherCurrency2 = new Currency();
		anotherCurrency2.setNo("TND");
		anotherCurrency2.setDescription("Dinar Tunisien");

		currencyService.save(defaultCurrency);
		currencyService.save(anotherCurrency);
		currencyService.save(anotherCurrency2);
	}

	private void insertDefaultCompanies() {
		Company defaultCompany = new Company();
		defaultCompany.setName("Default Company");
		defaultCompany.setName2("DC");
		defaultCompany.setAddress("123 Default Street");
		defaultCompany.setAddress2("Suite 100");
		defaultCompany.setEmail("contact@defaultcompany.com");
		defaultCompany.setEmail2("support@defaultcompany.com");
		defaultCompany.setPostalCode("12345");
		defaultCompany.setCity("Default City");
		defaultCompany.setPhone("123-456-7890");
		defaultCompany.setPhone2("098-765-4321");
		defaultCompany.setFaxNumber("123-456-7899");
		defaultCompany.setTaxIdentificationNumber("TID123456789");
		defaultCompany.setCommercialRegister("CR123456789");
		defaultCompany.setLegalStatus("Active");
		defaultCompany.setCapital(1000000.0);
		defaultCompany.setManager("John Doe");
		defaultCompany.setImage("default_image.png");
		defaultCompany.setVisa("VISA123456");

		companyRepository.save(defaultCompany);
		UserAccount userAccount = new UserAccount();
		userAccount.setUsername("sys_admin");
		userAccount.setCompany(defaultCompany.getId()); // Assuming you have a setter for company
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAccount,
				null, null);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	private Role initSysAdminRole() {
		Role role = new Role();
		role.setName("SysAdmin");
		role.setDescription("System Administrator");
		for (PermissionPage iterable_element : Arrays.stream(PermissionPage.values()).collect(Collectors.toList())) {
			for (PermissionAction _iterable_element : Arrays.stream(PermissionAction.values())
					.collect(Collectors.toList())) {
				Permission permission = new Permission();
				permission.setPage(iterable_element);
				permission.setAction(_iterable_element);
				permissionRepository.save(permission);
			}
		}
		role.setPermissions(new HashSet<Permission>(permissionRepository.findAll()));
		return roleRepository.save(role);
	}

	private void initSysAdminUser(Role role) {
		UserAccount user = new UserAccount();
		user.setUsername("sys_admin");
		user.setPassword(passwordEncoder.encode("P@ssw0rd"));
		user.setEmail("sys_admin@gmail.com");
		user.setActive(true);
		Set<Role> sysAdminRoles = new HashSet<>();
		sysAdminRoles.add(role);
		user.setRoles(sysAdminRoles);
		userRepository.save(user);
	}
}
