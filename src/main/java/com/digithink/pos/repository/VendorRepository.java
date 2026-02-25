package com.digithink.pos.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.digithink.pos.model.Vendor;

public interface VendorRepository extends _BaseRepository<Vendor, Long> {

	Optional<Vendor> findByVendorCode(String vendorCode);

	long countByCreatedAtGreaterThanEqual(LocalDateTime date);
}
