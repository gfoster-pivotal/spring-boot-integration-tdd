package com.example.input;

import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.UUID;

public class DataEnrichmentServiceActivator {

    private final EnrichedDataRepository enrichedDataRepository;
    private final RestOperations restOperations;

    public DataEnrichmentServiceActivator(EnrichedDataRepository enrichedDataRepository, RestOperations restOperations) {
        this.enrichedDataRepository = enrichedDataRepository;
        this.restOperations = restOperations;
    }

    public byte[] enrichData(byte[] bytes, UUID uuid) {
        ResponseEntity<Map> responseEntity = restOperations.getForEntity("/someUrl", Map.class);
        Map<String, Object> body = responseEntity.getBody();
        byte[] enrichedDatum = (byte[]) body.get("enrichedData");

        EnrichedData enrichedData = new EnrichedData();
        enrichedData.setEnrichment(enrichedDatum);
        enrichedData.setUuid(uuid);
        enrichedDataRepository.save(enrichedData);

        return enrichedDatum;
    }
}
