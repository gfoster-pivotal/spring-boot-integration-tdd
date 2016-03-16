package com.example.input;

import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.Optional;

public class DataEnrichmentServiceActivator {

    private final EnrichedDataRepository enrichedDataRepository;
    private final RestOperations restOperations;

    public DataEnrichmentServiceActivator(EnrichedDataRepository enrichedDataRepository, RestOperations restOperations) {
        this.enrichedDataRepository = enrichedDataRepository;
        this.restOperations = restOperations;
    }

    public Optional<String> enrichData(byte[] bytes) {
        // Make enrichment
        ResponseEntity<Map> responseEntity = restOperations.getForEntity("/someUrl", Map.class);
        byte[] enrichedDatum = responseEntity.getBody().get("enrichedData").toString().getBytes();

        // set response and save element
        EnrichedData enrichedData = new EnrichedData();
        enrichedData.setEnrichment(enrichedDatum);

        this.enrichedDataRepository.save(enrichedData);
        return Optional.of("123456789");
    }
}
