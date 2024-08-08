package com.digithink.business_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.digithink.business_management.model._BaseEntity;

@NoRepositoryBean
public interface _BaseRepository<T extends _BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	List<T> findAllByCompanyOrderByUpdatedAtDesc(Long company);

}
