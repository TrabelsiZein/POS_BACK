package com.digithink.pos.dto;

import java.time.LocalDate;

import com.digithink.pos.model.enumeration.InvoiceLineGroupingMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for GET /admin/invoices/{id}/details to avoid Jackson circular reference
 * when serializing entities (InvoiceHeader, InvoiceLine, SalesHeader).
 */
public class InvoiceDetailsDTO {

	/** Invoice header for details view (no lines collection). */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InvoiceHeaderDetail {
		private Long id;
		private String invoiceNumber;
		private LocalDate invoiceDate;
		private CustomerSummary customer;
		private UserSummary createdByUser;
		private InvoiceLineGroupingMode lineGroupingMode;
		private Double subtotal;
		private Double taxAmount;
		private Double discountAmount;
		private Double totalAmount;
		private String notes;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerSummary {
		private Long id;
		private String name;
		private String customerCode;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserSummary {
		private Long id;
		private String username;
		private String fullName;
	}

	/** One invoice line for details/print (no back-reference to invoice). */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InvoiceLineDetail {
		private Long id;
		private ItemSummary item;
		private ItemFamilySummary itemFamily;
		private ItemSubFamilySummary itemSubFamily;
		private String lineDescription;
		private Integer quantity;
		private Double unitPrice;
		private Double subtotal;
		private Double taxAmount;
		private Double totalAmount;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemSummary {
		private Long id;
		private String name;
		private String itemCode;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemFamilySummary {
		private Long id;
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemSubFamilySummary {
		private Long id;
		private String name;
	}

	/** Ticket summary for details/print (no invoice back-reference). */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TicketSummary {
		private Long id;
		private String salesNumber;
		private java.time.LocalDateTime salesDate;
		private Double totalAmount;
	}
}
