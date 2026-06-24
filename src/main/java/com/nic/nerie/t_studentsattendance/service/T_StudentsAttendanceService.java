package com.nic.nerie.t_studentsattendance.service;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_studentsattendance.model.T_StudentsAttendance;
import com.nic.nerie.t_studentsattendance.repository.T_StudentsAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class T_StudentsAttendanceService {
    private final T_StudentsAttendanceRepository tStudentsAttendanceRepository;

    @Autowired
    public T_StudentsAttendanceService(T_StudentsAttendanceRepository tStudentsAttendanceRepository) {
        this.tStudentsAttendanceRepository = tStudentsAttendanceRepository;
    }

    public List<Object[]> getStudentAttendanceList(String usercode, String subjectcode, String month) {
        return tStudentsAttendanceRepository.getStudentAttendanceList(usercode, subjectcode, month);
    }

    public List<String> getTimeList() {
        return tStudentsAttendanceRepository.getTimeList();
    }

    @Transactional
    public String saveOrUpdateStudentAttendance(T_Students student, M_Subjects subject, String attendanceStatus,
                                                Date attendanceDate, Date startTime, Date endTime, MT_Userlogin user) {
        try {
            Optional<T_StudentsAttendance> existingAttendanceOpt = tStudentsAttendanceRepository.findByUniqueKeysNative(
                    student.getStudentid(),
                    subject.getSubjectcode(),
                    attendanceDate,
                    startTime
            );

            T_StudentsAttendance attendanceToSave;

            if (existingAttendanceOpt.isPresent()) {
                attendanceToSave = existingAttendanceOpt.get();

                attendanceToSave.setAttendancestatus(attendanceStatus);
                attendanceToSave.setEndtime(endTime);
                attendanceToSave.setEntrydate(new Date());
                attendanceToSave.setUsercode(user);
            } else {
                attendanceToSave = new T_StudentsAttendance();

                Integer nextId = tStudentsAttendanceRepository.getNextAttendanceId();

                attendanceToSave.setStudentattendanceid(String.valueOf(nextId + 1));
                if(student.getSemestercode() != null)
                    attendanceToSave.setAttsemestercode(student.getSemestercode());
                else
                    attendanceToSave.setAttsphaseid(student.getSphaseid());

                attendanceToSave.setStudentid(student);
                attendanceToSave.setSubjectcode(subject);
                attendanceToSave.setAttendancestatus(attendanceStatus);
                attendanceToSave.setAttendancedate(attendanceDate);
                attendanceToSave.setStarttime(startTime);
                attendanceToSave.setEndtime(endTime);
                attendanceToSave.setEntrydate(new Date());
                attendanceToSave.setUsercode(user);

                if(student.getSemestercode() != null){
                    //attendanceToSave.setAttsemestercode(student.getSemestercode());
                }
                else{
                    //attendanceToSave.setAttsphaseid(student.getSphaseid());
                }
            }

            tStudentsAttendanceRepository.save(attendanceToSave);
            return "1"; // Return "1" to indicate success

        } catch (Exception e) {
            return "-1"; // Return "-1" to indicate failure
        }
    }

    // public List<Object[]> getStudentAttendanceDetails(String usercode, String subjectcode, String month, String time) {
    //     return tStudentsAttendanceRepository.getStudentAttendanceDetails(usercode, subjectcode, month, time);
    // }

    public List<Object[]> getStudentAttendanceDetails(String usercode, String subjectcode, Integer month, Integer year, String sem) {
         String semType = sem.startsWith("S") ? "S" : "P";
         String semCode = sem.substring(1); // S3 → 3, P2 → 2
        return tStudentsAttendanceRepository.getStudentAttendanceDetails(usercode, subjectcode, month, year, semType, semCode);
    }

}
