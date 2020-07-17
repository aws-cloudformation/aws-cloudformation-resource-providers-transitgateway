package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.services.ec2.model.TagSpecification;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.*;
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
        resourceModel.setTags(Utils.sdkTagsToCfnTags(transitGatewayMulticastDomain.tags()));

        return resourceModel;
    }

    /**
     * Converter method to convert stack/resource tags to EC2 TagSpecification List
     */
    static List<TagSpecification> translateTagsToTagSpecifications(final Map<String, String> tags) {
        if(tags == null) {
            return null;
        }

        List<Tag> newTags = tags.keySet()
                .stream()
                .map(t -> Tag.builder()
                        .key(t).value(tags.get(t))
                        .build())
                .collect(Collectors.toList());

        return Arrays.asList(TagSpecification.builder()
                .resourceType("transit-gateway-multicast-domain")
                .tags(newTags)
                .build());
    }
}
