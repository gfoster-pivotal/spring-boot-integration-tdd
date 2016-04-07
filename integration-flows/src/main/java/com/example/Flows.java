package com.example;

import com.example.input.DataEnrichmentServiceActivator;
import com.example.input.InvalidDataFormatException;
import com.example.input.persistance.Data;
import com.example.input.persistance.DataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

@Configuration
public class Flows {

    @Bean
    public MessageChannel messageChannel() {
        return MessageChannels
                .direct("api.input")
                .get();
    }

    @Bean
    public IntegrationFlow orders(DataRepository dataRepository, DataEnrichmentServiceActivator dataEnrichmentServiceActivator) {
        return IntegrationFlows.from("api.input")
                .channel(MessageChannels.executor(Executors.newCachedThreadPool()))
                .publishSubscribeChannel(publishSubscribeSpec -> publishSubscribeSpec
                        .subscribe(flow -> flow
                                .enrichHeaders((Consumer<HeaderEnricherSpec>) headerEnricherSpec -> headerEnricherSpec.header("X-UUID", UUID.randomUUID()))
                                .filter(new GenericSelector<byte[]>() {
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
                                })
                                .handle(new GenericHandler<byte[]>() {
                                    @Override
                                    public Object handle(byte[] payload, Map<String, Object> headers) {
                                        UUID uuid = (UUID) headers.get("X-UUID");
                                        return dataEnrichmentServiceActivator.enrichData(payload, uuid);
                                    }
                                })
                                .transform(new GenericTransformer<byte[], String>() {
                                    @Override
                                    public String transform(byte[] source) {
                                        return new String(source);
                                    }
                                })
                                .channel("gateway.output"))
                        .subscribe(flow -> flow.handle((MessageHandler) message -> {
                            Data data = new Data();
                            data.setOriginalData((byte[]) message.getPayload());
                            data.setSource("web");
                            dataRepository.save(data);
                        })))
                .get();
    }
}
