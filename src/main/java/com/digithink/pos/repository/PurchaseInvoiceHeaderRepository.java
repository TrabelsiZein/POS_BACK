package com.digithink.pos.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.digithink.pos.model.PurchaseInvoiceHeader;
import com.digithink.pos.model.Vendor;

public interface PurchaseInvoiceHeaderRepository extends _BaseRepository<PurchaseInvoiceHeader, Long> {

	Page<PurchaseInvoiceHeader> findByVendorAndInvoiceDateBetween(Vendor vendor, LocalDate from, LocalDate to,
			Pageable pageable);

	Page<PurchaseInvoiceHeader> findByInvoiceDateBetween(LocalDate from, LocalDate to, Pageable pageable);

	Page<PurchaseInvoiceHeader> findByVendor(Vendor vendor, Pageable pageable);
}
