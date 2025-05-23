package ru.otus.hw.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SchemaInitializer implements BeanPostProcessor {

    @Value("${spring.liquibase.liquibaseSchema}")
    private String liquibaseSchema;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource dataSource) {
            try {
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s", liquibaseSchema));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
