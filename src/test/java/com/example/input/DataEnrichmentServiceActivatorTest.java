package com.example.input;

import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.*;

public class DataEnrichmentServiceActivatorTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    DataEnrichmentServiceActivator dataEnrichmentServiceActivator;

    @Mock
    EnrichedDataRepository enrichedDataRepository;

    @Mock
    RestOperations restOperations;

    @Test
    public void sendValidJson_shouldReturnEnrichedData() {
        // assemble
        Mockito.when(restOperations.getForEntity(Matchers.anyString(), Matchers.any(Class.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("enrichedData", "enrichedData".getBytes())));

        // act
        byte[] output = dataEnrichmentServiceActivator.enrichData(null, null);

        //assert
        Assertions.assertThat(output).isEqualTo("enrichedData".getBytes());
    }

    @Test
    public void sendValidJson_shouldSaveEnrichedDataAndUniqueId() {
        // assemble
        ArgumentCaptor<EnrichedData> dataArgumentCaptor = ArgumentCaptor.forClass(EnrichedData.class);
        Mockito.when(enrichedDataRepository.save(dataArgumentCaptor.capture()))
                .thenReturn(new EnrichedData());

        Mockito.when(restOperations.getForEntity(Matchers.anyString(), Matchers.any(Class.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("enrichedData", "enrichedData".getBytes())));

        // act
        UUID uuid = UUID.randomUUID();
        dataEnrichmentServiceActivator.enrichData(null, uuid);

        //assert
        EnrichedData savedData = dataArgumentCaptor.getValue();
        Assertions.assertThat(savedData.getEnrichment()).isEqualTo("enrichedData".getBytes());
        Assertions.assertThat(savedData.getUuid()).isEqualTo(uuid);
    }
}
