package com.example;

import com.example.input.InvalidDataFormatException;
import com.example.input.persistance.Data;
import com.example.input.persistance.DataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.EnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.dsl.support.Function;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@Configuration
public class Flows {

    @Bean
    public MessageChannel messageChannel() {
        return MessageChannels.direct("api.input").get();
    }

    @Bean
    public IntegrationFlow orders(DataRepository dataRepository) {
        return IntegrationFlows.from("api.input")
                .channel(MessageChannels.executor(Executors.newCachedThreadPool()))
                .publishSubscribeChannel(publishSubscribeSpec -> {
                    publishSubscribeSpec.subscribe(
                            flow -> {
                                flow.filter(new GenericSelector<byte[]>() {
                                    ObjectMapper objectMapper = new ObjectMapper();

                                    @Override
                                    public boolean accept(byte[] source) {
                                        try {
                                            objectMapper.readTree(source);
                                        } catch (IOException e) {
                                            throw new InvalidDataFormatException();
                                        }
                                        return true;
                                    }
                                });

                                flow.transform(new GenericTransformer<byte[], Map<String, Object>>() {
                                    @Override
                                    public Map<String, Object> transform(byte[] source) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("input", source);
                                        map.put("uuid", UUID.randomUUID());
                                        return map;
                                    }
                                });

                                flow.transform(new GenericTransformer<Map<String, Object>, String>() {
                                    ObjectMapper objectMapper = new ObjectMapper();

                                    @Override
                                    public String transform(Map<String, Object> source) {
                                        try {
                                            return objectMapper.writeValueAsString(source);
                                        } catch (JsonProcessingException e) {
                                        }
                                        return null;
                                    }
                                });

                                flow.channel("gateway.output");
                            }).subscribe(
                            flow ->
                                    flow.handle((MessageHandler) message -> {
                                        Data data = new Data();
                                        data.setOriginalData((byte[]) message.getPayload());
                                        data.setSource("web");
                                        dataRepository.save(data);
                                    }));

                })
                .get();
    }
}
