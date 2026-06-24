package com.nic.nerie.t_programtimetable.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_venuerooms.model.MT_VenueRooms;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "t_programtimetable")
public class T_ProgramTimeTable implements Serializable {

    @Id
    private String programtimetablecode;

    @Temporal(TemporalType.DATE)
    private Date programdate;

    private Short programday;

    private String subject;

    private LocalTime starttime;
    private LocalTime endtime;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    @ManyToOne
    @JoinColumn(name = "roomcode")
    public MT_VenueRooms mtvenuerooms;

    @Temporal(TemporalType.TIMESTAMP)
    private Date entrydate;

    public T_ProgramTimeTable() {
    }

    public T_ProgramTimeTable(String programtimetablecode, Date programdate, Short programday, String subject,
                              LocalTime starttime, LocalTime endtime, M_Phases phaseid, MT_Userlogin mtuserlogin,
                              MT_VenueRooms mtvenuerooms, Date entrydate) {
        this.programtimetablecode = programtimetablecode;
        this.programdate = programdate;
        this.programday = programday;
        this.subject = subject;
        this.starttime = starttime;
        this.endtime = endtime;
        this.phaseid = phaseid;
        this.mtuserlogin = mtuserlogin;
        this.mtvenuerooms = mtvenuerooms;
        this.entrydate = entrydate;
    }

    public String getProgramtimetablecode() {
        return programtimetablecode;
    }

    public void setProgramtimetablecode(String programtimetablecode) {
        this.programtimetablecode = programtimetablecode;
    }

    public Date getProgramdate() {
        return programdate;
    }

    public void setProgramdate(Date programdate) {
        this.programdate = programdate;
    }

    public Short getProgramday() {
        return programday;
    }

    public void setProgramday(Short programday) {
        this.programday = programday;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalTime getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalTime starttime) {
        this.starttime = starttime;
    }

    public LocalTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalTime endtime) {
        this.endtime = endtime;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    public MT_VenueRooms getMtvenuerooms() {
        return mtvenuerooms;
    }

    public void setMtvenuerooms(MT_VenueRooms mtvenuerooms) {
        this.mtvenuerooms = mtvenuerooms;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    @Override
    public String toString() {
        return "T_ProgramTimeTable{" +
                "programtimetablecode='" + programtimetablecode + '\'' +
                ", programdate=" + programdate +
                ", programday=" + programday +
                ", subject='" + subject + '\'' +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", phaseid=" + phaseid +
                ", mtuserlogin=" + mtuserlogin +
                ", mtvenuerooms=" + mtvenuerooms +
                ", entrydate=" + entrydate +
                '}';
    }
}
