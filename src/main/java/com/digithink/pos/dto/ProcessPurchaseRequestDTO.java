package com.digithink.pos.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProcessPurchaseRequestDTO {

	private Long vendorId;

	private List<PurchaseLineDTO> lines;

	private String notes;

	@Data
	public static class PurchaseLineDTO {
		private Long itemId;
		private Integer quantity;
		private Double unitPrice;
	}
}
