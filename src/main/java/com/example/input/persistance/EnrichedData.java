package com.example.input.persistance;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@EntityListeners(AuditEntityListener.class)
public class EnrichedData {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    private String uniqueDataId;
    private ZonedDateTime zonedDateTime;
    private byte[] enrichment;

    public byte[] getEnrichment() {
        return enrichment;
    }

    public void setEnrichment(byte[] enrichment) {
        this.enrichment = enrichment;
    }
}
