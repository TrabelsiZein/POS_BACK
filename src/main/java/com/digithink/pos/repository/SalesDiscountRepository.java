package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.SalesDiscount;

public interface SalesDiscountRepository extends _BaseRepository<SalesDiscount, Long> {

	Optional<SalesDiscount> findByErpExternalId(String erpExternalId);

	Optional<SalesDiscount> findByTypeAndCodeAndSalesTypeAndSalesCodeAndResponsibilityCenterAndStartingDateAndAuxiliaryIndex1AndAuxiliaryIndex2AndAuxiliaryIndex3AndAuxiliaryIndex4(
			String type, String code, String salesType, String salesCode, String responsibilityCenter,
			String startingDate, String auxiliaryIndex1, String auxiliaryIndex2, String auxiliaryIndex3,
			Integer auxiliaryIndex4);
}

