package software.amazon.ec2.transitgateway;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
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
  ProxyClient<SdkClient> proxyClient;

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

  ResourceModel model = buildResourceModel();
  protected ResourceModel buildResourceModel() {
    return ResourceModel.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .amazonSideAsn(6124)
            .autoAcceptSharedAttachments("enable")
            .description("test")
            .defaultRouteTableAssociation("enable")
            .defaultRouteTablePropagation("enable")
            .dnsSupport("enable")
            .multicastSupport("disable")
            .vpnEcmpSupport("enable")
            .build();
  }

  TransitGatewayOptions transitGatewayOptions = TransitGatewayOptions.builder()
          .amazonSideAsn(model.getAmazonSideAsn().longValue())
          .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
          .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
          .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
          .dnsSupport(model.getDnsSupport())
          .multicastSupport(model.getMulticastSupport())
          .vpnEcmpSupport(model.getVpnEcmpSupport())
          .build();

  ModifyTransitGatewayOptions modifyTransitGatewayOptions = ModifyTransitGatewayOptions.builder()
          .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
          .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
          .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
          .dnsSupport(model.getDnsSupport())
          .vpnEcmpSupport(model.getVpnEcmpSupport())
          .build();


  protected TransitGateway buildTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .options(transitGatewayOptions)
            .state(TransitGatewayState.AVAILABLE)
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

  protected TransitGateway buildPendingTransitGateway() {
    return TransitGateway.builder()
            .transitGatewayId(TRANSIT_GATEWAY_ID)
            .state(TransitGatewayState.PENDING)
            .build();
  }


}
