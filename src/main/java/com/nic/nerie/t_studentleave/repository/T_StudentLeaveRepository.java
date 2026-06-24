package com.nic.nerie.t_studentleave.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.t_studentleave.model.T_StudentLeave;
import org.springframework.transaction.annotation.Transactional;

public interface T_StudentLeaveRepository extends JpaRepository<T_StudentLeave, String> {
        @Query("SELECT l FROM T_StudentLeave l WHERE l.studentleaveid = :studentleaveid")
        Optional<T_StudentLeave> findByStudentleaveid(@Param("studentleaveid") String studentleaveid);

        @Query("SELECT l FROM T_StudentLeave l WHERE l.studentid.studentid = :studentid ORDER BY l.applicationdate DESC")
        List<T_StudentLeave> getByStudentid(@Param("studentid") String studentid);

        @Query(value = "SELECT MAX(CAST(studentleaveid AS INTEGER)) FROM nerie.t_studentleave", nativeQuery = true)
        Integer findMaxStudentLeaveId();

        @Query(value = "SELECT * FROM T_StudentLeave WHERE isApproved IS NOT NULL " + // isdeanapproved='2' means
                                                                                      // dayscholar (bypass warden
                                                                                      // approval) new->//where
                                                                                      // (iswardenapproved='1' OR
                                                                                      // iswardenapproved='2') AND
                                                                                      // isdeanapproved='1'
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN isApproved IS NULL THEN 0 " +
                        "  WHEN isApproved = '0' THEN 1 " +
                        "  WHEN isApproved = '1' THEN 2 " +
                        "END, " +
                        "applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getAllStudentLeaveApplications();

        /* OLD
         * Queries Without Office Code
        @Query(value = "SELECT * FROM t_studentleave ORDER BY " +
                        "CASE " +
                        "  WHEN isapproved IS NULL THEN 0 " +
                        "  WHEN isapproved = '0' THEN 1 " +
                        "  WHEN isapproved = '1' THEN 2 " +
                        "END, " +
                        "applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPStudentLeaveApplications();

        @Query(value = "SELECT * FROM t_studentleave where studentid.gender='M' " +
                "ORDER BY " +
                "CASE " +
                "  WHEN isapproved IS NULL THEN 0 " +
                "  WHEN isapproved = '0' THEN 1 " +
                "  WHEN isapproved = '1' THEN 2 " +
                "END, " +
                "applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPMStudentLeaveApplications();

        @Query(value = "SELECT * FROM t_studentleave where studentid.gender='F' " +
                "ORDER BY " +
                "CASE " +
                "  WHEN isapproved IS NULL THEN 0 " +
                "  WHEN isapproved = '0' THEN 1 " +
                "  WHEN isapproved = '1' THEN 2 " +
                "END, " +
                "applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPFStudentLeaveApplications();
        */

        // New
        @Query(value = "SELECT sl.* FROM nerie.t_studentleave sl " +
                "JOIN nerie.t_students s ON sl.studentid = s.studentid " +
                "WHERE s.officecode = :officeCode " +
                "ORDER BY CASE " +
                "  WHEN sl.isapproved IS NULL THEN 0 " +
                "  WHEN sl.isapproved = '0' THEN 1 " +
                "  WHEN sl.isapproved = '1' THEN 2 " +
                "END, sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPStudentLeaveApplications(@Param("officeCode") String officeCode);

        // New
        @Query(value = "SELECT sl.* FROM nerie.t_studentleave sl " +
                "JOIN nerie.t_students s ON sl.studentid = s.studentid " +
                "WHERE s.officecode = :officeCode AND s.gender = 'M' " +
                "ORDER BY CASE " +
                "  WHEN sl.isapproved IS NULL THEN 0 " +
                "  WHEN sl.isapproved = '0' THEN 1 " +
                "  WHEN sl.isapproved = '1' THEN 2 " +
                "END, sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPMStudentLeaveApplications(@Param("officeCode") String officeCode);

        // New
        @Query(value = "SELECT sl.* FROM nerie.t_studentleave sl " +
                "JOIN nerie.t_students s ON sl.studentid = s.studentid " +
                "WHERE s.officecode = :officeCode AND s.gender = 'F' " +
                "ORDER BY CASE " +
                "  WHEN sl.isapproved IS NULL THEN 0 " +
                "  WHEN sl.isapproved = '0' THEN 1 " +
                "  WHEN sl.isapproved = '1' THEN 2 " +
                "END, sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getPFStudentLeaveApplications(@Param("officeCode") String officeCode);

        @Query(value = "SELECT sl.* FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "WHERE (sl.iswardenapproved IS NULL OR sl.iswardenapproved = '0') " +
                        "AND sl.isdeanapproved IS NULL " +
                        "AND sl.isapproved IS NULL " +
                        "AND s.gender = 'M' " +
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN sl.isapproved IS NULL THEN 0 " +
                        "  WHEN sl.isapproved = '0' THEN 1 " +
                        "  WHEN sl.isapproved = '1' THEN 2 " +
                        "END, " +
                        "sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getMStudentLeaveApplications();

        @Query(value = "SELECT sl.* FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "WHERE (sl.iswardenapproved IS NULL OR sl.iswardenapproved = '0') " +
                        "AND sl.isdeanapproved IS NULL " +
                        "AND sl.isapproved IS NULL " +
                        "AND s.gender = 'F' " +
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN sl.isapproved IS NULL THEN 0 " +
                        "  WHEN sl.isapproved = '0' THEN 1 " +
                        "  WHEN sl.isapproved = '1' THEN 2 " +
                        "END, " +
                        "sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getFStudentLeaveApplications();

        @Query(value = "SELECT * FROM t_studentleave sl " +
                        "WHERE (sl.iswardenapproved = '1' OR sl.iswardenapproved = '2') " +
                        "AND (sl.isdeanapproved IS NULL OR sl.isdeanapproved = '0') " +
                        "AND sl.isapproved IS NULL " +
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN sl.isapproved IS NULL THEN 0 " +
                        "  WHEN sl.isapproved = '0' THEN 1 " +
                        "  WHEN sl.isapproved = '1' THEN 2 " +
                        "END, " +
                        "sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getDStudentLeaveApplications();

        @Query(value = "SELECT * FROM t_studentleave sl " +
                        "WHERE (sl.iswardenapproved IS NULL OR sl.iswardenapproved = '0') " +
                        "AND sl.isdeanapproved IS NULL " +
                        "AND sl.isapproved IS NULL " +
                        "ORDER BY " +
                        "CASE " +
                        "  WHEN sl.isapproved IS NULL THEN 0 " +
                        "  WHEN sl.isapproved = '0' THEN 1 " +
                        "  WHEN sl.isapproved = '1' THEN 2 " +
                        "END, " +
                        "sl.applicationdate DESC", nativeQuery = true)
        List<T_StudentLeave> getCWStudentLeaveApplications();

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sp.sphasename "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_shortterm_phases sp ON s.sphaseid = sp.sphaseid " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend", nativeQuery = true)
        List<Object[]> findStudentLeaveDetailsByCourseAndDateRange(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sp.sphasename "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "JOIN m_shortterm_phases sp ON s.sphaseid = sp.sphaseid " + // JOIN is appropriate if sphaseid
                                                                                    // is a filter
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sp.sphaseid = :sphaseid", nativeQuery = true)
        List<Object[]> findStudentLeaveDetailsByCourseDateAndPhase(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend,
                        @Param("sphaseid") String sphaseid);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sp.sphasename "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_shortterm_phases sp ON s.sphaseid = sp.sphaseid " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='1'", nativeQuery = true)
        List<Object[]> findApprovedStudentLeavesByCourseAndDateRange(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sp.sphasename "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_shortterm_phases sp ON s.sphaseid = sp.sphaseid " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='0'", nativeQuery = true)
        List<Object[]> findNotApprovedStudentLeavesByCourseAndDateRange(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        // Long Term Leave Queries

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sem.semestercode "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_semesters sem ON s.semestercode = sem.semestercode " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend", nativeQuery = true)
        List<Object[]> findStudentLeavesWithSemesterByCourseAndDateRange(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sem.semestercode "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "JOIN m_semesters sem ON s.semestercode = sem.semestercode " + // JOIN is appropriate if
                                                                                       // semester is a filter
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sem.semestercode = :semester", nativeQuery = true)
        List<Object[]> findStudentLeavesByCourseDateAndSemester(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend,
                        @Param("semester") String semester);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sem.semestercode "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_semesters sem ON s.semestercode = sem.semestercode " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='1'", nativeQuery = true)
        List<Object[]> findApprovedStudentLeavesByCourseDateAndSemester(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason, sem.semestercode "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "JOIN m_course_academics ca ON s.coursecode = ca.coursecode " +
                        "LEFT JOIN m_semesters sem ON s.semestercode = sem.semestercode " +
                        "WHERE ca.coursecode = :course AND sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='0'", nativeQuery = true)
        List<Object[]> findUnapprovedStudentLeavesByCourseAndDate(
                        @Param("course") String course,
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        // All Leave Queries

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " +
                        "WHERE sl.applicationdate BETWEEN :fystart AND :fyend", nativeQuery = true)
        List<Object[]> findStudentLeavesByDateRange(
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " + // JOIN with t_students
                        "WHERE sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='1'", nativeQuery = true)
        List<Object[]> findApprovedStudentLeavesByDateRange(
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT sl.requestedfrom, sl.requestedto, sl.reasonforleave, sl.leavestation, " +
                        "sl.isapproved, sl.applicationdate, s.rollno, s.fname, s.lname, sl.studentleaveid, sl.rejectionreason "
                        +
                        "FROM t_studentleave sl " +
                        "JOIN t_students s ON sl.studentid = s.studentid " + // JOIN with t_students
                        "WHERE sl.applicationdate BETWEEN :fystart AND :fyend " +
                        "AND sl.isapproved ='0'", nativeQuery = true)
        List<Object[]> findUnapprovedStudentLeavesByDateRange(
                        @Param("fystart") Date fystart,
                        @Param("fyend") Date fyend);

        @Query(value = "SELECT * FROM T_StudentLeave WHERE studentleaveid = :fid", nativeQuery = true)
        Optional<T_StudentLeave> findStudentLeaveById(@Param("fid") String fid);

        @Query("SELECT MAX(CAST(sl.studentleaveid as int)) FROM T_StudentLeave sl")
        Integer findLastUsedStudentleaveid();

        @Query(value = "SELECT * FROM t_studentleave l " +
                       "WHERE l.studentid = :studentid " +
                       "AND ((:isshortterm = '1' AND l.sphaseid = :semPhaseId) " +
                       "OR (:isshortterm <> '1' AND l.coursecode = :semPhaseId)) " +
                       "ORDER BY l.applicationdate DESC ", nativeQuery = true)
        List<T_StudentLeave> getOwnLeaveApplicationsCurrentSem(@Param("studentid") String studentid, @Param("isshortterm") String isshortterm, @Param("semPhaseId") String semPhaseId);

        @Query(
                value = "SELECT COUNT(*) " +
                        "FROM ( " +
                        "   SELECT gs.day " +
                        "   FROM t_studentleave l " +
                        "   JOIN LATERAL generate_series(l.requestedfrom, l.requestedto, INTERVAL '1 day') AS gs(day) ON true " +
                        "   WHERE l.studentid = :studentid " +
                        "     AND ( " +
                        "          (:isshortterm = '1' AND l.sphaseid = :semPhaseId) " +
                        "       OR (:isshortterm <> '1' AND l.semestercode = :semPhaseId) " +
                        "     ) " +
                        ") alldays " +
                        "WHERE EXTRACT(DOW FROM alldays.day) NOT IN (0, 6) " +
                        "  AND alldays.day NOT IN (SELECT holidaydate FROM m_holidays)",
                nativeQuery = true
        )
        Long countLeaveDaysWithoutWeekends(@Param("studentid") String studentid,
                                           @Param("isshortterm") String isshortterm,
                                           @Param("semPhaseId") String semPhaseId);

        @Modifying
        @Transactional
        @Query(value = "DELETE FROM nerie.t_studentleave WHERE studentleaveid = :studentleaveid", nativeQuery = true)
        int deleteByStudentleaveid(@Param("studentleaveid") String studentleaveid);

}
