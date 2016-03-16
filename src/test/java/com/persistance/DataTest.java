package com.persistance;


import com.example.input.persistance.Data;
import com.example.input.persistance.DataRepository;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = DataTestConfiguration.class)
public class DataTest {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataRepository dataRepository;

    @Test
    public void save_validateJpaListenerIsSettingDateTimeAndUniqueDataId() {
        Data data = dataRepository.save(new Data());

        assertThat(data.getZonedDateTime()).isNotNull();
        assertThat(data.getUniqueDataId()).isNotNull();
    }

    @Test
    public void save_verifyThatEntityWasPersisted() {
        Data data = dataRepository.save(new Data());
        Data savedData = entityManager.find(Data.class, data.getId());

        assertThat(savedData.getId()).isEqualTo(data.getId());
    }

}
