package software.amazon.ec2.transitgatewaymulticastdomain;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomainState;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class TestBase {

    protected final static String TRANSIT_GATEWAY_ID = "tgw-0d88d2d0d5EXAMPLE";
    protected final static String TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID = "tgw-mcast-domain-02bb79002EXAMPLE";
    protected final static String TAG_KEY_1 = "testKey1";
    protected final static String TAG_VALUE_1 = "testValue1";
    protected final static String TAG_KEY_2 = "testKey2";
    protected final static String TAG_VALUE_2 = "testValue2";

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
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .tags(Utils.sdkTagsToCfnTags(createTransitGatewayTags()))
                .build();
    }

    protected ResourceModel buildResourceModelWithDifferentTags() {
        return ResourceModel.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .tags(Utils.sdkTagsToCfnTags(createTransitGatewayTags(TAG_KEY_2, TAG_VALUE_2)))
                .build();
    }

    protected TransitGatewayMulticastDomain buildTransitGatewayMulticastDomain() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .tags(createTransitGatewayTags())
                .build();
    }

    protected List<Tag> createTransitGatewayTags() {
        return createTransitGatewayTags(TAG_KEY_1, TAG_VALUE_1);
    }

    protected List<Tag> createTransitGatewayTags(String tagKey, String tagValue) {
        final List<Tag> tags = new ArrayList<>();
        final Tag t1 = Tag.builder().key(tagKey).value(tagValue).build();
        tags.add(t1);
        return tags;
    }

    protected TransitGatewayMulticastDomain buildAvailableTransitGatewayMulticastDomain() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .state(TransitGatewayMulticastDomainState.AVAILABLE)
                .build();
    }

    protected TransitGatewayMulticastDomain buildAvailableTransitGatewayMulticastDomainWithTags() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .tags(createTransitGatewayTags())
                .state(TransitGatewayMulticastDomainState.AVAILABLE)
                .build();
    }

    protected TransitGatewayMulticastDomain buildPendingTransitGatewayMulticastDomain() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .state(TransitGatewayMulticastDomainState.PENDING)
                .build();
    }

    protected TransitGatewayMulticastDomain buildDeletingTransitGatewayMulticastDomain() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .state(TransitGatewayMulticastDomainState.DELETING)
                .build();
    }

    protected TransitGatewayMulticastDomain buildDeletedTransitGatewayMulticastDomain() {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(TRANSIT_GATEWAY_ID)
                .transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID)
                .state(TransitGatewayMulticastDomainState.DELETED)
                .build();
    }
}
