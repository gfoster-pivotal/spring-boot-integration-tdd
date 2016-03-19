package com.integration;


import com.example.input.persistance.*;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.test.ImportAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = DataTest.DataTestConfiguration.class)
public class DataTest {
    @Transactional
    @Configuration
    @EntityScan(basePackages = "com.example.input.persistance")
    @EnableJpaRepositories(basePackages = "com.example.input.persistance")
    @ImportAutoConfiguration({HibernateJpaAutoConfiguration.class})
    public static class DataTestConfiguration implements BeanClassLoaderAware {
        private ClassLoader classLoader;

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseConnection.get(classLoader).getType())
                    .build();
        }
    }

    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private EnrichedDataRepository enrichedDataRepository;

    @Test
    public void dataSave_validateJpaListenerIsSettingDateTimeAndUniqueDataId() {
        BaseEntity baseEntity = dataRepository.save(new Data());

        assertThat(baseEntity.getZonedDateTime()).isNotNull();
        assertThat(baseEntity.getUniqueDataId()).isNotNull();
    }

    @Test
    public void enrichedDataSave_validateJpaListenerIsSettingDateTimeAndUniqueDataId() {
        BaseEntity baseEntity = enrichedDataRepository.save(new EnrichedData());

        assertThat(baseEntity.getZonedDateTime()).isNotNull();
        assertThat(baseEntity.getUniqueDataId()).isNotNull();
    }

    @Test
    public void save_verifyThatEntityWasPersisted() {
        Data data = dataRepository.save(new Data());
        Data savedData = entityManager.find(Data.class, data.getId());

        assertThat(savedData.getId()).isEqualTo(data.getId());
    }

}
