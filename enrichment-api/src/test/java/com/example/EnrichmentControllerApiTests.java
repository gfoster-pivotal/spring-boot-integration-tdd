package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringApplicationConfiguration(classes = EnrichmentApiApplication.class)
public class EnrichmentControllerApiTests {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    EnrichmentController enrichmentController;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(enrichmentController)
                .build();
    }

    @Test
    public void makeAnEnrichmentRequest_returns200AndEnrichedData() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/");
        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(status().isOk());
        String response = new ObjectMapper().writeValueAsString(new EnrichedData("sample enrichment".getBytes()));
        resultActions.andExpect(content().json(response));
    }
}
