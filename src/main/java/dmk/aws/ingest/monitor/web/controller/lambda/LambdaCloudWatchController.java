package dmk.aws.ingest.monitor.web.controller.lambda;

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
 * Query cloud watch for rollup metrics given days ago and a kinesis stream name
 */
@RestController
@RequestMapping(value = "/api/lambda")
public class LambdaCloudWatchController {
    protected Logger logger = LoggerFactory.getLogger(LambdaCloudWatchController.class);
    protected static String CLOUDWATCH_NAMESPACE = "AWS/Lambda";

    @Autowired
    AmazonCloudWatchAsync cloudWatchAsyncClient;
    @Autowired
    ExecutorService scheduledExecutorService;

    @RequestMapping(value = "{functionName}/cloudwatch/invocations/days/{days}/period/{period}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getInvocationsSumDaysAgoWindowed(
            @PathVariable("functionName") String functionName,
            @PathVariable("days") Integer days,
            @PathVariable("period") Integer period) {
        if (logger.isDebugEnabled()) {
            logger.debug("get invocations for lambda {}, {} day(s) ago, {} period", functionName, days, period);
        }

        GetMetricStatisticsRequest request = buildInvocationsMetricsRequest(functionName, days, period);
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

    @RequestMapping(value = "{functionName}/cloudwatch/invocations/days/{days}",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    public Mono<List<Datapoint>> getInvocationsSumDaysAgo(
            @PathVariable("functionName") String functionName,
            @PathVariable("days") Integer days) {
        if (logger.isDebugEnabled()) {
            logger.debug("get invocations for lambda {}, {} day(s) ago", functionName, days);
        }

        return this.getInvocationsSumDaysAgoWindowed(functionName, days, 5 * 60);
    }

    /**
     * build a sum statistics count metric for invocations, given days ago
     *
     * @param metrics
     * @param daysAgo    Integer
     * @param functionName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildMetricsRequest(String metrics, String functionName, Integer daysAgo, Integer period) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime yesterday = now.minusDays(daysAgo);
        final ZoneId zoneId = ZoneId.systemDefault();
        final Dimension dimension = new Dimension().withName("FunctionName").withValue(functionName);
        final GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
        request
                .withNamespace(LambdaCloudWatchController.CLOUDWATCH_NAMESPACE)
                .withStatistics(Statistic.Sum)
                .withDimensions(dimension)
                .withPeriod(period)
                .withEndTime(Date.from(now.atZone(zoneId).toInstant()))
                .withStartTime(Date.from(yesterday.atZone(zoneId).toInstant()))
                .withMetricName(metrics);
        return request;
    }

    /**
     * build a sum statistics count metric for invocations, given days ago
     *
     * @param daysAgo    Integer
     * @param functionName String
     * @return GetMetricStatisticsRequest
     */
    protected GetMetricStatisticsRequest buildInvocationsMetricsRequest(String functionName, Integer daysAgo, Integer period) {
        return this.buildMetricsRequest("Invocations", functionName, daysAgo, period);
    }

}



