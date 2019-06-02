package dmk.aws.ingest.monitor.web.handler;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

public class HelloHandler {

    public Mono<ServerResponse> functionalHello(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(fromObject("{ \"msg\": \"Hello from function Webflux!\" }"));
    }



    @ApiOperation(value="", produces = "application/json", response = Mono.class)
    public Mono<ServerResponse> functionalHelloForceSwaggerDoc(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(fromObject("{ \"msg\": \"Hello from function Webflux!\" }"));
    }
}
