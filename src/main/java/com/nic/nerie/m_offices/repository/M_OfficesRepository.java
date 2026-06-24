package com.nic.nerie.m_offices.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.m_offices.model.M_Offices;

public interface M_OfficesRepository extends JpaRepository<M_Offices, String> {
    @Query("FROM M_Offices ORDER BY officename")
    List<M_Offices> findAllOrderByOfficename();

    @Query("FROM M_Offices ORDER BY officename ASC")
    List<M_Offices> findAllByOrderByOfficenameAsc();

    @Query("SELECT MAX(CAST(o.officecode AS integer)) FROM M_Offices o")
    Integer findMaxOfficeCodeAsInteger();

    // Old
    //@Query(value = "SELECT COUNT(*) FROM nerie.mt_userlogin WHERE isfaculty='1' AND officecode=:officecode", nativeQuery = true)
    //Integer getOfficeFacultiesCount(@Param("officecode") String officecode);

    // New
    @Query(value =
                    "SELECT COUNT(*) " +
                            "FROM ( " +
                            "   SELECT usercode " +
                            "   FROM nerie.mt_userlogin " +
                            "   WHERE userrole = 'U' " +
                            "     AND userdescription ILIKE '%faculty%' " +
                            "     AND officecode = :officecode " +
                            "   GROUP BY usercode " +
                            "   HAVING COUNT(usermobile) < 2 " +
                            ") AS subquery",
            nativeQuery = true)
    Integer getOfficeFacultiesCount(@Param("officecode") String officecode);


    @Query(value = "SELECT COUNT(*) FROM nerie.mt_userlogin u "
                + "INNER JOIN nerie.t_students s ON s.usercode=u.usercode "
                + "WHERE u.userrole='T' AND s.officecode=:officecode", nativeQuery = true)
    Integer getOfficeStudentsCount(@Param("officecode") String officecode);

// Old
//    @Query(value = "SELECT COUNT(distinct rollno)  "
//                + "FROM nerie.t_alumni WHERE officecode=:officecode", nativeQuery = true)
//    Integer getOfficeAlumniCount(@Param("officecode") String officecode);

    // new
    @Query(
            value =
                    "SELECT COUNT(*) " +
                            "FROM nerie.t_alumni " +
                            "WHERE (:officecode = '2' OR officecode = :officecode)",
            nativeQuery = true
    )
    Integer getOfficeAlumniCount(@Param("officecode") String officecode);


    @Query(value = "SELECT COUNT(*) FROM nerie.mt_userlogin WHERE userrole='P' AND officecode = :officecode", nativeQuery = true)
    Integer getOfficeParticipantsCount(@Param("officecode") String officecode);

    // Spring generates: SELECT count(*) > 0 FROM m_offices WHERE shorttermcoursecode = ?
    boolean existsByShorttermcoursecode(String shorttermcoursecode);

    // Spring generates: SELECT count(*) > 0 FROM m_offices WHERE longtermcoursecode = ?
    boolean existsByLongtermcoursecode(String longtermcoursecode);

    // Check if code exists in OTHER records
    boolean existsByShorttermcoursecodeAndOfficecodeNot(String code, String officecode);

    boolean existsByLongtermcoursecodeAndOfficecodeNot(String code, String officecode);
}