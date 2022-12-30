package pl.logic.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.aws")
@Configuration
@Getter
@Setter
public class AwsProperties {
    private String bucketNameOfExams;
    private String bucketNameOfTests;
    private String bucketNameOfInboxExams;
}
