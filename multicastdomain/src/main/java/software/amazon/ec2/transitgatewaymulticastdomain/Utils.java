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
     * where Tag1 is cloudformation Tag object and Tag2 is EC2 SDK Tag object
     */
    static List<software.amazon.awssdk.services.ec2.model.TagSpecification> cfnTagsToSdkTags(final List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        for (final software.amazon.ec2.transitgatewaymulticastdomain.Tag tag : tags) {
            if (tag.getKey() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null key");
            }
            if (tag.getValue() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null value");
            }
        }
        final List<TagSpecification> tagSpecificationList = new ArrayList<>();

        for (final software.amazon.ec2.transitgatewaymulticastdomain.Tag tag : tags) {
            Tag t = Tag.builder()
                    .key(tag.getKey())
                    .value(tag.getValue())
                    .build();
            tagSpecificationList.add(TagSpecification.builder().tags(t).build());
        }

        return tagSpecificationList;
    }

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
     * Converter method to convert Map<String, String> tag to SDK tag
     */
    static List<TagSpecification> mapTagsToSdkTags(final Map<String, String> tags) {
        if (tags == null) return null;
        List<Tag> tagList = tags.keySet().stream().map(key -> Tag.builder()
                .key(key)
                .value(tags.get(key))
                .build()
        ).collect(Collectors.toList());

        TagSpecification tagSpecification = TagSpecification.builder().tags(tagList).build();
        final List<TagSpecification> tagSpecificationList = new ArrayList<>();
        tagSpecificationList.add(tagSpecification);

        return tagSpecificationList;
    }

    static List<TagSpecification> translateTagsToTagSpecifications(final List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> modelTags) {
        if (modelTags == null) {
            return null;
        }
        List<Tag> tags = modelTags
                .stream()
                .map(tag -> Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .collect(Collectors.toList());
        return Arrays.asList(TagSpecification.builder()
                .resourceType("transit-gateway-multicast-domain")
                .tags(tags)
                .build());
    }
}


