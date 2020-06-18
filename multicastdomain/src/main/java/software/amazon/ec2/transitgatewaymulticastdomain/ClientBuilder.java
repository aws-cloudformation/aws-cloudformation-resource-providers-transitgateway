package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    static Ec2Client getClient() {
        return Ec2Client.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .region(Region.US_EAST_1)
                .build();
    }
}
