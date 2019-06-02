package dmk.aws.ingest.monitor.web;


import dmk.aws.ingest.monitor.web.conf.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Import({ AwsCredsConfig.class, WebfluxConfig.class, SwaggerWebfluxConfig.class, HelloRoutes.class, AwsConfig.class})
@PropertySource("classpath:application-development.properties")
@ComponentScan(basePackages = {"dmk.aws.ingest.monitor.web"})
public class Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}


