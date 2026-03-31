package com.digithink.pos.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digithink.pos.model.Promotion;
import com.digithink.pos.model.enumeration.PromotionScope;

public interface PromotionRepository extends _BaseRepository<Promotion, Long> {

	Optional<Promotion> findByCode(String code);

	/** Total number of sales lines + sales headers that reference this promotion. */
	@Query(value =
		"SELECT (SELECT COUNT(*) FROM sales_line WHERE promotion_id = :id) " +
		"     + (SELECT COUNT(*) FROM sales_header WHERE promotion_id = :id)",
		nativeQuery = true)
	long countUsages(@Param("id") Long id);

	/** All active promotions for a given scope that are valid on the given date, ordered by priority DESC */
	@Query("SELECT p FROM Promotion p WHERE p.active = true AND p.scope = :scope "
		+ "AND (p.startDate IS NULL OR p.startDate <= :today) "
		+ "AND (p.endDate   IS NULL OR p.endDate   >= :today) "
		+ "ORDER BY p.priority DESC")
	List<Promotion> findActiveByScope(
		@Param("scope") PromotionScope scope,
		@Param("today") LocalDate today);

	/** Active promotions targeting a specific item, valid today */
	@Query("SELECT p FROM Promotion p WHERE p.active = true AND p.item.id = :itemId "
		+ "AND (p.startDate IS NULL OR p.startDate <= :today) "
		+ "AND (p.endDate   IS NULL OR p.endDate   >= :today) "
		+ "ORDER BY p.priority DESC")
	List<Promotion> findActiveByItemId(
		@Param("itemId") Long itemId,
		@Param("today") LocalDate today);

	/** Active promotions targeting a specific item family, valid today */
	@Query("SELECT p FROM Promotion p WHERE p.active = true AND p.itemFamily.id = :familyId "
		+ "AND (p.startDate IS NULL OR p.startDate <= :today) "
		+ "AND (p.endDate   IS NULL OR p.endDate   >= :today) "
		+ "ORDER BY p.priority DESC")
	List<Promotion> findActiveByItemFamilyId(
		@Param("familyId") Long familyId,
		@Param("today") LocalDate today);

	/** Active promotions targeting a specific item subfamily, valid today */
	@Query("SELECT p FROM Promotion p WHERE p.active = true AND p.itemSubFamily.id = :subFamilyId "
		+ "AND (p.startDate IS NULL OR p.startDate <= :today) "
		+ "AND (p.endDate   IS NULL OR p.endDate   >= :today) "
		+ "ORDER BY p.priority DESC")
	List<Promotion> findActiveByItemSubFamilyId(
		@Param("subFamilyId") Long subFamilyId,
		@Param("today") LocalDate today);
}
