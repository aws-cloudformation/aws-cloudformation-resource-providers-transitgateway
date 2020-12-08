package software.amazon.ec2.transitgatewayattachment;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;
import software.amazon.awssdk.core.SdkClient;
// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceClient
// import software.amazon.awssdk.services.yourservice.YourServiceClient;
// import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  public static Ec2Client getClient() {
    return Ec2Client.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
  }
}
