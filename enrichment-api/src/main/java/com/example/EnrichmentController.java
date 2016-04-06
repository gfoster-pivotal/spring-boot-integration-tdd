package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrichmentController {

    @RequestMapping("/datum/enrichment")
    public ResponseEntity<EnrichedData> getEnrichment() {
        return ResponseEntity.ok(new EnrichedData("sample enrichment".getBytes()));
    }
}
