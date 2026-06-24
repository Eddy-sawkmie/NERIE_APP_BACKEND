package com.nic.nerie.m_holidays.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "m_holidays")
public class M_Holidays {
    @Id
    @Temporal(TemporalType.DATE)
    private Date holidaydate;

    private String holidayreason;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    public M_Holidays() {
    }

    public M_Holidays(Date holidaydate, String holidayreason, MT_Userlogin mtuserlogin) {
        this.holidaydate = holidaydate;
        this.holidayreason = holidayreason;
        this.mtuserlogin = mtuserlogin;
    }

    public Date getHolidaydate() {
        return holidaydate;
    }

    public void setHolidaydate(Date holidaydate) {
        this.holidaydate = holidaydate;
    }

    public String getHolidayreason() {
        return holidayreason;
    }

    public void setHolidayreason(String holidayreason) {
        this.holidayreason = holidayreason;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }
}
