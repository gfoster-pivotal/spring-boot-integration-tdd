package com.example.input;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class InputControllerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    InputController inputController;

    @Mock
    InputGateway inputGateway;

    @Test
    public void receiveData() throws URISyntaxException {
        ResponseEntity<Void> responseEntity = inputController.receiveData(new byte[0]);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(new URI(""));
    }

    @Test
    public void injectServiceInterface() throws URISyntaxException {
        // assemble
//        Mockito.when(inputGateway.digest(Matchers.anyObject())).thenReturn(null);
        Mockito.doNothing().when(inputGateway).digest(null);

        // act
        byte[] bytes = new byte[0];
        ResponseEntity<Void> responseEntity = inputController.receiveData(bytes);

        // assert
//        Mockito.verify(inputGateway).digest(new String(bytes));
        Mockito.verify(inputGateway).digest(bytes);
    }

}