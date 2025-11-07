package com.digithink.vacation_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.digithink.vacation_app.model._BaseEntity;

@NoRepositoryBean
public interface _BaseRepository<T extends _BaseEntity, ID>
		extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	List<T> findAllByOrderByUpdatedAtDesc();

}
