package com.jowen.smartqa.config;

import io.minio.MinioClient;
import lombok.Data;
import okhttp3.HttpUrl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(Objects.requireNonNull(HttpUrl.parse(endpoint)))
                .credentials(accessKey, secretKey)
                .build();
    }
}
