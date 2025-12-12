package com.digithink.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Customer;
import com.digithink.pos.service.CustomerService;
import com.digithink.pos.service.GeneralSetupService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("customer")
@Log4j2
public class CustomerAPI extends _BaseController<Customer, Long, CustomerService> {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private GeneralSetupService generalSetupService;

	/**
	 * Search customers by name, code, phone, or email
	 */
	@GetMapping("/search")
	public ResponseEntity<?> searchCustomers(@RequestParam(required = false) String q) {
		try {
			log.info("CustomerAPI::searchCustomers: " + q);
			
			if (q == null || q.trim().isEmpty()) {
				return ResponseEntity.ok(java.util.Collections.emptyList());
			}
			
			List<Customer> customers = customerService.searchCustomers(q.trim());
			// Only return active customers
			List<Customer> activeCustomers = customers.stream()
				.filter(c -> c.getActive() != null && c.getActive())
				.collect(java.util.stream.Collectors.toList());
			
			return ResponseEntity.ok(activeCustomers);
		} catch (Exception e) {
			log.error("CustomerAPI::searchCustomers:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get passenger customer (default customer)
	 */
	@GetMapping("/passenger")
	public ResponseEntity<?> getPassengerCustomer() {
		try {
			log.info("CustomerAPI::getPassengerCustomer");
			
			// Get passenger customer code from GeneralSetup
			String passengerCode = generalSetupService.findValueByCode("PASSENGER_CUSTOMER");
			
			// Check if passenger code is configured
			if (passengerCode == null || passengerCode.trim().isEmpty()) {
				log.warn("PASSENGER_CUSTOMER not configured in GeneralSetup");
				return ResponseEntity.status(400).body(createErrorResponse("Passenger not configured"));
			}
			
			// Retrieve customer using the code from GeneralSetup
			java.util.Optional<Customer> passenger = customerService.getCustomerRepository()
				.findByCustomerCode(passengerCode.trim());
			
			if (passenger.isPresent()) {
				return ResponseEntity.ok(passenger.get());
			} else {
				log.warn("Passenger customer not found with code: " + passengerCode);
				return ResponseEntity.status(404).body(createErrorResponse("Passenger customer not found"));
			}
		} catch (Exception e) {
			log.error("CustomerAPI::getPassengerCustomer:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Delete customer by ID - override to prevent deletion of passenger customer
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		try {
			log.info("CustomerAPI::deleteById::" + id);
			
			// Get customer to check if it's the passenger customer
			java.util.Optional<Customer> customerOpt = customerService.findById(id);
			if (!customerOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Customer not found"));
			}
			
			Customer customer = customerOpt.get();
			
			// Prevent deletion of passenger customer
			if (customer.getCustomerCode() != null && customer.getCustomerCode().equals("PASSENGER")) {
				log.warn("Attempt to delete passenger customer blocked: " + id);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(createErrorResponse("The passenger customer cannot be deleted as it is required by the system."));
			}
			
			// Proceed with deletion
			customerService.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error("CustomerAPI::deleteById:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}

	/**
	 * Set customer as default
	 */
	@PutMapping("/{id}/set-default")
	public ResponseEntity<?> setAsDefault(@PathVariable Long id) {
		try {
			log.info(this.getClass().getSimpleName() + "::setAsDefault::" + id);
			Customer customer = customerService.setAsDefault(id);
			return ResponseEntity.ok(customer);
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::setAsDefault:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}
}

