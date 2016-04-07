package com.example.input;

import com.example.DemoApplication;
import com.example.input.persistance.Data;
import com.example.input.persistance.DataRepository;
import com.example.input.persistance.EnrichedData;
import com.example.input.persistance.EnrichedDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//@WebAppConfiguration
@SpringApplicationConfiguration(classes = DemoApplication.class)
public class InputApiControllerTest {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    InputController inputController;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private EnrichedDataRepository enrichedDataRepository;

    @Autowired
    DataEnrichmentServiceActivator dataEnrichmentServiceActivator;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(inputController)
                .build();
    }

    @Before
    public void setupMockServer() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> value = new HashMap<>();
        value.put("enrichedData","enrichedData");
        String string = objectMapper.writeValueAsString(value);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(dataEnrichmentServiceActivator.getRestTemplate());
        mockServer.expect(requestTo("http://localhost:8081"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(string, MediaType.APPLICATION_JSON));
    }

    @Test
    public void sendDataRequest_shouldAcceptAPostRequest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content("{\"input:\":\"test\"}");
        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void sendDataRequest_shouldGoThroughTheFlow_ReturnAUniqueIdInACustomHeader() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content("{\"input:\":\"test\"}");
        ResultActions resultActions = mockMvc.perform(request);

        String uniqueId = resultActions.andReturn().getResponse().getHeader("X-UUID");
        assertThat(uniqueId).isNotEmpty();
    }

    @Test
    public void sendDataRequest_shouldPersistEnrichedData() throws Exception {
        byte[] content = "{\"input:\":\"test\"}".getBytes();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content(content);
        mockMvc.perform(request);

        EnrichedData data = enrichedDataRepository.findAll().remove(0);
        assertThat(data.getUuid()).isNotNull();
        assertThat(data.getEnrichment()).isNotNull();
    }

    @Test
    public void sendDataRequest_shouldValidateData_BadRequestResponse() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content("test");
        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void sendDataRequest_shouldPersistRawData() throws Exception {
        byte[] content = "{\"input:\":\"test\"}".getBytes();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content(content);
        mockMvc.perform(request);

        Data data = dataRepository.findAll().remove(0);
        assertThat(data.getSource()).isEqualTo("web");
        assertThat(data.getOriginalData()).isEqualTo(content);
    }
}
