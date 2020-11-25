package com.aws.ec2.transitgatewayattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TagUtils {
    public static List<com.aws.ec2.transitgatewayattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) return null;
        return tags.stream()
            .map(e -> com.aws.ec2.transitgatewayattachment.Tag.builder()
                .key(e.key())
                .value(e.value())
                .build())
            .collect(Collectors.toList());
    }

    public static List<Tag> cfnTagsToSdkTags(final List<com.aws.ec2.transitgatewayattachment.Tag> tags) {
        if (tags == null) {
            return new ArrayList<Tag>();
        }
        for (final com.aws.ec2.transitgatewayattachment.Tag tag : tags) {
            if (tag.getKey() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null key");
            }
            if (tag.getValue() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null value");
            }
        }
        return tags.stream()
            .map(e -> Tag.builder()
                .key(e.getKey())
                .value(e.getValue())
                .build())
            .collect(Collectors.toList());
    }

    public static List<TagSpecification> cfnTagsToSdkTagSpecifications(final List<com.aws.ec2.transitgatewayattachment.Tag> tags) {

       if(tags == null) return null;
        List<Tag> listTags = TagUtils.cfnTagsToSdkTags(tags);
        return TagUtils.translateTagsToTagSpecifications(listTags);
    }

    public static List<TagSpecification> translateTagsToTagSpecifications(final List<Tag> newTags) {
        if (newTags == null) {
            return null;
        }
        return Arrays.asList(TagSpecification.builder()
            .resourceType("transit-gateway")
            .tags(newTags).build());
    }

    public static java.util.Set<Tag> listToSet(final List<Tag> tags) {
        if(tags == null) return null;
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static List<Tag> difference(List<com.aws.ec2.transitgatewayattachment.Tag>  tags1, List<com.aws.ec2.transitgatewayattachment.Tag> tags2) {
        final List<Tag> sdkTags1 = TagUtils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = TagUtils.cfnTagsToSdkTags(tags2);
        return Sets.difference(TagUtils.listToSet(sdkTags1), TagUtils.listToSet(sdkTags2)).immutableCopy().asList();
    }

}
