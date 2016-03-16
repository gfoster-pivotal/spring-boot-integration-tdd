package com.example.input.persistance;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditEntityListener.class)
public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private byte[] originalData;
    private String source;
    private String uniqueDataId;
    private ZonedDateTime zonedDateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getOriginalData() {
        return originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUniqueDataId() {
        return uniqueDataId;
    }

    public void setUniqueDataId(String uniqueDataId) {
        this.uniqueDataId = uniqueDataId;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }
}
