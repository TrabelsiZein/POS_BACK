package com.digithink.vacation_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.digithink.vacation_app.model.Role;

public interface RoleRepository extends _BaseRepository<Role, Long> {

	@Query("select new com.digithink.vacation_app.model.Role(r.id,r.name,r.description) from Role r")
	List<Role> findAll();
}
