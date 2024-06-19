package com.jowen.smartqa.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zhipu.ai")
public class AiConfig {
    private String apiKey;

    @Bean
    public ClientV4 clientV4() {
        System.out.println("apiKey: " + apiKey);
        return new ClientV4.Builder(apiKey).build();
    }
}
