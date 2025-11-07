package com.digithink.pos.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * DTO for processing a complete sale transaction
 */
@Data
public class ProcessSaleRequestDTO {
	
	private Double subtotal;
	private Double taxAmount;
	private Double discountAmount;
	private Double totalAmount;
	private Double paidAmount;
	private Double changeAmount;
	private Long customerId;
	private String notes;
	
	private List<SaleLineDTO> lines;
	private List<PaymentDTO> payments;
	
	@Data
	public static class SaleLineDTO {
		private Long itemId;
		private Integer quantity;
		private Double unitPrice;
		private Double lineTotal;
	}
	
	@Data
	public static class PaymentDTO {
		private Long paymentMethodId;
		private Double amount;
		private String reference;
		private String notes;
		private String titleNumber;
		@JsonFormat(pattern = "yyyy-MM-dd")
		private LocalDate dueDate;
		private String drawerName;
		private String issuingBank;
	}
}

