package dmk.aws.ingest.monitor.web.controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.model.*;
import dmk.aws.ingest.monitor.web.model.lambda.UpdateLambdaEventSourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    AWSCredentials awsCredentials;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "eventsourcemappings",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
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
            produces = {MediaType.APPLICATION_JSON_VALUE})
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
            produces = {MediaType.APPLICATION_JSON_VALUE})
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

}



