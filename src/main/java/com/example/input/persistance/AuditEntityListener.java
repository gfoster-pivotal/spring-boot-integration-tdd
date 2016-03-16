package com.example.input.persistance;

import javax.persistence.PrePersist;
import java.time.ZonedDateTime;
import java.util.UUID;

public class AuditEntityListener {
    @PrePersist
    public void auditPrePersist(Data data) {
        data.setZonedDateTime(ZonedDateTime.now());
        data.setUniqueDataId(UUID.randomUUID().toString());
    }
}
