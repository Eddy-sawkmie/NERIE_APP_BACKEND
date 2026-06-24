package com.nic.nerie.m_processes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.m_processes.model.M_Processes;

public interface M_ProcessesRepository extends JpaRepository<M_Processes, Integer> {
        /* OLD QUERY:
        @Query(value = "SELECT M.mainmenucode, M.mainmenuname, P.menuname, P.newpageurl "
                        + " FROM nerie.m_processes P "
                        + " INNER JOIN nerie.mt_userprocesses U ON P.processcode = U.processcode "
                        + " INNER JOIN nerie.m_mainmenu M ON M.mainmenucode = P.mainmenucode "
                        + " WHERE U.usercode = :ucode"
                        + " ORDER BY M.mainmenucode, P.processcode", nativeQuery = true)
        List<Object[]> getProcessesForUserNavigation(@Param("ucode") String ucode);
        */

        /*
         * NEW Query using Role-Based Access (mt_role_process)
         *
         * Retrieves the navigation menu items based on the user's "Effective Role" and
         * specific process visibility rules.
         *
         * Logic for Role Mapping:
         * 1. Standard Roles (A, S, Z): Map directly to their assigned permissions in mt_role_process.
         * 2. Backend User ('U'):
         * - Role 'U' with iscoordinator='1' always inherits 'C' (Coordinator) permissions.
         * - Role 'U' with isfaculty='1' additionally inherits 'F' (Faculty) permissions.
         *
         * Logic for **Leave Application** (Process ID 44):
         * - This process is treated as a special case to restrict visibility.
         * - It is visible ONLY if:
         * a) The user has the Role 'Z' (Principal) - shown by default.
         * b) OR the user is explicitly mapped in the 'mt_la_usermapping' table
         * (assigned as Warden, Dean, or Chief Warden).
         * - If neither condition is met, Process 44 is excluded from the menu, even if mapped in mt_role_process.
         *
         * Uses SELECT DISTINCT to merge duplicate processes if they exist in both inherited roles.
         * P.processcode is selected to satisfy the ORDER BY clause when using DISTINCT.
         */
        @Query(value = """
                SELECT DISTINCT
                    M.mainmenucode, 
                    M.mainmenuname, 
                    P.menuname, 
                    P.newpageurl,
                    P.processcode      
                FROM nerie.mt_userlogin U
                INNER JOIN nerie.mt_userloginrole UR_ASSIGNED ON U.role_id = UR_ASSIGNED.role_id
                INNER JOIN nerie.mt_userloginrole UR_PERMS ON (
                    (UR_ASSIGNED.role_code NOT IN ('U') AND UR_PERMS.role_code = UR_ASSIGNED.role_code)
                    OR
                    (UR_ASSIGNED.role_code = 'U' AND U.iscoordinator = '1' AND UR_PERMS.role_code = 'C')
                    OR
                    (UR_ASSIGNED.role_code = 'U' AND U.isfaculty = '1' AND UR_PERMS.role_code = 'F')
                )
                INNER JOIN nerie.mt_role_process RP ON UR_PERMS.role_id = RP.role_id
                INNER JOIN nerie.m_processes P ON RP.processcode = P.processcode
                INNER JOIN nerie.m_mainmenu M ON P.mainmenucode = M.mainmenucode
                WHERE U.usercode = :ucode
                AND (
                    P.processcode != 44 
                    OR 
                    (
                        P.processcode = 44 
                        AND (
                            UR_ASSIGNED.role_code = 'Z'
                            OR 
                            EXISTS (SELECT 1 FROM nerie.mt_la_usermapping lam WHERE lam.usercode = U.usercode)
                        )
                    )
                )
                ORDER BY M.mainmenucode, P.processcode
            """, nativeQuery = true)
        List<Object[]> getProcessesForUserNavigation(@Param("ucode") String ucode);

        @Query("SELECT m FROM M_Processes m WHERE m.processcode IN :codes ORDER BY m.processcode")
        List<M_Processes> getPrincipalProcesses(@Param("codes") List<Integer> codes);

        @Query(value = "SELECT processcode, processname from M_Processes " +
                "WHERE processcode='1' OR processcode='7' OR processcode='10' OR processcode='17' OR processcode='19' OR processcode='24' " +
                "ORDER BY processcode", nativeQuery = true)
        List<Object[]> getPrincipalProcesses();

// OLD QUERY
//        @Query(value = "SELECT processcode, COALESCE(processname, '') " +
//                        "FROM nerie.m_processes " +
//                        "WHERE processcode IN (1, 7, 10, 17, 19, 24, 44) " +
//                        "ORDER BY processcode", nativeQuery = true)
//        List<Object[]> getPrincipalProcessesFixed(); // Changed return type to List<Object[]>

        @Query(value = "SELECT processcode, processname from M_Processes " +
                "WHERE processcode!='5'  OR processcode!='7' " +
                "ORDER BY processcode", nativeQuery = true)
        List<Object[]> getAllProcesses();

// OLD QUERY
//        @Query(value = "SELECT m.processcode, m.processname " +
//                        "FROM nerie.m_processes m " +
//                        "WHERE m.processcode NOT IN (7, 25, 42, 43) " +
//                        "ORDER BY m.processcode", nativeQuery = true)
//        List<Object[]> getAllProcesses();

        @Transactional
        @Query(value = """
                SELECT distinct(p.processcode), n.processname 
                FROM nerie.mt_userprocesses p, nerie.mt_userlogin u, nerie.m_processes n 
                WHERE u.usercode=p.usercode and p.processcode!=1 and p.processcode!=17 and p.processcode!=5 and p.processcode!=7 and u.userrole='A' and n.processcode=p.processcode and p.usercode=:usercode 
                ORDER BY p.processcode
                """, nativeQuery = true)
        List<Object[]> getLocalAdminProcesses(@Param("usercode") String usercode);

// OLD QUERY
//        @Transactional
//        @Query(value = """
//                SELECT distinct(p.processcode), n.processname
//                FROM nerie.mt_userprocesses p
//                JOIN nerie.mt_userlogin u ON u.usercode = p.usercode
//                JOIN nerie.m_processes n ON n.processcode = p.processcode
//                WHERE p.processcode NOT IN (1, 5, 7, 17, 25, 32, 42, 43)
//                """, nativeQuery = true)
//        List<Object[]> getLocalAdminProcesses(@Param("usercode") String usercode);

        @Transactional
        @Query(value = "SELECT usercode, processcode FROM nerie.mt_userprocesses WHERE usercode = :usercode", nativeQuery = true)
        List<Object[]> getUserProcesses(@Param("usercode") String usercode);

        @Transactional
        @Query(value = "SELECT processcode FROM nerie.mt_userprocesses WHERE usercode = :usercode", nativeQuery = true)
        List<String> getProcessesFromUsercode(@Param("usercode") String usercode);

        @Transactional
        @Modifying
        @Query(value = "INSERT INTO nerie.mt_userprocesses(usercode, processcode) " +
                        "VALUES(:usercode, :processcode)", nativeQuery = true)
        void createUserProcessesEntry(@Param("usercode") String usercode, @Param("processcode") Integer processcode);

        @Query(value = "SELECT processcode FROM nerie.m_processes WHERE mainmenucode = :mainmenucode", nativeQuery = true)
        List<Integer> findProcessCodesByMainMenuCode(@Param("mainmenucode") int mainmenucode);

        @Modifying
        @Query(value = "DELETE FROM nerie.mt_userprocesses up WHERE up.usercode = :usercode", nativeQuery = true)
        void removeUserProcessEntry(@Param("usercode") String usercode);

        @Modifying
        @Query(value = "INSERT INTO nerie.mt_userprocesses (usercode, processcode) VALUES (:usercode, :processcode)", nativeQuery = true)
        void insertUserProcess(@Param("usercode") String usercode, @Param("processcode") int processcode);


        /*
         * New Logic: Checks if the user's assigned ROLE has access to the process.
         * No more mt_userprocesses table check.
         * Checks if the user's "Effective Role" has access to the specific process.
         *
         * Logic for Role Mapping:
         * 1. Standard Roles (A, S, Z, etc.): Map directly to their own permissions.
         * 2. Backend User ('U'): Does not have direct entries in mt_role_process.
         *      - Role 'U' always inherits 'C' (Coordinator) permissions
         *      - Role 'U' with isfaculty='1' additionally inherits 'F' (Faculty) permissions
         *
         * This ensures Authorization matches the Dynamic Menu generation logic.
         */
        @Query(value = """
                SELECT COUNT(*) 
                FROM nerie.mt_userlogin U
                JOIN nerie.mt_userloginrole UR_ASSIGNED ON U.role_id = UR_ASSIGNED.role_id
                JOIN nerie.mt_userloginrole UR_PERMS ON (
                    (UR_ASSIGNED.role_code NOT IN ('U') AND UR_PERMS.role_code = UR_ASSIGNED.role_code)
                    OR
                    (UR_ASSIGNED.role_code = 'U' AND UR_PERMS.role_code = 'C')
                    OR
                    (UR_ASSIGNED.role_code = 'U' AND U.isfaculty = '1' AND UR_PERMS.role_code = 'F')
                )
                JOIN nerie.mt_role_process RP ON UR_PERMS.role_id = RP.role_id
                WHERE U.usercode = :usercode AND RP.processcode = :processcode
            """, nativeQuery = true)
        int userProcessExists(@Param("usercode") String usercode, @Param("processcode") Integer processcode);

        /*
         * Old Logic: Using only mt_userprocesses Table
         */
//        @Query(value = "SELECT EXISTS (SELECT 1 FROM nerie.mt_userprocesses up WHERE up.usercode = :usercode AND up.processcode = :processcode)", nativeQuery = true)
//        boolean userProcessExists(@Param("usercode") String usercode, @Param("processcode") Integer processcode);

        @Query(value = "SELECT * FROM nerie.m_processes ORDER BY processcode", nativeQuery = true)
        List<M_Processes> getActualAllProcessesNative();
}
