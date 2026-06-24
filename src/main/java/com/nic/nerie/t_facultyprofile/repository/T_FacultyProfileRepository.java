package com.nic.nerie.t_facultyprofile.repository;

import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface T_FacultyProfileRepository extends JpaRepository<T_FacultyProfile, String> {
    @Query("SELECT f FROM T_FacultyProfile f LEFT JOIN FETCH f.researchpapers WHERE f.usercode.usercode = :usercode")
    T_FacultyProfile findFacultyProfileByUsercode(@Param("usercode") String usercode);

    @Query("SELECT MAX(CAST(fp.facultyprofileid AS integer)) FROM T_FacultyProfile fp")
    Integer findMaxFacultyProfileId();

    @Query(value = """
        SELECT 
            COALESCE(p.programname,'') AS programname,
            COALESCE(CC.coursecategoryname,'No Program Type Defined Yet') as programtype,
            COALESCE(p.programid::text,'') AS programid,
            COALESCE(NULLIF(ph.phasedescription,''),'No Phase Details') AS phasedescription,
            COALESCE(
                STRING_AGG(
                    TO_CHAR(pd.startdate,'DD Mon YYYY') || ' - ' || TO_CHAR(pd.enddate,'DD Mon YYYY'),
                    ', '
                ),
                'No Program Date Yet'
            ) AS program_date,
            p.programcode AS programcode
        FROM nerie.mt_program_members pm
        INNER JOIN nerie.m_phases ph 
            ON pm.phaseid = ph.phaseid
        INNER JOIN nerie.m_programs p 
            ON p.programcode = ph.programcode
        INNER JOIN nerie.mt_programdetails pd 
            ON pd.phaseid = ph.phaseid
        LEFT JOIN nerie.m_coursecategories CC 
            ON p.coursecodecategory = CC.coursecategorycode
        WHERE pm.usercode = :usercode
        AND pd.finalized = 'Y'
        GROUP BY 
            p.programname,
            p.programid,
            ph.phasedescription,
            CC.coursecategoryname,
            pm.program_memberid,
            p.programcode
        ORDER BY pm.program_memberid ASC;
        """, nativeQuery = true)
    List<Object[]> findProgramDetailsForFacultyProfile(@Param("usercode") String usercode);
}
