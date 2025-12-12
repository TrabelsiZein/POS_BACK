package com.digithink.pos.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.model.Customer;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository._BaseRepository;
import com.digithink.pos.service.GeneralSetupService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CustomerService extends _BaseService<Customer, Long> {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GeneralSetupService generalSetupService;

	@Override
	protected _BaseRepository<Customer, Long> getRepository() {
		return customerRepository;
	}

	/**
	 * Get CustomerRepository specifically (for accessing in controller)
	 */
	public CustomerRepository getCustomerRepository() {
		return customerRepository;
	}

	/**
	 * Override save to auto-generate customer code if not provided
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Customer save(Customer customer) throws Exception {
		// Validate required fields
		if (customer.getName() == null || customer.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Customer name is required");
		}
		if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
			throw new IllegalArgumentException("Customer phone is required");
		}

		// Generate customer code if not provided or empty (only for new customers)
		if (customer.getId() == null && (customer.getCustomerCode() == null || customer.getCustomerCode().trim().isEmpty())) {
			customer.setCustomerCode(generateCustomerCode());
		} else if (customer.getId() == null && customer.getCustomerCode() != null && !customer.getCustomerCode().trim().isEmpty()) {
			// Check if code already exists
			if (customerRepository.findByCustomerCode(customer.getCustomerCode()).isPresent()) {
				throw new IllegalArgumentException("Customer code already exists: " + customer.getCustomerCode());
			}
		}
		// For updates, keep existing code (don't regenerate)

		return super.save(customer);
	}

	/**
	 * Generate unique customer code: CUST-YYYYMMDD-XXX (incremental by day)
	 */
	private String generateCustomerCode() {
		// Count customers created today
		LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		long count = customerRepository.countByCreatedAtGreaterThanEqual(todayStart);

		// Format: CUST-YYYYMMDD-XXX
		String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String code = "CUST-" + dateStr + "-" + String.format("%03d", count + 1);

		log.info("Generated customer code: " + code);
		return code;
	}

	/**
	 * Search customers by name, code, phone, or email
	 */
	public java.util.List<Customer> searchCustomers(String searchTerm) {
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			return java.util.Collections.emptyList();
		}
		
		String searchPattern = "%" + searchTerm.toLowerCase() + "%";
		
		// Use JPA Specification to search across multiple fields
		org.springframework.data.jpa.domain.Specification<Customer> spec = (root, query, criteriaBuilder) -> {
			javax.persistence.criteria.Predicate namePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("name")), searchPattern);
			javax.persistence.criteria.Predicate codePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("customerCode")), searchPattern);
			javax.persistence.criteria.Predicate phonePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("phone")), searchPattern);
			javax.persistence.criteria.Predicate emailPredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("email")), searchPattern);
			
			// Combine with OR
			return criteriaBuilder.or(namePredicate, codePredicate, phonePredicate, emailPredicate);
		};
		
		return customerRepository.findAll(spec);
	}

	@Transactional
	public Customer setAsDefault(Long customerId) {
		java.util.Optional<Customer> customerOpt = customerRepository.findById(customerId);
		if (!customerOpt.isPresent()) {
			throw new RuntimeException("Customer not found with id: " + customerId);
		}

		Customer customer = customerOpt.get();

		// Unset all other default customers
		customerRepository.findByIsDefaultTrue().ifPresent(currentDefault -> {
			if (!currentDefault.getId().equals(customerId)) {
				currentDefault.setIsDefault(false);
				customerRepository.save(currentDefault);
			}
		});

		// Set this customer as default
		customer.setIsDefault(true);
		Customer savedCustomer = customerRepository.save(customer);

		// Update GeneralSetup
		generalSetupService.updateValue("PASSENGER_CUSTOMER", customer.getCustomerCode());

		return savedCustomer;
	}
}

