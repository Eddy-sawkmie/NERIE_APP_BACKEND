package com.nic.nerie.t_studentsattendance.model;

import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_students.model.T_Students;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_studentsattendance")
public class T_StudentsAttendance {

    @Id
    private String studentattendanceid;

    private String attendancestatus;

    @ManyToOne
    @JoinColumn(name = "studentid")
    public T_Students studentid;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;

    @ManyToOne
    @JoinColumn(name="usercode")
    public MT_Userlogin usercode;

    @Temporal(TemporalType.DATE)
    private Date attendancedate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date entrydate;

    @Temporal(TemporalType.TIME)
    private Date starttime;

    @Temporal(TemporalType.TIME)
    private Date endtime;

    @ManyToOne
    @JoinColumn(name = "attsemestercode")
    public M_Semesters attsemestercode; //Current Semester of the Student at the time when the attendance is saved

    @ManyToOne
    @JoinColumn(name = "attsphaseid")
    public M_ShortTerm_Phases attsphaseid; //Current Phase of the Student at the time when the attendance is saved

    public T_StudentsAttendance() {
    }

    public T_StudentsAttendance(String studentattendanceid, String attendancestatus, T_Students studentid, M_Subjects subjectcode, MT_Userlogin usercode, Date attendancedate, Date entrydate, Date starttime, Date endtime) {
        this.studentattendanceid = studentattendanceid;
        this.attendancestatus = attendancestatus;
        this.studentid = studentid;
        this.subjectcode = subjectcode;
        this.usercode = usercode;
        this.attendancedate = attendancedate;
        this.entrydate = entrydate;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public String getStudentattendanceid() {
        return studentattendanceid;
    }

    public void setStudentattendanceid(String studentattendanceid) {
        this.studentattendanceid = studentattendanceid;
    }

    public String getAttendancestatus() {
        return attendancestatus;
    }

    public void setAttendancestatus(String attendancestatus) {
        this.attendancestatus = attendancestatus;
    }

    public T_Students getStudentid() {
        return studentid;
    }

    public void setStudentid(T_Students studentid) {
        this.studentid = studentid;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public Date getAttendancedate() {
        return attendancedate;
    }

    public void setAttendancedate(Date attendancedate) {
        this.attendancedate = attendancedate;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }


    @Override
    public String toString() {
        return "T_StudentAttendance{" + "studentattendanceid=" + studentattendanceid + ", attendancestatus=" + attendancestatus + ", studentid=" + studentid + ", subjectcode=" + subjectcode + ", usercode=" + usercode + ", attendancedate=" + attendancedate + ", entrydate=" + entrydate + ", starttime=" + starttime + ", endtime=" + endtime + '}';
    }

    public M_ShortTerm_Phases getAttsphaseid() {
        return attsphaseid;
    }

    public void setAttsphaseid(M_ShortTerm_Phases sphaseid) {
        this.attsphaseid = sphaseid;
    }

    public M_Semesters getAttsemestercode() {
        return attsemestercode;
    }

    public void setAttsemestercode(M_Semesters semestercode) {
        this.attsemestercode = semestercode;
    }

    
}
