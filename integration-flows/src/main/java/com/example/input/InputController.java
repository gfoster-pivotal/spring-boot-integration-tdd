package com.example.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class InputController {
    private static final Logger logger = LoggerFactory.getLogger(InputController.class);
    private final InputGateway inputGateway;

    @Autowired
    public InputController(InputGateway inputGateway) {
        this.inputGateway = inputGateway;
    }

    @RequestMapping(path = "/input", method = RequestMethod.POST)
    public ResponseEntity<Void> receiveData(@RequestBody byte[] bytes) throws URISyntaxException {
        try {
            String digest = inputGateway.digest(bytes);
            return ResponseEntity
                    .created(new URI(""))
                    .header("X-UUID", digest)
                    .build();

        } catch (InvalidDataFormatException e) {
            logger.error("Bad data format: [{}]", new String(bytes));
            return ResponseEntity
                    .badRequest()
                    .build();
        }
    }
}