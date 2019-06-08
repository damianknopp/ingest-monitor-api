package dmk.aws.ingest.monitor.web.controller.kinesis;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.model.*;
import dmk.aws.ingest.monitor.web.model.kinesis.DescribeConsumer;
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
 * Query information about given kinesis streams
 */
@RestController
@RequestMapping(value = "/api/kinesis")
public class KinesisController {
    protected Logger logger = LoggerFactory.getLogger(KinesisController.class);

    @Autowired
    AmazonKinesisAsync kinesisAsyncClient;
    @Autowired
    AWSCredentials awsCredentials;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "streams",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<String>> getAllStreams() {
        if (logger.isDebugEnabled()) {
            logger.debug("get all kinesis streams");
            logger.debug("access key = " + awsCredentials.getAWSAccessKeyId());
        }

        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            try {
                return kinesisAsyncClient.listStreamsAsync().get().getStreamNames();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService));
    }

    @RequestMapping(value = "stream/{name}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<StreamDescription> describeStream(@PathVariable("name") String streamName) {
        if (logger.isDebugEnabled()) {
            logger.debug("describe stream {}", streamName);
        }

        Future<DescribeStreamResult> resp = kinesisAsyncClient.describeStreamAsync(streamName);
        CompletableFuture<StreamDescription> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getStreamDescription();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "stream/consumer",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<ConsumerDescription> describeStreamConsumer(@RequestBody DescribeConsumer describeConsumer) {
        if (logger.isDebugEnabled()) {
            logger.debug("describe stream consumer {}", describeConsumer.toString());
        }

        DescribeStreamConsumerRequest request = new DescribeStreamConsumerRequest();
        request.setConsumerName(describeConsumer.getConsumerName());
        request.setStreamARN(describeConsumer.getStreamArn());
        Future<DescribeStreamConsumerResult> resp = kinesisAsyncClient.describeStreamConsumerAsync(request);
        CompletableFuture<ConsumerDescription> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getConsumerDescription();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

}



