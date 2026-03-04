package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.InvoiceHeader;
import com.digithink.pos.model.InvoiceLine;

public interface InvoiceLineRepository extends _BaseRepository<InvoiceLine, Long> {

	List<InvoiceLine> findByInvoice(InvoiceHeader invoice);
}

