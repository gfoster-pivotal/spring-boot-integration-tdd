package com.example.input.persistance;

import javax.persistence.Entity;

@Entity
public class Data extends BaseEntity {
    private byte[] originalData;
    private String source;

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
}
