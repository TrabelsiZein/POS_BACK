package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.PurchaseInvoiceHeader;
import com.digithink.pos.model.PurchaseInvoiceLine;

public interface PurchaseInvoiceLineRepository extends _BaseRepository<PurchaseInvoiceLine, Long> {

	List<PurchaseInvoiceLine> findByPurchaseInvoice(PurchaseInvoiceHeader purchaseInvoice);
}
