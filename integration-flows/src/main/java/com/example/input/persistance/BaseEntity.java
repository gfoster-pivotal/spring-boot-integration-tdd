package com.example.input.persistance;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private String uniqueDataId;
    private ZonedDateTime zonedDateTime;

    public final long getId() {
        return id;
    }

    public final String getUniqueDataId() {
        return uniqueDataId;
    }

    public final ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final void setUniqueDataId(String uniqueDataId) {
        this.uniqueDataId = uniqueDataId;
    }

    public final void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
