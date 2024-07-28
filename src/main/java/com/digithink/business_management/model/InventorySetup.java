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
	// Item Entity
	@Column(length = 20)
	private String temNo;
	private AutomaticCostAdjustment automaticCostAdjustment;
	private Boolean avoidNegativeStock;
	// Transfer Order Entity
	@Column(length = 20)
	private String transferOrderNo;
	// Registred Transfer Entity
	@Column(length = 20)
	private String registeredTransferNo;
	// Registred Reception Entity
	@Column(length = 20)
	private String registeredReceptionferNo;
	// Inventory Entity
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
	private Double MinimumCoverageClassAX;
	private Double MaximumCoverageClassAX;
	private Double MinimumCoverageClassBX;
	private Double MaximumCoverageClassBX;
	private Double MinimumCoverageClassCX;
	private Double MaximumCoverageClassCX;
	private Double MinimumCoverageClassAY;
	private Double MaximumCoverageClassAY;
	private Double MinimumCoverageClassBY;
	private Double MaximumCoverageClassBY;
	private Double MinimumCoverageClassCY;
	private Double MaximumCoverageClassCY;
	private Double MinimumCoverageClassAZ;
	private Double MaximumCoverageClassAZ;
	private Double MinimumCoverageClassBZ;
	private Double MaximumCoverageClassBZ;
	private Double MinimumCoverageClassCZ;
	private Double MaximumCoverageClassCZ;

}
