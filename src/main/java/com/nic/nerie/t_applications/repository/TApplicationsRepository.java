package com.nic.nerie.t_applications.repository;

import com.nic.nerie.t_applications.model.T_Applications;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TApplicationsRepository extends JpaRepository<T_Applications, String> {

    @Modifying
    @Transactional
    @Query("UPDATE T_Applications ta SET ta.remarks = :remarks, ta.status = :status " +
            "WHERE ta.mtuserlogin.usercode = :usercode AND ta.phaseid.phaseid = :phaseid")
    int updateStatusAndRemarksByUsercodeAndPhaseid(
            @Param("usercode") String usercode,
            @Param("phaseid") String phaseid,
            @Param("remarks") String remarks,
            @Param("status") String status
    );

    @Query("SELECT CASE WHEN COUNT(ta) > 0 THEN true ELSE false END " +
           "FROM T_Applications ta " +
           "WHERE ta.mtuserlogin.usercode = :usercode " + 
           "AND ta.phaseid.phaseid = :phaseid")
    boolean existsByUsercodeAndPhaseid(@Param("usercode") String usercode, @Param("phaseid") String phaseid);

    @Query(value = "SELECT u.usercode, u.username, a.applicationcode, pa.attendance " +
           "FROM nerie.t_applications a " +
           "INNER JOIN nerie.mt_userlogin u ON u.usercode = a.usercode " +
           "LEFT JOIN nerie.t_participantattendance pa ON pa.pusercode = a.usercode AND pa.programtimetablecode = :programtimetablecode " +
           "WHERE a.status = 'A' AND a.phaseid = :phaseid", nativeQuery = true)
    List<Object[]> findParticipantsInSession(
            @Param("programtimetablecode") String programtimetablecode,
            @Param("phaseid") String phaseid
    );

    @Query(value =
            "SELECT " +
                    "p.programname, " +//0
                    "p.programid, " +//1
                    "ul.username, " +//2
                    "des.designationname, " +//3
                    "q.qualificationname, " +//4
                    "par.participantteachingsubjects, " +//5
                    "par.participantexperienceyears, " +//6
                    "par.gender, " +//7
                    "par.otherparticipantofficetype, " +//8
                    "par.participantofficeaddress, " +//9
                    "par.addressline1, " +//10
                    "ul.usermobile, " +//11
                    "ul.emailid, " +//12
                    "par.locality, " +//13
                    "cat.categoryname, " +//14
                    "par.isminority, " +//15
                    "min.minorityname, " +//16
                    "par.others, " +//17
                    "app.remarks, " +//18
                    "STRING_AGG(DISTINCT v.venuename, ',') AS venuename, " +//19
                    "STRING_AGG(DISTINCT cu.username, ',') AS coordinator, app.entrydate " +//20 //21
                    "FROM nerie.t_applications app " +
                    "INNER JOIN nerie.m_phases ph ON app.phaseid = ph.phaseid " +
                    "INNER JOIN nerie.m_programs p ON ph.programcode = p.programcode " +
                    "INNER JOIN nerie.mt_userlogin ul ON app.usercode = ul.usercode " +
                    "INNER JOIN nerie.t_participants par ON par.usercode = app.usercode " +
                    "INNER JOIN nerie.mt_programvenues pv ON ph.phaseid = pv.phaseid " +
                    "INNER JOIN nerie.m_venues v ON pv.venuecode = v.venuecode " +
                    "INNER JOIN nerie.mt_program_members pm ON pm.phaseid = ph.phaseid " +
                    "INNER JOIN nerie.mt_userlogin cu ON cu.usercode = pm.usercode " +
                    "LEFT JOIN nerie.m_designations des ON par.participantdesignationcode = des.designationcode " +
                    "LEFT JOIN nerie.m_categories cat ON par.categorycode = cat.categorycode " +
                    "LEFT JOIN nerie.m_minorities min ON par.minoritycode = min.minoritycode " +
                    "LEFT JOIN nerie.m_qualifications q ON par.participantqualificationcode = q.qualificationcode " +
                    "WHERE app.applicationcode = :applicationcode " +
                    "GROUP BY " +
                    "p.programname, p.programid, ul.username, des.designationname, q.qualificationname, " +
                    "par.participantteachingsubjects, par.participantexperienceyears, par.gender, " +
                    "par.otherparticipantofficetype, par.participantofficeaddress, par.addressline1, " +
                    "ul.usermobile, ul.emailid, par.locality, cat.categoryname, par.isminority, " +
                    "min.minorityname, par.others, app.remarks, app.entrydate",
            nativeQuery = true)
    List<Object[]> getApplicationParticipantDetails(
            @Param("applicationcode") String applicationcode);

            @Query(value =
            "SELECT "
                + "p.programname, " //0
                + "p.programid, " //1
                + "app.name, " //2
                + "app.designation, " //3
                + "app.educationalqualification, " //4
                + "app.experience, " //5
                + "app.gender, " //6
                + "app.addressoffice, " //7
                + "app.addressresidence, " //8
                + "app.contactno, " //9
                + "app.emailid, " //10
                + "app.localityregion, " //11
                + "app.category, " //12
                + "app.religiousminority, " //13
                + "app.religiousminorityname, " //14
                + "app.remarks, "//15
                + "STRING_AGG(distinct v.venuename, ',' ORDER BY v.venuename) AS venuename, " //16
                + "STRING_AGG(distinct cu.username, ',') AS coordinator, app.entrydate " //17 18
                + "FROM nerie.t_applications app "
                + "INNER JOIN nerie.m_phases ph ON app.phaseid = ph.phaseid "
                + "INNER JOIN nerie.m_programs p ON ph.programcode = p.programcode "
                + "INNER JOIN nerie.mt_userlogin ul ON app.usercode = ul.usercode "
                + "INNER JOIN nerie.mt_programvenues pv ON ph.phaseid = pv.phaseid "
                + "INNER JOIN nerie.m_venues v ON pv.venuecode = v.venuecode "
                + "INNER JOIN nerie.mt_program_members pm ON pm.phaseid = ph.phaseid "
                + "INNER JOIN nerie.mt_userlogin cu ON cu.usercode = pm.usercode "
                + "WHERE app.applicationcode = :applicationcode "
                + "GROUP BY " +
                "    p.programname, " +
                "    p.programid, " +
                "    app.name, " +
                "    app.designation, " +
                "    app.educationalqualification, " +
                "    app.experience, " +
                "    app.gender, " +
                "    app.addressoffice, " +
                "    app.addressresidence, " +
                "    app.contactno, " +
                "    app.emailid, " +
                "    app.localityregion," +
                "    app.category, " +
                "    app.religiousminority, " +
                "    app.religiousminorityname, " +
                "    app.remarks,app.entrydate;",
            nativeQuery = true)
    List<Object[]> getApplicationParticipantDetailsV2(
            @Param("applicationcode") String applicationcode);

}