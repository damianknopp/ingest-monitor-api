package dmk.aws.ingest.monitor.web.controller.lambda;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.model.*;
import dmk.aws.ingest.monitor.web.model.lambda.UpdateLambdaConcurrency;
import dmk.aws.ingest.monitor.web.model.lambda.UpdateLambdaEventSourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Query AWS for Lambda information including event source mappings
 */
@RestController
@RequestMapping(value = "/api/lambda")
public class LambdaController {
    protected Logger logger = LoggerFactory.getLogger(LambdaController.class);

    @Autowired
    AWSLambdaAsync awsLambdaAsync;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "functions",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<FunctionConfiguration>> listFunctions() {
        if (logger.isDebugEnabled()) {
            logger.debug("list functions");
        }

        Future<ListFunctionsResult> resp = awsLambdaAsync.listFunctionsAsync();
        CompletableFuture<List<FunctionConfiguration>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getFunctions();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "function/{name}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<GetFunctionResult> listFunction(@PathVariable("name") String name) {
        if (logger.isDebugEnabled()) {
            logger.debug("list function {}", name);
        }

        GetFunctionRequest request = new GetFunctionRequest()
                    .withFunctionName(name);
        Future<GetFunctionResult> resp = awsLambdaAsync.getFunctionAsync(request);
        CompletableFuture<GetFunctionResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "eventsourcemappings",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<EventSourceMappingConfiguration>> listEventSourceMappings() {
        if (logger.isDebugEnabled()) {
            logger.debug("get event source mappings");
        }

        Future<ListEventSourceMappingsResult> resp = awsLambdaAsync.listEventSourceMappingsAsync();
        CompletableFuture<List<EventSourceMappingConfiguration>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getEventSourceMappings();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "eventsourcemappings/disable",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<UpdateEventSourceMappingResult> disableEventSourceMapping(
            @RequestBody UpdateLambdaEventSourceMapping updateLambdaEventSourceMapping) {
        if (logger.isDebugEnabled()) {
            logger.debug("disable event source mapping {}", updateLambdaEventSourceMapping.toString());
        }

        UpdateEventSourceMappingRequest updateEventSourceMappingRequest = new UpdateEventSourceMappingRequest();
        updateEventSourceMappingRequest
                .withFunctionName(updateLambdaEventSourceMapping.getLambdaArn())
                .withUUID(updateLambdaEventSourceMapping.getEventSourceMappingUuid().toString())
                .withEnabled(Boolean.FALSE);
        Future<UpdateEventSourceMappingResult> resp = awsLambdaAsync.updateEventSourceMappingAsync(updateEventSourceMappingRequest);
        CompletableFuture<UpdateEventSourceMappingResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "eventsourcemappings/enable",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<UpdateEventSourceMappingResult> enableEventSourceMapping(
            @RequestBody UpdateLambdaEventSourceMapping updateLambdaEventSourceMapping) {
        if (logger.isDebugEnabled()) {
            logger.debug("disable event source mapping {}", updateLambdaEventSourceMapping.toString());
        }

        UpdateEventSourceMappingRequest updateEventSourceMappingRequest = new UpdateEventSourceMappingRequest();
        updateEventSourceMappingRequest
                .withFunctionName(updateLambdaEventSourceMapping.getLambdaArn())
                .withUUID(updateLambdaEventSourceMapping.getEventSourceMappingUuid().toString())
                .withEnabled(Boolean.TRUE);
        Future<UpdateEventSourceMappingResult> resp = awsLambdaAsync.updateEventSourceMappingAsync(updateEventSourceMappingRequest);
        CompletableFuture<UpdateEventSourceMappingResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "function/concurrency",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<PutFunctionConcurrencyResult> updateConcurrency(
            @RequestBody UpdateLambdaConcurrency updateLambdaConcurrency) {
        if (logger.isDebugEnabled()) {
            logger.debug("update concurrency {}", updateLambdaConcurrency.toString());
        }

        PutFunctionConcurrencyRequest request = new PutFunctionConcurrencyRequest()
                .withFunctionName(updateLambdaConcurrency.getLambdaName())
                .withReservedConcurrentExecutions(updateLambdaConcurrency.getConcurrency());
        Future<PutFunctionConcurrencyResult> resp = awsLambdaAsync.putFunctionConcurrencyAsync(request);
        CompletableFuture<PutFunctionConcurrencyResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

}



