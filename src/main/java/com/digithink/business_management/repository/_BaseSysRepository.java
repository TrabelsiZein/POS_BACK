package com.digithink.business_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.digithink.business_management.model._BaseSysEntity;

@NoRepositoryBean
public interface _BaseSysRepository<T extends _BaseSysEntity, ID>
		extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	List<T> findAllByOrderByUpdatedAtDesc();

}
