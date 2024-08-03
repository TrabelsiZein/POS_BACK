package com.digithink.business_management.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends _BaseEntity implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(unique = true, nullable = false)
	private String email;

	private Boolean active = true;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return roles.stream().flatMap(role -> role.getPermissions().stream())
//				.map(permission -> new SimpleGrantedAuthority(permission.getAction() + "_" + permission.getPage()))
//				.collect(Collectors.toList());
//	}

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		Set<PermissionDTO> authorities = new HashSet<>();
//
//		// Iterate through the roles assigned to the user
//		for (Role role : roles) {
//
//			// Iterate through the permissions associated with the role
//			for (Permission permission : role.getPermissions()) {
//				PermissionDTO dto = new PermissionDTO();
//				dto.setPermission_id(permission.getPage() + "_" + permission.getAction());
//				dto.setModule_ar(permission.getModule().getTitleAr());
//				dto.setModule_en(permission.getModule().getTitleEn());
//				dto.setModule_fr(permission.getModule().getTitleFr());
//				dto.setPage_ar(permission.getTitleAr());
//				dto.setPage_en(permission.getTitleEn());
//				dto.setPage_fr(permission.getTitleFr());
//				authorities.add(dto);
//			}
//		}
//
//		return (Collection<? extends GrantedAuthority>) authorities;
//	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return active;
	}

}