package com.nic.nerie.audittrail.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "audittrail")
public class Audittrail implements Serializable {

    @EmbeddedId
    Audittrail_Id id;

    public Audittrail() {
    }

    public Audittrail(Audittrail_Id id) {
        this.id = id;
    }

    public Audittrail_Id getId() {
        return id;
    }

    public void setId(Audittrail_Id id) {
        this.id = id;
    }

}
