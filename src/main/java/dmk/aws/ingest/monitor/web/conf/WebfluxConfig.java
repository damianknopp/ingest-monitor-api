package dmk.aws.ingest.monitor.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import springfox.documentation.spring.web.WebFluxObjectMapperConfigurer;

/**
 * Webflux does not have path based content negotiation, favors query param and headers
 * @see https://github.com/spring-projects/spring-framework/issues/20198
 *
 *
 */
@Configuration
// TODO: using EnableWebFlux and WebfluxConfig alone breaks the swagger ui page with "No matching handler"
//@EnableWebFlux
public class WebfluxConfig {
//public class WebfluxConfig extends WebFluxConfigurationSupport {


//    public void configureContentTypeResolver(RequestedContentTypeResolverBuilder builder) {
////        builder.headerResolver();
////        RequestedContentTypeResolverBuilder.ParameterResolverConfigurer paramConfigurer = builder.parameterResolver()
////                .parameterName("format")
////                .mediaType("html", MediaType.TEXT_HTML)
////                .mediaType("json", MediaType.APPLICATION_JSON)
////                .mediaType("xml", MediaType.APPLICATION_XML);
//
//        builder.headerResolver();
//        builder.parameterResolver();
//    }

//    /**
//     * Attempt to setup parameters based content negotiation with all webflux endpoints
//     * @return
//     */
//    @Bean
//    public RequestedContentTypeResolver webFluxContentTypeResolver() {
//        RequestedContentTypeResolverBuilder builder = new RequestedContentTypeResolverBuilder();
//        builder.headerResolver();
//        RequestedContentTypeResolverBuilder.ParameterResolverConfigurer paramConfigurer = builder.parameterResolver()
//                .parameterName("format")
//                .mediaType("json", MediaType.APPLICATION_JSON)
//                .mediaType("xml", MediaType.APPLICATION_XML);
//        this.configureContentTypeResolver(builder);
//        return builder.build();
//    }

}