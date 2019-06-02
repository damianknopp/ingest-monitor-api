package dmk.aws.ingest.monitor.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 * Swagger 3 is needed for Spring 5 and Webflux
 * @see https://github.com/springfox/springfox/issues/1773
 *
 * Swagger has trouble showing functional webflux style handlers
 * @see https://github.com/springfox/springfox/issues/279
 */
@Configuration
@EnableSwagger2WebFlux
public class SwaggerWebfluxConfig {

    /**
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("dmk.aws.ingest.monitor.web"))
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
}
