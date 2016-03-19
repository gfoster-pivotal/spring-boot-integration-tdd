package com.example.input.persistance;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
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
