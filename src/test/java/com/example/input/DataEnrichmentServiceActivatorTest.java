package com.example.input;

import com.example.input.persistance.Data;
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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
    public void sendValidJson_shouldReturnAValidString() {
        // assemble
        Mockito.when(restOperations.getForEntity(Matchers.anyString(), Matchers.any(Class.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("enrichedData", "")));

        // act
        byte[] bytes = "{\"input\":\"test\"}".getBytes();
        Optional<String> enrichedResponse = dataEnrichmentServiceActivator.enrichData(bytes);

        //assert
        Assertions.assertThat(enrichedResponse.get()).isEqualTo("123456789");
    }

    @Test
    public void sendValidJson_shouldSaveEnrichedData() {
        // assemble
        ArgumentCaptor<EnrichedData> dataArgumentCaptor = ArgumentCaptor.forClass(EnrichedData.class);
        Mockito.when(enrichedDataRepository.save(dataArgumentCaptor.capture()))
                .thenReturn(new EnrichedData());

        Mockito.when(restOperations.getForEntity(Matchers.anyString(), Matchers.any(Class.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonMap("enrichedData", "")));

        // act
        byte[] bytes = "{\"input\":\"test\"}".getBytes();
        dataEnrichmentServiceActivator.enrichData(bytes);

        //assert
        EnrichedData savedData = dataArgumentCaptor.getValue();
        Assertions.assertThat(savedData.getEnrichment()).isEqualTo("".getBytes());
    }
}
