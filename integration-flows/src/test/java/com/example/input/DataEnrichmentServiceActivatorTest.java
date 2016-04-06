package com.example.input;

import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class DataEnrichmentServiceActivatorTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    EnrichedDataRepository enrichedDataRepository;

    @Spy
    RestTemplate restTemplate = new RestTemplate();

    DataEnrichmentServiceActivator dataEnrichmentServiceActivator;

    @Before
    public void setupClassUnderTest() {
        dataEnrichmentServiceActivator = new DataEnrichmentServiceActivator(enrichedDataRepository, restTemplate, "http://test.com");
    }

    @Before
    public void setupMockRestServiceServer() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> value = new HashMap<>();
        value.put("enrichedData","enrichedData");
        String string = objectMapper.writeValueAsString(value);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(dataEnrichmentServiceActivator.getRestTemplate());
        mockServer.expect(requestTo("http://test.com"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));
    }

    @Test
    public void sendValidJson_shouldReturnEnrichedData() {
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

        // act
        UUID uuid = UUID.randomUUID();
        dataEnrichmentServiceActivator.enrichData(null, uuid);

        //assert
        EnrichedData savedData = dataArgumentCaptor.getValue();
        Assertions.assertThat(savedData.getEnrichment()).isEqualTo("enrichedData".getBytes());
        Assertions.assertThat(savedData.getUuid()).isEqualTo(uuid);
    }

    @Test
    public void sendValidJson_shouldQueryTheCorrentEndpointAndParseValidResponse() {
        // act
        dataEnrichmentServiceActivator.enrichData(null, null);

        //assert
        Mockito.verify(restTemplate).getForEntity("http://test.com", Map.class);
    }
}
