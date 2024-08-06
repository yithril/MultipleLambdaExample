package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.secretsmanager.secret.id}")
    private String secretId;

    @Value("${DB_HOST}")
    private String dbHost;

    @Value("${DB_PORT}")
    private String dbPort;

    @Value("${DB_NAME}")
    private String dbName;

    @Bean
    public DataSource dataSource() {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();

        GetSecretValueResponse getSecretValueResponse;
        String secretString;
        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            secretString = getSecretValueResponse.secretString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get secret from AWS Secrets Manager", e);
        }

        Map<String, String> secretsMap;
        try {
            secretsMap = new ObjectMapper().readValue(secretString, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse secret JSON", e);
        }

        String username = secretsMap.get("username");
        String password = secretsMap.get("password");

        String dbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
