package com.nic.nerie.m_venues.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.m_venues.model.M_Venues;

public interface M_VenuesRepository extends JpaRepository<M_Venues, String> {
    @Query("SELECT v FROM M_Venues v WHERE v.venuecode = :venuecode")
    Optional<M_Venues> findByVenuecode(@Param("venuecode") String venuecode);

    @Query("FROM M_Venues v WHERE v.moffices.officecode = :officecode ORDER BY v.venuename")
    List<M_Venues> findByOfficecodeOrderByVenuename(@Param("officecode") String officecode);

    @Query(value = """
       SELECT CS.venuecode AS key, 
       S.venuename AS value 
       FROM nerie.mt_programvenues CS 
       INNER JOIN nerie.m_venues S ON CS.venuecode = S.venuecode 
       WHERE CS.phaseid = :phaseid 
       ORDER BY CS.venuecode
       """, nativeQuery = true)    
    List<Object[]> getByPhaseid(@Param("phaseid") String phaseid);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
           "FROM M_Venues v " +
           "WHERE UPPER(v.venuename) = UPPER(:venuename) " +
           "AND v.moffices.officecode = :officecode " +
           "AND (:venuecode IS NULL OR v.venuecode != :venuecode)")
    boolean existsByVenuenameOfficecodeAndVenuecode(@Param("venuename") String venuename,
                                           @Param("officecode") String officecode,
                                           @Param("venuecode") String venuecode);

    @Query("SELECT MAX(CAST(v.venuecode AS int)) FROM M_Venues v")
    Integer findMaxVenuecode();
}
