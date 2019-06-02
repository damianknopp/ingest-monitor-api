package dmk.aws.ingest.monitor.web.runner;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.amazonaws.services.kinesis.model.ListStreamsResult;

/**
 * List kinesis stream names for given configured profile
 */
public class KinesisApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // replace with a profile name in your ${HOME}/.aws/credentials file
        String profileName = (args != null && args.length > 1) ? args[1] : "default";
        String region = new AwsProfileRegionProvider(profileName).getRegion();
        region = (region != null && region.trim().isEmpty()) ? region : "us-east-1";
        AWSCredentials awsCredentials = new ProfileCredentialsProvider(profileName).getCredentials();
        AWSCredentialsProvider credsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonKinesisAsync kinesisClient =  AmazonKinesisAsyncClientBuilder
                .standard()
                .withCredentials(credsProvider)
                .withRegion(region)
                .build();

        try {
            ListStreamsResult resp = kinesisClient
                    .listStreamsAsync()
                    .get();
            System.out.println(resp.getStreamNames());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

}