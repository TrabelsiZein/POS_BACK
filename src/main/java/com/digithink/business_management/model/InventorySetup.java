package com.digithink.business_management.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.business_management.model.enumeration.AutomaticCostAdjustment;
import com.digithink.business_management.model.enumeration.AverageCostPeriod;
import com.digithink.business_management.model.enumeration.StockEvaluationMode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class InventorySetup extends _BaseEntity {

	private Boolean isLocationMondatory;
	// SeriesHeader Entity
	@Column(length = 20)
	private String temNo;
	private AutomaticCostAdjustment automaticCostAdjustment;
	private Boolean avoidNegativeStock;
	// SeriesHeader Entity
	@Column(length = 20)
	private String transferOrderNo;
	// SeriesHeader Transfer Entity
	@Column(length = 20)
	private String registeredTransferShipmentNo;
	// SeriesHeader Reception Entity
	@Column(length = 20)
	private String registeredTransferReceiptNo;
	// SeriesHeader Entity
	@Column(length = 20)
	private String inventoryNo;
	private StockEvaluationMode defaultStockEvaluationMode;
	private AverageCostPeriod averageCostPeriod;
	private Boolean itemNoGenerator;
	private Integer numberOfDaysABCClassification;
	private Integer numberOfDaysCBNCalculation;
	private Double percentClassA;
	private Double percentClassB;
	private Double percentClassC;
	private Double percentClassX;
	private Double percentClassY;
	private Double percentClassZ;
	private Double minimumCoverageClassAX;
	private Double maximumCoverageClassAX;
	private Double minimumCoverageClassBX;
	private Double maximumCoverageClassBX;
	private Double minimumCoverageClassCX;
	private Double maximumCoverageClassCX;
	private Double minimumCoverageClassAY;
	private Double maximumCoverageClassAY;
	private Double minimumCoverageClassBY;
	private Double maximumCoverageClassBY;
	private Double minimumCoverageClassCY;
	private Double maximumCoverageClassCY;
	private Double minimumCoverageClassAZ;
	private Double maximumCoverageClassAZ;
	private Double minimumCoverageClassBZ;
	private Double maximumCoverageClassBZ;
	private Double minimumCoverageClassCZ;
	private Double maximumCoverageClassCZ;

}
