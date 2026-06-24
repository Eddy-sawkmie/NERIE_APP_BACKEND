package com.nic.nerie.mt_resourcepersons.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.mt_resourcepersons.dto.ResourcePersonsDTO;
import com.nic.nerie.mt_resourcepersons.model.MT_ResourcePersons;

import jakarta.transaction.Transactional;

public interface MT_ResourcePersonsRepository extends JpaRepository<MT_ResourcePersons, String> {
    @Query("SELECT rp FROM MT_ResourcePersons rp WHERE rp.moffices.officecode = :officecode")
    List<MT_ResourcePersons> findAllByOfficecode(@Param("officecode") String officecode);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM MT_ResourcePersons r WHERE UPPER(r.rpemailid) = UPPER(:rpemailid)")
    boolean existsByRpemailid(@Param("rpemailid") String rpemailid);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM MT_ResourcePersons r WHERE UPPER(r.rpemailid) = UPPER(:rpemailid) AND r.rpslno != :rpslno")
    boolean existsByRpemailidAndNotId(@Param("rpemailid") String rpemailid, @Param("rpslno") String rpslno);

    @Query(value = "SELECT DISTINCT rp.rpslno, rp.rpemailid, rp.rpname, q.qualificationname, rp.rpspecialization, " +
                "d.designationname, rp.rpinstitutename, rp.rpofficeaddress, p.phaseid " +
                "FROM nerie.mt_resourcepersons rp " +
                "LEFT JOIN nerie.mt_resourcepersoncoursemap rpm ON rp.rpslno = rpm.rpslno AND rpm.phaseid = :phaseid " +
                "LEFT JOIN nerie.m_phases p ON p.phaseid = rpm.phaseid " +
                "LEFT JOIN nerie.m_qualifications q ON q.qualificationcode = rp.qualificationcode " +
                "LEFT JOIN nerie.m_designations d ON d.designationcode = rp.designationcode " +
                "ORDER BY rp.rpname", 
        nativeQuery = true)
    List<Object[]> getAllResourcePersonsWithPhase(@Param("phaseid") String phaseid);

    @Query(value = """
       SELECT rc.rpslno, p.rpname 
       FROM nerie.mt_resourcepersoncoursemap rc 
       INNER JOIN nerie.mt_resourcepersons p ON rc.rpslno = p.rpslno 
       WHERE rc.phaseid = :phaseid 
       ORDER BY p.rpname
       """, nativeQuery = true) 
    List<Object[]> getResourcePersonsPhaseid(@Param("phaseid") String phaseid);

    @Query("SELECT MAX(CAST(rp.rpslno as int)) FROM MT_ResourcePersons rp")
    Integer getLastUsedRpslno();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM nerie.mt_resourcepersoncoursemap rp where rp.phaseid = :phaseid", nativeQuery = true)
    void deleteResourcePersonCourseEntryByPhaseid(@Param("phaseid") String phaseid);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO nerie.mt_resourcepersoncoursemap(phaseid, rpslno) " + 
    "VALUES(:phaseid, :resourceperson) ", nativeQuery = true)
    void createResourcePersonCourseEntry(@Param("phaseid") String phaseid, @Param("resourceperson") String resourceperson);

    //-----------------------------------------------------------------------------------------------------------------------
    @Query(value = """
        SELECT 
            rp.rpname AS name,
            rp.rpemailid AS email,
            q.qualificationname AS qualification,
            rp.rpofficeaddress AS office,
            d.designationname AS designation
        FROM nerie.mt_resourcepersons rp
        LEFT JOIN nerie.m_qualifications q 
               ON rp.qualificationcode = q.qualificationcode
        LEFT JOIN nerie.m_designations d 
               ON rp.designationcode = d.designationcode
        """,
        countQuery = """
        SELECT COUNT(*) FROM nerie.mt_resourcepersons
        """,
        nativeQuery = true)
    Page<ResourcePersonsDTO> getResourcePersons(Pageable pageable);

    @Query(value = """
        SELECT 
                rp.rpname AS name,
                rp.rpemailid AS email,
                q.qualificationname AS qualification,
                rp.rpofficeaddress AS office,
                d.designationname AS designation
        FROM nerie.mt_resourcepersons rp
        LEFT JOIN nerie.m_qualifications q 
                ON rp.qualificationcode = q.qualificationcode
        LEFT JOIN nerie.m_designations d 
                ON rp.designationcode = d.designationcode
        WHERE 
                LOWER(rp.rpname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(rp.rpemailid) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(q.qualificationname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(rp.rpofficeaddress) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.designationname) LIKE LOWER(CONCAT('%', :search, '%'))
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM nerie.mt_resourcepersons rp
        LEFT JOIN nerie.m_qualifications q 
                ON rp.qualificationcode = q.qualificationcode
        LEFT JOIN nerie.m_designations d 
                ON rp.designationcode = d.designationcode
        WHERE 
                LOWER(rp.rpname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(rp.rpemailid) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(q.qualificationname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(rp.rpofficeaddress) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(d.designationname) LIKE LOWER(CONCAT('%', :search, '%'))
        """,
        nativeQuery = true)
        Page<ResourcePersonsDTO> searchResourcePersons(String search, Pageable pageable);
    //---------------------------------------------------------------------------------------------------------------------
}
