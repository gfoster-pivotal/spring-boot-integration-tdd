package com.example.input.persistance;

import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class EnrichedData extends BaseEntity {
    private byte[] enrichment;
    private UUID uuid;

    public byte[] getEnrichment() {
        return enrichment;
    }

    public void setEnrichment(byte[] enrichment) {
        this.enrichment = enrichment;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
