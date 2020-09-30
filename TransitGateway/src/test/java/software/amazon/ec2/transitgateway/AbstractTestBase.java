package software.amazon.ec2.transitgateway;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import static org.mockito.Mockito.mock;

public class AbstractTestBase {
  protected final static String TRANSIT_GATEWAY_ID = "tgw-0d88d2d0d5EXAMPLE";
  protected final static String TAG_KEY_1 = "testKey1";
  protected final static String TAG_VALUE_1 = "testValue1";

  protected CallbackContext context;

  @Mock
  protected AmazonWebServicesClientProxy proxy;

  @Mock
  protected Logger logger;

  @BeforeEach
  public void setup() {
    proxy = mock(AmazonWebServicesClientProxy.class);
    logger = mock(Logger.class);
    context = null;
  }

  protected ResourceModel buildResourceModel() {
    return ResourceModel.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .build();
  }

  protected TransitGateway buildTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .tags(createTransitGatewayTags())
            .build();
  }

  protected List<software.amazon.awssdk.services.ec2.model.Tag> createTransitGatewayTags() {
    final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
    final software.amazon.awssdk.services.ec2.model.Tag t1 = Tag.builder().key(TAG_KEY_1).value(TAG_VALUE_1).build();
    tags.add(t1);
    return tags;
  }

  protected TransitGateway buildAvailableTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .state(TransitGatewayState.AVAILABLE)
            .build();
  }

  protected TransitGateway buildDeletingTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .state(TransitGatewayState.DELETING)
            .build();
  }

  protected TransitGateway buildDeletedTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .state(TransitGatewayState.DELETED)
            .build();
  }


}
