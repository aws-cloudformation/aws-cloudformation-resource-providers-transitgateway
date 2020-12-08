package software.amazon.ec2.transitgatewayattachment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.cloudformation.proxy.*;

import static org.mockito.Mockito.mock;

public class AbstractTestBase {
  protected final static String TRANSIT_GATEWAY_ID = "tgw-0d88d2d0d5EXAMPLE";
  protected final static String VPD_ID = "vpc-id-0d8e2e0d5EXAMPLE";
  protected final static String[] SUBNET_IDs = new String[]{"subnet-id-0d8ef0d5EXAMPLE"};

  protected final static String TAG_KEY_1 = "testKey1";
  protected final static String TAG_VALUE_1 = "testValue1";
  protected final static String RESOURCE_ID = "resource-id-02b782-EXAMPLE";
  protected final static String RESOURCE_TYPE = "vpc";

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

  protected TransitGatewayVpcAttachment buildTransitGatewayVpcAttachment() {
    return TransitGatewayVpcAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .tags(createTransitGatewayTags())
            .subnetIds(SUBNET_IDs)
            .build();
  }

  protected List<software.amazon.awssdk.services.ec2.model.Tag> createTransitGatewayTags() {
    final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
    final software.amazon.awssdk.services.ec2.model.Tag t1 = Tag.builder().key(TAG_KEY_1).value(TAG_VALUE_1).build();
    tags.add(t1);
    return tags;
  }

  protected List<software.amazon.awssdk.services.ec2.model.Filter> createTransitGatewayFilter() {
    final List<Filter> filters = new ArrayList<>();
    final Filter f1 = Filter.builder().name("resource-id").values(RESOURCE_ID).build();
    final Filter f2 = Filter.builder().name("resource-type").values(RESOURCE_TYPE).build();
    filters.add(f1);
    filters.add(f2);
    return filters;
  }

  protected TransitGatewayVpcAttachment buildAvailableTransitGatewayVpcAttachment() {
    return TransitGatewayVpcAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .vpcId(VPD_ID)
            .subnetIds(SUBNET_IDs)
            .state(TransitGatewayAttachmentState.AVAILABLE)
            .build();
  }

  protected TransitGatewayAttachment buildAvailableTransitGatewayAttachment() {
    return TransitGatewayAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .state(TransitGatewayAttachmentState.AVAILABLE)
            .resourceId(RESOURCE_ID)
            .resourceType(RESOURCE_TYPE)
            .build();
  }

  protected TransitGatewayVpcAttachment buildDeletingTransitGatewayVpcAttachment() {
    return TransitGatewayVpcAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .vpcId(VPD_ID)
            .subnetIds(SUBNET_IDs)
            .state(TransitGatewayAttachmentState.DELETING)
            .build();
  }

  protected TransitGatewayVpcAttachment buildDeletedTransitGatewayVpcAttachment() {
    return TransitGatewayVpcAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .vpcId(VPD_ID)
            .subnetIds(SUBNET_IDs)
            .state(TransitGatewayAttachmentState.DELETED)
            .build();
  }

  protected TransitGatewayVpcAttachment buildPendingTransitGatewayVpcAttachment() {
    return TransitGatewayVpcAttachment.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .vpcId(VPD_ID)
            .subnetIds(SUBNET_IDs)
            .state(TransitGatewayAttachmentState.PENDING)
            .build();
  }


}
