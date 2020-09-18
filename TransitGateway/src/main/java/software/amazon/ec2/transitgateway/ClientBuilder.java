package software.amazon.ec2.transitgateway;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {

//  TODO: uncomment the following, replacing YourServiceClient with your service client name
//  It is recommended to use static HTTP client so less memory is consumed
//  e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/master/aws-logs-loggroup/src/main/java/software/amazon/logs/loggroup/ClientBuilder.java#L9

  public static Ec2Client getClient() {
    return Ec2Client.builder()
              .httpClient(LambdaWrapper.HTTP_CLIENT)
              .build();
  }


}
