package dmk.aws.ingest.monitor.web.conf;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.spring4.properties.EncryptablePropertyPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Order(1)
@Configuration
public class AwsCredsConfig {
    Logger logger = LoggerFactory.getLogger(getClass());

    final static Resource[] locations = {
            new ClassPathResource("aws-creds-development.properties")
    };

    @Value("${dmk.aws.region:us-east-1}")
    protected String awsRegion;

    @Value("${dmk.aws.access.key}")
    protected String awsAccessKey;

    @Value("${dmk.aws.access.secret}")
    protected String awsAccessSecret;

    @Bean
    public String awsRegion(){
        return this.awsRegion;
    }

    @Bean
    public String awsAccessKey(){
        return this.awsAccessKey;
    }

    @Bean
    public String awsAccessSecret(){
        return this.awsAccessSecret;
    }

    @Bean
    public static EnvironmentStringPBEConfig environmentStringPBEConfig() {
        EnvironmentStringPBEConfig tmp = new EnvironmentStringPBEConfig();
        tmp.setAlgorithm("PBEWITHMD5ANDTRIPLEDES");
        tmp.setPasswordCharArray("uA8-S?(8D{I@*S\\Si".toCharArray());
        return tmp;
    }

    @Bean
    public static StandardPBEStringEncryptor configurationEncryptor(EnvironmentStringPBEConfig environmentStringPBEConfig) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setConfig(environmentStringPBEConfig);
        standardPBEStringEncryptor.setIvGenerator(new RandomIvGenerator());
        standardPBEStringEncryptor.setKeyObtentionIterations(9000);
        return standardPBEStringEncryptor;
    }

    @Bean
    public static EncryptablePropertyPlaceholderConfigurer encryptablePropertyPlaceholderConfigurer(PBEStringCleanablePasswordEncryptor pBEStringCleanablePasswordEncryptor) {
        EncryptablePropertyPlaceholderConfigurer configurer = new EncryptablePropertyPlaceholderConfigurer(
                pBEStringCleanablePasswordEncryptor);
        configurer.setLocations(locations);
        return configurer;
    }

}
