package com.digithink.pos.erp.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpTimestamped;
import com.digithink.pos.erp.enumeration.ErpSyncJobType;
import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.service.GeneralSetupService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpSyncCheckpointService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpSyncCheckpointService.class);
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	private static final Map<ErpSyncJobType, String> JOB_TYPE_TO_CODE;
	private static final Map<String, String> CODE_TO_DESCRIPTION;

	static {
		Map<ErpSyncJobType, String> jobToCode = new EnumMap<>(ErpSyncJobType.class);
		jobToCode.put(ErpSyncJobType.IMPORT_ITEM_FAMILIES, "ERP_SYNC_LAST_ITEM_FAMILY");
		jobToCode.put(ErpSyncJobType.IMPORT_ITEM_SUBFAMILIES, "ERP_SYNC_LAST_ITEM_SUBFAMILY");
		jobToCode.put(ErpSyncJobType.IMPORT_ITEMS, "ERP_SYNC_LAST_ITEM");
		jobToCode.put(ErpSyncJobType.IMPORT_ITEM_BARCODES, "ERP_SYNC_LAST_ITEM_BARCODE");
		jobToCode.put(ErpSyncJobType.IMPORT_LOCATIONS, "ERP_SYNC_LAST_LOCATION");
		jobToCode.put(ErpSyncJobType.IMPORT_CUSTOMERS, "ERP_SYNC_LAST_CUSTOMER");
		JOB_TYPE_TO_CODE = Collections.unmodifiableMap(jobToCode);

		Map<String, String> descriptions = new HashMap<>();
		descriptions.put("ERP_SYNC_LAST_ITEM_FAMILY", "Timestamp (Modified_At) of last synchronized item family");
		descriptions.put("ERP_SYNC_LAST_ITEM_SUBFAMILY", "Timestamp (Modified_At) of last synchronized item subfamily");
		descriptions.put("ERP_SYNC_LAST_ITEM", "Timestamp (Modified_At) of last synchronized item");
		descriptions.put("ERP_SYNC_LAST_ITEM_BARCODE", "Timestamp (Modified_At) of last synchronized item barcode");
		descriptions.put("ERP_SYNC_LAST_LOCATION", "Timestamp (Modified_At) of last synchronized location");
		descriptions.put("ERP_SYNC_LAST_CUSTOMER", "Timestamp (Modified_At) of last synchronized customer");
		CODE_TO_DESCRIPTION = Collections.unmodifiableMap(descriptions);
	}

	private final GeneralSetupService generalSetupService;

	public ErpSyncFilter createFilterForJob(ErpSyncJobType jobType) {
		ErpSyncFilter filter = new ErpSyncFilter();
		resolveLastSync(jobType).ifPresent(filter::setUpdatedAfter);
		return filter;
	}

	public void updateLastSync(ErpSyncJobType jobType, Collection<?> payload) {
		if (payload == null || payload.isEmpty()) {
			return;
		}

		Optional<OffsetDateTime> maxTimestamp = payload.stream()
				.filter(Objects::nonNull)
				.filter(ErpTimestamped.class::isInstance)
				.map(ErpTimestamped.class::cast)
				.map(ErpTimestamped::getLastModifiedAt)
				.filter(Objects::nonNull)
				.max(Comparator.naturalOrder());

		maxTimestamp.ifPresent(ts -> updateLastSync(jobType, ts));
	}

	public void updateLastSync(ErpSyncJobType jobType, OffsetDateTime timestamp) {
		if (timestamp == null) {
			return;
		}
		String code = JOB_TYPE_TO_CODE.get(jobType);
		if (code == null) {
			return;
		}
		GeneralSetup setup = generalSetupService.findByCode(code);
		if (setup == null) {
			LOGGER.warn("Missing general setup entry for ERP sync checkpoint code {}", code);
			return;
		}
		generalSetupService.updateValue(code, FORMATTER.format(timestamp));
	}

	public Optional<OffsetDateTime> resolveLastSync(ErpSyncJobType jobType) {
		String code = JOB_TYPE_TO_CODE.get(jobType);
		if (code == null) {
			return Optional.empty();
		}
		String value = generalSetupService.findValueByCode(code);
		if (value == null || value.trim().isEmpty()) {
			return Optional.empty();
		}
		try {
			return Optional.of(OffsetDateTime.parse(value.trim()));
		} catch (DateTimeParseException ex) {
			LOGGER.warn("Invalid timestamp '{}' for ERP sync checkpoint '{}'", value, code);
			return Optional.empty();
		}
	}

	public static Map<ErpSyncJobType, String> getCheckpointCodes() {
		return JOB_TYPE_TO_CODE;
	}

	public static Map<String, String> getCheckpointDescriptions() {
		return CODE_TO_DESCRIPTION;
	}
}

