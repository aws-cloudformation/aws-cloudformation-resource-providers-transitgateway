package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;

import java.time.Duration;

public class ClientBuilder {
   private static Ec2Client ec2Client;

    private static final BackoffStrategy EC2_BACKOFF_THROTTLING_STRATEGY =
            EqualJitterBackoffStrategy.builder()
                    .baseDelay(Duration.ofMillis(2000)) //1st retry is ~2 sec
                    .maxBackoffTime(SdkDefaultRetrySetting.MAX_BACKOFF) //default is 20s
                    .build();

    private static final RetryPolicy EC2_RETRY_POLICY =
            RetryPolicy.builder()
                    .numRetries(4)
                    .retryCondition(RetryCondition.defaultRetryCondition())
                    .throttlingBackoffStrategy(EC2_BACKOFF_THROTTLING_STRATEGY)
                    .build();

   public static Ec2Client getClient() {

        return ec2Client = Ec2Client.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .retryPolicy(EC2_RETRY_POLICY)
                        .build())
                .build();
    }

}
