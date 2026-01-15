package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.SalesPrice;

public interface SalesPriceRepository extends _BaseRepository<SalesPrice, Long> {

	Optional<SalesPrice> findByErpExternalId(String erpExternalId);

	Optional<SalesPrice> findByItemNoAndSalesTypeAndSalesCodeAndResponsibilityCenterAndStartingDateAndCurrencyCodeAndVariantCodeAndUnitOfMeasureCodeAndMinimumQuantity(
			String itemNo, String salesType, String salesCode, String responsibilityCenter, String startingDate,
			String currencyCode, String variantCode, String unitOfMeasureCode, Double minimumQuantity);
}

