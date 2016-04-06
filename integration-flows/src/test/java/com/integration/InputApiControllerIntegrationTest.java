package com.integration;

import com.example.DemoApplication;
import com.example.input.InputController;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = {DemoApplication.class, InputApiControllerIntegrationTest.FlowTestConfiguration.class})
public class InputApiControllerIntegrationTest {
    @Configuration
    public static class FlowTestConfiguration implements BeanClassLoaderAware {
        private ClassLoader classLoader;

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
    }

    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    InputController inputController;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(inputController)
                .build();
    }

    @Test
    public void sendDataRequest_shouldMakeACallToTheEnrichmentServer() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/input")
                .content("{\"input:\":\"test\"}");
        ResultActions resultActions = mockMvc.perform(request);

        String uniqueId = resultActions.andReturn().getResponse().getHeader("X-UUID");
        assertThat(uniqueId).isNotEmpty();
    }
}
