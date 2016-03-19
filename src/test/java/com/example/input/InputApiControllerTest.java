package com.example.input;

import com.example.DemoApplication;
import com.example.input.persistance.Data;
import com.example.input.persistance.DataRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;

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

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(inputController)
                .build();
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
    public void sendDataRequest_shouldValidateData_BadRequestResponse() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content("test");
        ResultActions resultActions = mockMvc.perform(request);

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void sendDataRequest_shouldPersistData() throws Exception {
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
