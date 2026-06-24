package com.nic.nerie.m_participantofficetypes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "m_participantofficetypes")
public class M_ParticipantOfficeTypes implements Serializable {

    @Id
    private String participantofficetypecode;
    private String participantofficetypename;

    public M_ParticipantOfficeTypes() {
    }

    public M_ParticipantOfficeTypes(String participantofficetypecode, String participantofficetypename) {
        this.participantofficetypecode = participantofficetypecode;
        this.participantofficetypename = participantofficetypename;
    }

    public String getParticipantofficetypecode() {
        return participantofficetypecode;
    }

    public void setParticipantofficetypecode(String participantofficetypecode) {
        this.participantofficetypecode = participantofficetypecode;
    }

    public String getParticipantofficetypename() {
        return participantofficetypename;
    }

    public void setParticipantofficetypename(String participantofficetypename) {
        this.participantofficetypename = participantofficetypename;
    }

}

