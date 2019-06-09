package dmk.aws.ingest.monitor.web.conf;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AwsConfig {
    protected Logger logger = LoggerFactory.getLogger(AwsConfig.class);

    @Bean
    AWSCredentials awsCredentials(String awsAccessKey, String awsAccessSecret) {
        return new BasicAWSCredentials(awsAccessKey, awsAccessSecret);
    }

    /**
     * @see https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-http.html
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/kinesis/AmazonKinesisAsync.html
     * @param awsCredentials
     * @return AmazonKinesisAsync
     */
    @Bean
    AmazonKinesisAsync kinesisAsyncClient(AWSCredentials awsCredentials, String awsRegion) {
        AWSCredentialsProvider credsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonKinesisAsync kinesisClient = AmazonKinesisAsyncClientBuilder
                .standard()
                .withCredentials(credsProvider)
                .withRegion(awsRegion)
                .build();

        if (logger.isDebugEnabled()) {
            try {
                ListStreamsResult resp = kinesisClient
                        .listStreamsAsync()
                        .get();
                logger.debug(resp.getStreamNames().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return kinesisClient;
    }

    /**
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudwatch/AmazonCloudWatchAsyncClientBuilder.html
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudwatch/AmazonCloudWatchAsync.html
     * @param awsCredentials
     * @return AmazonCloudWatchAsync
     */
    @Bean
    AmazonCloudWatchAsync cloudWatchAsyncClient(AWSCredentials awsCredentials, String awsRegion) {
        AWSCredentialsProvider credsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonCloudWatchAsync cloudWatchAsyncClient = AmazonCloudWatchAsyncClientBuilder
                .standard()
                .withCredentials(credsProvider)
                .withRegion(awsRegion)
                .build();
        return cloudWatchAsyncClient;
    }

    /**
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/lambda/AWSLambdaAsyncClientBuilder.html
     * @param awsCredentials
     * @param awsRegion
     * @return AWSLambdaAsync
     */
    @Bean
    AWSLambdaAsync awsLambdaAsync(AWSCredentials awsCredentials, String awsRegion) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AWSLambdaAsync awsLambdaAsync = AWSLambdaAsyncClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(awsRegion)
                .build();
        return awsLambdaAsync;
    }

    /**
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/services/sqs/AmazonSQSAsyncClientBuilder.html
     * @see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSAsync.html
     * @param awsCredentials
     * @param awsRegion
     * @return AWSLambdaAsync
     */
    @Bean
    AmazonSQSAsync awsSqsAsync(AWSCredentials awsCredentials, String awsRegion) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(awsRegion)
                .build();
        return amazonSQSAsync;
    }

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newScheduledThreadPool(cores * 2);
    }

}