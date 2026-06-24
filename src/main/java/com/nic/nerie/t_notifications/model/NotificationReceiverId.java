package com.nic.nerie.t_notifications.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationReceiverId implements Serializable {

    private String notificationId;
    private String receiverUserCode;

    public NotificationReceiverId() {}

    public NotificationReceiverId(String notificationId, String receiverUserCode) {
        this.notificationId = notificationId;
        this.receiverUserCode = receiverUserCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((notificationId == null) ? 0 : notificationId.hashCode());
        result = prime * result + ((receiverUserCode == null) ? 0 : receiverUserCode.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NotificationReceiverId other = (NotificationReceiverId) obj;
        if (notificationId == null) {
            if (other.notificationId != null)
                return false;
        } else if (!notificationId.equals(other.notificationId))
            return false;
        if (receiverUserCode == null) {
            if (other.receiverUserCode != null)
                return false;
        } else if (!receiverUserCode.equals(other.receiverUserCode))
            return false;
        return true;
    }

    // equals() and hashCode() required
    
}
