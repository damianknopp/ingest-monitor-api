package dmk.aws.ingest.monitor.web.controller;

import dmk.aws.ingest.monitor.web.model.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api")
public class HelloController {
    protected Logger logger = LoggerFactory.getLogger(HelloController.class);

    /**
     * Using Mono confuses the Swagger API document, it will not know the return type structure
     * @param id
     * @return
     */
    @RequestMapping(value = "hello-as-mono/{id}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Hello> getHelloAsMono(@PathVariable("id") String id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get hello as mono " + id);
        }

        Hello hello = new Hello();
        hello.setMsg("Hello from Mono, " + id + "!");
        return Mono.just(hello);
    }

    /**
     *  Not using Mono as a return type will help the Swagger API document, it will know the return type structure
     * @param id
     * @return
     */
    @RequestMapping(value = "hello/{id}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Hello getHello(@PathVariable(value = "id") String id) {
        if (logger.isDebugEnabled()) {
            logger.debug("GET hello id = " + id);
        }


        Hello hello = new Hello();
        hello.setMsg("Hello " + id + "!");
        return hello;
    }

    /**
     * TODO: Cant seem to tell Webflux to respect content negotiation
     *  regardless if the negotiation is to use Accept header or format query parameter
     * NOTE: suffix pattern matching does not work
     * @see https://github.com/spring-projects/spring-framework/issues/20198
     * @param id
     * @return
     */
    @RequestMapping(value = "hello-as-content/{id}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Hello getHelloWithContentNegotiation(@PathVariable("id") String id) {
        if (logger.isDebugEnabled()) {
            logger.debug("get hello as content negotiated!");
            logger.debug("id = " + id);
//            logger.debug(request.getMethod() + " from " + request.getRemoteAddress());
//            var acceptHeader = request.getHeaders().getAccept();
//            var formatParam = request.getQueryParams().get("format");
//            logger.debug("accept " + acceptHeader + " formatParam " + formatParam);
        }

        Hello hello = new Hello();
        hello.setMsg("Hello " + id + "!");
        return hello;
    }

}



