package com.example.input;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface InputGateway {
    @Gateway(requestChannel = "api.input", replyChannel = "gateway.output")
    String digest(byte[] bytes);
}
