package com.example;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class EnrichmentControllerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    EnrichmentController enrichmentController;

    @Test
    public void enrichData() {
        ResponseEntity<EnrichedData> responseEntity = enrichmentController.getEnrichment();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getEnrichedData()).isEqualTo("sample enrichment".getBytes());
    }
}