package dmk.aws.ingest.monitor.web.controller.kinesis;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.Statistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Query cloud watch for rollup metrics given days ago and a kinesis stream name
 */
@RestController
@RequestMapping(value = "/api/kinesis/stream")
public class KinesisCloudWatchController {
    protected Logger logger = LoggerFactory.getLogger(KinesisCloudWatchController.class);
    protected static String KINESIS_NAMESPACE = "AWS/Kinesis";

    @Autowired
    AmazonCloudWatchAsync cloudWatchAsyncClient;
    @Autowired
    AWSCredentials awsCredentials;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "{stream}/cloudwatch/records/incoming/period/day/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getIncomingRecords(
            @PathVariable("stream") String streamName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get incoming records for kinesis stream {} and {} day(s) ago", streamName, days);
        }

        GetMetricStatisticsRequest request = buildMetricsRequestForIncomingRecords(streamName, days);
        Future<GetMetricStatisticsResult> resp = cloudWatchAsyncClient.getMetricStatisticsAsync(request);
        CompletableFuture<List<Datapoint>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getDatapoints();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "{stream}/cloudwatch/records/outgoing/period/day/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getOutgoingRecords(
            @PathVariable("stream") String streamName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get outgoing records for kinesis stream {} and {} day(s) ago", streamName, days);
        }

        GetMetricStatisticsRequest request = buildMetricsRequestForOutgoingRecords(streamName, days);
        Future<GetMetricStatisticsResult> resp = cloudWatchAsyncClient.getMetricStatisticsAsync(request);
        CompletableFuture<List<Datapoint>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getDatapoints();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "{stream}/cloudwatch/bytes/outgoing/period/day/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getOutgoingBytes(
            @PathVariable("stream") String streamName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get outgoing bytes for kinesis stream {} and {} day(s) ago", streamName, days);
        }

        GetMetricStatisticsRequest request = buildMetricsRequestForOutgoingBytes(streamName, days);
        Future<GetMetricStatisticsResult> resp = cloudWatchAsyncClient.getMetricStatisticsAsync(request);
        CompletableFuture<List<Datapoint>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getDatapoints();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    @RequestMapping(value = "{stream}/cloudwatch/bytes/incoming/period/day/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getIncomingBytes(
            @PathVariable("stream") String streamName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get incoming bytes for kinesis stream {} and {} day(s) ago", streamName, days);
        }

        GetMetricStatisticsRequest request = buildMetricsRequestForIncomingBytes(streamName, days);
        Future<GetMetricStatisticsResult> resp = cloudWatchAsyncClient.getMetricStatisticsAsync(request);
        CompletableFuture<List<Datapoint>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return resp.get().getDatapoints();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, scheduledExecutorService);

        return Mono.fromFuture(future);
    }

    /**
     * build a single sum statistics count metric for incoming or outgoing records, given days ago
     *
     * @param metrics    PutRecords.Records, GetRecords.Records
     *                   - MetricsName.OutgoingRecords, MetricsName.IncomingRecords did not seem to work
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForRecords(String metrics, String streamName, Integer daysAgo) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime yesterday = now.minusDays(daysAgo);
        final ZoneId zoneId = ZoneId.systemDefault();
        final Integer daysInSeconds = daysAgo * 86400;
        final Dimension dimension = new Dimension().withName("StreamName").withValue(streamName);
        final GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
        request
                .withNamespace(KinesisCloudWatchController.KINESIS_NAMESPACE)
                .withStatistics(Statistic.Sum)
                .withDimensions(dimension)
                .withPeriod(daysInSeconds)
                .withEndTime(Date.from(now.atZone(zoneId).toInstant()))
                .withStartTime(Date.from(yesterday.atZone(zoneId).toInstant()))
                .withMetricName(metrics);
        return request;
    }

    /**
     * build a single sum statistics count metric for incoming records, given days ago
     *
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForIncomingRecords(String streamName, Integer daysAgo) {
        return this.buildMetricsRequestForRecords("PutRecords.Records", streamName, daysAgo);
    }

    /**
     * build a single sum statistics count metric for outgoing records, given days ago
     *
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForOutgoingRecords(String streamName, Integer daysAgo) {
        return this.buildMetricsRequestForRecords("GetRecords.Records", streamName, daysAgo);
    }

    /**
     * build a single sum statistics count metric for incoming or outgoing records, given days ago
     *
     * @param metrics    PutRecords.Bytes, GetRecords.Bytes
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForBytes(String metrics, String streamName, Integer daysAgo) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime yesterday = now.minusDays(daysAgo);
        final ZoneId zoneId = ZoneId.systemDefault();
        final Integer daysInSeconds = daysAgo * 86400;
        final Dimension dimension = new Dimension().withName("StreamName").withValue(streamName);
        final GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
        request
                .withNamespace(KinesisCloudWatchController.KINESIS_NAMESPACE)
                .withStatistics(Statistic.Sum)
                .withDimensions(dimension)
                .withPeriod(daysInSeconds)
                .withEndTime(Date.from(now.atZone(zoneId).toInstant()))
                .withStartTime(Date.from(yesterday.atZone(zoneId).toInstant()))
                .withMetricName(metrics)
                .withUnit(StandardUnit.Bytes);
        return request;
    }

    /**
     * build a single sum statistics count metric for outgoing bytes, given days ago
     *
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForOutgoingBytes(String streamName, Integer daysAgo) {
        return this.buildMetricsRequestForBytes("GetRecords.Bytes", streamName, daysAgo);
    }

    /**
     * build a single sum statistics count metric for incoming bytes, given days ago
     *
     * @param daysAgo    Integer
     * @param streamName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequestForIncomingBytes(String streamName, Integer daysAgo) {
        return this.buildMetricsRequestForBytes("PutRecords.Bytes", streamName, daysAgo);
    }
}



