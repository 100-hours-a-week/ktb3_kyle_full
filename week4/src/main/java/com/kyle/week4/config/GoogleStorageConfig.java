package com.kyle.week4.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;

@Getter
@Component
@Configuration
public class GoogleStorageConfig {

    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String credentialJson;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Bean
    public Storage storage() throws IOException {
        InputStream is = ResourceUtils.getURL(credentialJson).openStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(is);
        return StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build()
            .getService();
    }
}
