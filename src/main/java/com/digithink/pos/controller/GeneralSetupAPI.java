package com.digithink.pos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.service.GeneralSetupService;
import com.digithink.pos.service.LocationService;
import com.digithink.pos.service.CustomerService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("general-setup")
@Log4j2
public class GeneralSetupAPI extends _BaseController<GeneralSetup, Long, GeneralSetupService> {

	@Autowired
	private LocationService locationService;

	@Autowired
	private CustomerService customerService;

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GeneralSetup updatedSetting) {
        try {
            Optional<GeneralSetup> existingOpt = service.findById(id);
            if (!existingOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Setting not found"));
            }

            GeneralSetup existing = existingOpt.get();

            if (Boolean.TRUE.equals(existing.getReadOnly())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("This setting is read only and cannot be modified."));
            }

            String oldValue = existing.getValeur();
            existing.setValeur(updatedSetting.getValeur());
            existing.setDescription(updatedSetting.getDescription());

            // Preserve readOnly flag and code
            GeneralSetup saved = service.save(existing);

            // If this is DEFAULT_LOCATION, update the Location entity
            if ("DEFAULT_LOCATION".equals(existing.getCode()) && !oldValue.equals(updatedSetting.getValeur())) {
                updateLocationDefaultFlag(updatedSetting.getValeur());
            }

            // If this is PASSENGER_CUSTOMER, update the Customer entity
            if ("PASSENGER_CUSTOMER".equals(existing.getCode()) && !oldValue.equals(updatedSetting.getValeur())) {
                updateCustomerDefaultFlag(updatedSetting.getValeur());
            }

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            String detailedMessage = getDetailedMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(detailedMessage));
        }
    }

    private void updateLocationDefaultFlag(String locationCode) {
        try {
            // Unset all current default locations
            locationService.findAll().forEach(location -> {
                if (Boolean.TRUE.equals(location.getIsDefault())) {
                    location.setIsDefault(false);
                    try {
                        locationService.save(location);
                    } catch (Exception e) {
                        log.error("Error updating location default flag: " + e.getMessage(), e);
                    }
                }
            });

            // Set the new default location
            locationService.findAll().stream()
                    .filter(location -> locationCode.equals(location.getLocationCode()))
                    .findFirst()
                    .ifPresent(location -> {
                        location.setIsDefault(true);
                        try {
                            locationService.save(location);
                        } catch (Exception e) {
                            log.error("Error setting location as default: " + e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error updating location default flags: " + e.getMessage(), e);
        }
    }

    private void updateCustomerDefaultFlag(String customerCode) {
        try {
            // Unset all current default customers
            customerService.findAll().forEach(customer -> {
                if (Boolean.TRUE.equals(customer.getIsDefault())) {
                    customer.setIsDefault(false);
                    try {
                        customerService.save(customer);
                    } catch (Exception e) {
                        log.error("Error updating customer default flag: " + e.getMessage(), e);
                    }
                }
            });

            // Set the new default customer
            customerService.findAll().stream()
                    .filter(customer -> customerCode.equals(customer.getCustomerCode()))
                    .findFirst()
                    .ifPresent(customer -> {
                        customer.setIsDefault(true);
                        try {
                            customerService.save(customer);
                        } catch (Exception e) {
                            log.error("Error setting customer as default: " + e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error updating customer default flags: " + e.getMessage(), e);
        }
    }
}

