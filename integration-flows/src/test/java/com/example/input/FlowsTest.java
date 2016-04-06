package com.example.input;

import com.example.DemoApplication;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@SpringApplicationConfiguration(classes = DemoApplication.class)
public class FlowsTest {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    ApplicationContext applicationContext;

//    @Test
//    public void sendInvalidJson_returnInvalidReponse() {
//        MessageChannel messageChannel = applicationContext.getBean("api.input", MessageChannel.class);
//    }
}