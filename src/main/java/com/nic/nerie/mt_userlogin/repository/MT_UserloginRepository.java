package com.nic.nerie.mt_userlogin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;

public interface MT_UserloginRepository extends JpaRepository<MT_Userlogin, String> {
        @Modifying
        @Transactional
        @Query("UPDATE MT_Userlogin u SET u.userphotograph = :userphotograph WHERE u.usercode = :usercode")
        Integer updateUserphotographByUsercode(@Param("usercode") String usercode,
                        @Param("userphotograph") byte[] userphotograph);

        @Query("SELECT u FROM MT_Userlogin u WHERE u.userid = :userid")
        Optional<MT_Userlogin> findByUserId(@Param("userid") String userid);

        @Query("SELECT u from MT_Userlogin u WHERE u.username = :username")
        Optional<MT_Userlogin> findByUsername(String username);

        @Query("SELECT u.userpassword FROM MT_Userlogin u WHERE u.usercode = :usercode")
        String getUserpasswordByUsercode(@Param("usercode") String usercode);

        // Query to get the maximum usercode
        @Query("SELECT MAX(CAST(u.usercode as int)) FROM MT_Userlogin u")
        Optional<Integer> findMaxUsercodeAsInt();

        @Query("SELECT u FROM MT_Userlogin u WHERE u.usercode = :usercode")
        Optional<MT_Userlogin> findByUsercode(@Param("usercode") String usercode);

        // For Super Admin (S) and Principal (Z) - viewing their subordinates
        @Query(value = """
            SELECT 
                u.usercode, u.username, u.userdescription, u.userid, u.userpassword, 
                u.enabled, u.userrole, u.usermobile, u.ismodified, u.emailid, 
                o.officecode, o.officename, d.designationcode, d.designationname,
                la.larolecode
            FROM nerie.mt_userlogin u 
            INNER JOIN nerie.m_offices o ON u.officecode = o.officecode 
            LEFT JOIN nerie.m_designations d ON u.designationcode = d.designationcode 
            LEFT JOIN nerie.mt_la_usermapping la ON u.usercode = la.usercode
            WHERE u.userrole = :urole 
            ORDER BY u.entrydate DESC
        """, nativeQuery = true)
        List<Object[]> getUserListByRole(@Param("urole") String urole);

        List<MT_Userlogin> findByRole_RoleCode(@Param("urole") String urole);

        // For filtering by both role AND office (for Z viewing A)
        @Query(value = """
            SELECT 
                u.usercode, u.username, u.userdescription, u.userid, u.userpassword, 
                u.enabled, u.userrole, u.usermobile, u.ismodified, u.emailid, 
                o.officecode, o.officename, d.designationcode, d.designationname,
                la.larolecode
            FROM nerie.mt_userlogin u 
            INNER JOIN nerie.m_offices o ON u.officecode = o.officecode 
            LEFT JOIN nerie.m_designations d ON u.designationcode = d.designationcode 
            LEFT JOIN nerie.mt_la_usermapping la ON u.usercode = la.usercode
            WHERE u.userrole = :urole 
            AND u.officecode = :officecode
            ORDER BY u.entrydate DESC
        """, nativeQuery = true)
        List<Object[]> getUserListByRoleAndOffice(@Param("urole") String urole, @Param("officecode") String officecode);


        // For Local Admin (A) - viewing users in their office only
        @Query(value = """
                SELECT 
                    u.usercode,
                    u.username,
                    u.userdescription,
                    u.userid,
                    u.userpassword,
                    u.enabled,
                    u.userrole,  
                    u.usermobile,
                    u.isfaculty,
                    u.emailid,
                    o.officecode,
                    o.officename,
                    d.designationcode,
                    d.designationname,
                    la.larolecode,   
                    u.ismodified, 
                    u.iscoordinator 
                FROM nerie.mt_userlogin u 
                LEFT JOIN nerie.m_offices o ON u.officecode = o.officecode 
                LEFT JOIN nerie.m_designations d ON u.designationcode = d.designationcode 
                LEFT JOIN nerie.mt_la_usermapping la ON u.usercode = la.usercode
                WHERE u.userrole = :urole 
                AND u.officecode = :officecode
                ORDER BY u.entrydate DESC
            """, nativeQuery = true)
        List<Object[]> getAdminUserList(@Param("urole") String urole, @Param("officecode") String officecode);

// OLD QUERY
//        @Query(value = """
//            SELECT
//                u.usercode, u.username, u.userdescription, u.userid, u.userpassword,
//                u.enabled, u.userrole, u.usermobile, u.ismodified, u.emailid,
//                o.officecode, o.officename, d.designationcode, d.designationname,
//                la.larolecode
//            FROM nerie.mt_userlogin u
//            LEFT JOIN nerie.m_offices o ON u.officecode = o.officecode
//            LEFT JOIN nerie.m_designations d ON u.designationcode = d.designationcode
//            LEFT JOIN nerie.mt_la_usermapping la ON u.usercode = la.usercode
//            WHERE u.userrole = :urole
//            AND u.officecode = :officecode
//            ORDER BY u.entrydate DESC
//        """, nativeQuery = true)
//        List<Object[]> getAdminUserList(@Param("urole") String urole, @Param("officecode") String officecode);


        // Check if email exists (Saving Userlogin Scenarios)
        @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM MT_Userlogin u WHERE LOWER(u.emailid) = LOWER(:emailId)")
        Boolean checkIfEmailExistsIgnoreCase(@Param("emailId") String emailId);

        // Check if email exists excluding a specific usercode (Update Userlogin
        // Scenarios)
        @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM MT_Userlogin u WHERE LOWER(u.emailid) = LOWER(:emailId) AND u.usercode <> :usercode")
        Boolean checkIfEmailExistsIgnoreCaseExcludingUsercode(@Param("emailId") String emailId,
                        @Param("usercode") String usercode);

        @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM MT_Userlogin u WHERE u.userid = :userid OR u.usercode = :usercode OR u.emailid = :emailid OR u.usermobile = :usermobile")
        boolean existsByUseridUsercodeEmailidUsermobile(@Param("userid") String userid,
                        @Param("usercode") String usercode,
                        @Param("emailid") String emailid, @Param("usermobile") String usermobile);

        @Query("SELECT MAX(CAST(u.usercode AS int)) FROM MT_Userlogin u")
        Integer findLastUsercodeUsed();

        @Modifying
        @Transactional
        @Query(value = "UPDATE nerie.mt_userlogin SET enabled = CASE WHEN enabled = 1 THEN 0 ELSE 1 END, ismodified = 'Y' WHERE usercode = :usercode", nativeQuery = true)
        int toggleUserStatus(@Param("usercode") String usercode);

        @Query(value = "SELECT u.usercode, u.username FROM nerie.mt_userlogin u WHERE u.officecode = :officecode ORDER BY u.username", nativeQuery = true)
        List<Object[]> getOfficeUserForCoordinator(@Param("officecode") String officecode);

        @Query(value = "SELECT u.usercode, u.username " +
                "FROM nerie.mt_userlogin u " +
                "WHERE u.enabled = 1 AND u.usercode = :usercode " +
                "AND u.userrole = 'U' AND u.officecode = :officecode", nativeQuery = true)
        List<Object[]> findFacultyCandidatesByUsercode(@Param("usercode") String usercode, @Param("officecode") String officecode);

        @Query(value = "SELECT u.usercode, u.username " +
                "FROM nerie.mt_userlogin u " +
                "WHERE u.enabled = 1 AND u.userrole = 'U' AND u.officecode = :officecode", nativeQuery = true)
        List<Object[]> findFacultyCandidates(@Param("officecode") String officecode);

        @Modifying
        @Query(value = "UPDATE nerie.mt_userlogin SET isfaculty = '1' WHERE usercode = :usercode", nativeQuery = true)
        void updateIsFaculty(@Param("usercode") String usercode);

        @Query(value = "SELECT * FROM mt_userlogin WHERE UPPER(userid) = :emailid AND usercode != :usercode", nativeQuery = true)
        MT_Userlogin checkEmailExistsForUpdate(@Param("emailid") String emailid, @Param("usercode") String usercode);

        @Modifying
        @Transactional
        @Query(value = "UPDATE mt_userlogin SET userid = :emailid, username = :username, usermobile = :usermobile, " +
                        "emailid = :emailid, ismodified = 'N', designationcode = :designationcode WHERE usercode = :usercode", nativeQuery = true)
        int updateParticipantUserProfile(@Param("emailid") String emailid,
                        @Param("username") String username,
                        @Param("usermobile") String usermobile,
                        @Param("designationcode") String designationcode,
                        @Param("usercode") String usercode);

        @Query(value = "SELECT COUNT(*) "
                        + "FROM nerie.mt_userlogin "
                        + "WHERE userrole='U' AND officecode=:officecode", nativeQuery = true)
        Integer getCoordinatorsCount(@Param("officecode") String officecode);

        @Query(value = """
                        SELECT u.* 
                        FROM mt_userlogin u
                        JOIN t_applications a ON a.usercode = u.usercode
                        WHERE a.phaseid = :phaseId AND a.status = 'A'
                        """, nativeQuery = true)
        List<MT_Userlogin> findParticipantsByPhase(@Param("phaseId") String phaseId);

        @Query("SELECT CASE WHEN u.enabled = 1 THEN true ELSE false END FROM MT_Userlogin u WHERE u.userid = :userid")
        boolean isUserEnabled(@Param("userid") String userid);

        @Query("SELECT u.useBcrypt FROM MT_Userlogin u WHERE u.usercode = :usercode")
        boolean isUserBcrypt(@Param("usercode") String usercode);
}
