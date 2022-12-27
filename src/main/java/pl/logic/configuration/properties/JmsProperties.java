package pl.logic.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "activemq.broker")
@Configuration
@Getter
@Setter
public class JmsProperties {
    private String url;
}
