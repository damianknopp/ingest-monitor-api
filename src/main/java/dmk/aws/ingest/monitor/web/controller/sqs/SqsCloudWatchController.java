package dmk.aws.ingest.monitor.web.controller.sqs;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
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
 * Query cloud watch for rollup metrics given days ago and a sqs queue name/arn
 */
@RestController
@RequestMapping(value = "/api/sqs")
public class SqsCloudWatchController {
    protected Logger logger = LoggerFactory.getLogger(SqsCloudWatchController.class);
    protected static String SQS_NAMESPACE = "AWS/SQS";

    @Autowired
    AmazonCloudWatchAsync cloudWatchAsyncClient;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "{queueName}/cloudwatch/messages/deleted/days/{days}/period/{period}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getNumberOfMessagesDeletedWindowed(
            @PathVariable("functionName") String queueName,
            @PathVariable("days") Integer days,
            @PathVariable("period") Integer period) {
        if (logger.isDebugEnabled()) {
            logger.debug("get number of messages deleted for queue {}, {} day(s) ago, {} window period", queueName, days, period);
        }

        GetMetricStatisticsRequest request = buildMessagesDeletedMetricsRequest(queueName, days, period);
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

    @RequestMapping(value = "{queueName}/cloudwatch/messages/deleted/days/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getNumberOfMessagesDeletedDefaultWindow(
            @PathVariable("queueName") String queueName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get number of messages deleted for queue {}, {} day(s) ago", queueName, days);
        }

        // default 5 min window
        return this.getNumberOfMessagesDeletedWindowed(queueName, days, 5 * 60);
    }

    /**
     * build a sum statistics count for metric, given days ago, windowed period
     *
     * @param metrics   String
     * @param queueName String
     * @param daysAgo   Integer
     * @param period    Integer
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequest(String metrics, String queueName, Integer daysAgo, Integer period) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime yesterday = now.minusDays(daysAgo);
        final ZoneId zoneId = ZoneId.systemDefault();
        final Dimension dimension = new Dimension().withName(metrics).withValue(queueName);
        final GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
        request
                .withNamespace(SqsCloudWatchController.SQS_NAMESPACE)
                .withStatistics(Statistic.Sum)
                .withDimensions(dimension)
                .withPeriod(period)
                .withEndTime(Date.from(now.atZone(zoneId).toInstant()))
                .withStartTime(Date.from(yesterday.atZone(zoneId).toInstant()))
                .withMetricName(metrics);
        return request;
    }

    /**
     * build a sum statistics count metric for deleted messages, given days ago, windowed period
     *
     * @param queueName String
     * @param daysAgo   Integer
     * @param period    Integer
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMessagesDeletedMetricsRequest(String queueName, Integer daysAgo, Integer period) {
        return this.buildMetricsRequest("NumberOfMessagesDeleted", queueName, daysAgo, period);
    }
}



