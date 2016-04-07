package com.example.input;

import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
public class DataEnrichmentServiceActivator {

    private final EnrichedDataRepository enrichedDataRepository;
    private final RestTemplate restTemplate;
    private final String enrichmentHostUrl;

    @Autowired
    public DataEnrichmentServiceActivator(EnrichedDataRepository enrichedDataRepository,
                                          RestTemplate restTemplate,
                                          @Value("${enrichment.server.url}") String enrichmentHostUrl) {
        this.enrichedDataRepository = enrichedDataRepository;
        this.restTemplate = restTemplate;
        this.enrichmentHostUrl = enrichmentHostUrl;
    }

    public byte[] enrichData(byte[] bytes, UUID uuid) {
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(enrichmentHostUrl, Map.class);
        Map<String, Object> body = responseEntity.getBody();
        String data = (String) body.get("enrichedData");
        byte[] enrichedDatum = data.getBytes();

        EnrichedData enrichedData = new EnrichedData();
        enrichedData.setEnrichment(enrichedDatum);
        enrichedData.setUuid(uuid);
        enrichedDataRepository.save(enrichedData);

        return enrichedDatum;
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
