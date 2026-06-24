package com.nic.nerie.mt_venuerooms.model;

import com.nic.nerie.m_venues.model.M_Venues;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "mt_venuerooms")
public class MT_VenueRooms implements Serializable {

    @Id
    private String roomcode;
    private String roomname;
    private int capacity;
    @ManyToOne
    @JoinColumn(name = "venuecode")
    public M_Venues mvenues;

    public MT_VenueRooms(String roomcode, String roomname, int capacity, M_Venues mvenues) {
        this.roomcode = roomcode;
        this.roomname = roomname;
        this.capacity = capacity;
        this.mvenues = mvenues;
    }

    public MT_VenueRooms() {
    }

    public String getRoomcode() {
        return roomcode;
    }

    public void setRoomcode(String roomcode) {
        this.roomcode = roomcode;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public M_Venues getMvenues() {
        return mvenues;
    }

    public void setMvenues(M_Venues mvenues) {
        this.mvenues = mvenues;
    }

}

