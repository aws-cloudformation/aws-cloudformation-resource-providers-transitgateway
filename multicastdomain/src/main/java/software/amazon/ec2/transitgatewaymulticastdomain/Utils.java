package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is EC2 SDK Tag object and Tag2 is CFN Tag object
     */
    static List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) return null;
        final List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> cfnTags =
                tags.stream()
                        .map(e -> software.amazon.ec2.transitgatewaymulticastdomain.Tag.builder()
                                .key(e.key())
                                .value(e.value())
                                .build())
                        .collect(Collectors.toList());
        return cfnTags;
    }

    /**
     * Converter method to convert TransitGatewayMulticastDomain to CFN ResourceModel for LIST/READ request
     */
    static ResourceModel transformTransitGatewayMulticastDomain(final TransitGatewayMulticastDomain transitGatewayMulticastDomain) {
        final ResourceModel resourceModel = ResourceModel.builder().build();

        resourceModel.setTransitGatewayId(transitGatewayMulticastDomain.transitGatewayId());
        resourceModel.setTransitGatewayMulticastDomainId(transitGatewayMulticastDomain.transitGatewayMulticastDomainId());
        resourceModel.setTagSet(Utils.sdkTagsToCfnTags(transitGatewayMulticastDomain.tags()));

        return resourceModel;
    }
}
