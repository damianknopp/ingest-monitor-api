package dmk.aws.ingest.monitor.web.conf;

import dmk.aws.ingest.monitor.web.handler.HelloHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

// Setup functional only Webflux handlers
//  Webflux can be written in functional or traditional controller style
@Configuration
public class HelloRoutes {

    @Bean
    HelloHandler kinesisHandler() {
        return new HelloHandler();
    }

    @Bean
    RouterFunction<ServerResponse> helloFunctionalWebflux(HelloHandler handler) {
        return route(GET("/api/hello-function-webflux/1"), handler::functionalHello);
    }

    @Bean
    RouterFunction<ServerResponse> helloFunctionalWebfluxWithDocs(HelloHandler handler) {
        return route(GET("/api/hello-function-webflux/2"), handler::functionalHelloForceSwaggerDoc);
    }
}
