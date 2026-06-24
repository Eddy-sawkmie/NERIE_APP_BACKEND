package com.nic.nerie.t_participantattendance.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.t_applications.model.T_Applications;
import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class T_P_Attendance_Id implements Serializable {

    @ManyToOne
    @JoinColumn(name = "applicationcode")
    public T_Applications tapplications;
    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "programtimetablecode")
    public T_ProgramTimeTable programtimetablecode;

    public T_P_Attendance_Id() {
    }

    public T_P_Attendance_Id(T_Applications tapplications, M_Phases phaseid, T_ProgramTimeTable programtimetablecode) {
        this.tapplications = tapplications;
        this.phaseid = phaseid;
        this.programtimetablecode = programtimetablecode;
    }

    public T_Applications getTapplications() {
        return tapplications;
    }

    public void setTapplications(T_Applications tapplications) {
        this.tapplications = tapplications;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    public T_ProgramTimeTable getProgramtimetablecode() {
        return programtimetablecode;
    }

    public void setProgramtimetablecode(T_ProgramTimeTable programtimetablecode) {
        this.programtimetablecode = programtimetablecode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check for same instance
        if (o == null || getClass() != o.getClass()) return false; // Check for null and different class

        T_P_Attendance_Id that = (T_P_Attendance_Id) o; // Cast to the correct type

        return Objects.equals(tapplications != null ? tapplications.getApplicationcode() : null, that.tapplications != null ? that.tapplications.getApplicationcode() : null) &&
                Objects.equals(phaseid != null ? phaseid.getPhaseid() : null, that.phaseid != null ? that.phaseid.getPhaseid() : null) &&
                Objects.equals(programtimetablecode != null ? programtimetablecode.getProgramtimetablecode() : null, that.programtimetablecode != null ? that.programtimetablecode.getProgramtimetablecode() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                tapplications != null ? tapplications.getApplicationcode() : null,
                phaseid != null ? phaseid.getPhaseid() : null,
                programtimetablecode != null ? programtimetablecode.getProgramtimetablecode() : null
        );
    }

    @Override
    public String toString() {
        String appCode = (tapplications != null) ? String.valueOf(tapplications.getApplicationcode()) : "null";
        String phId = (phaseid != null) ? String.valueOf(phaseid.getPhaseid()) : "null";
        String pttCode = (programtimetablecode != null) ? String.valueOf(programtimetablecode.getProgramtimetablecode()) : "null";

        return "T_P_Attendance_Id{" +
                "applicationcode=" + appCode +
                ", phaseid=" + phId +
                ", programtimetablecode=" + pttCode +
                '}';
    }
}

