package com.aws.ec2.transitgatewayvpcattachment.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagUtils {
    public static List<com.aws.ec2.transitgatewayvpcattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) return new ArrayList<>();
        return tags.stream()
            .map(e -> com.aws.ec2.transitgatewayvpcattachment.Tag.builder()
                .key(e.key())
                .value(e.value())
                .build())
            .collect(Collectors.toList());
    }

    public static List<Tag> cfnTagsToSdkTags(final List<com.aws.ec2.transitgatewayvpcattachment.Tag> tags) {
        if (tags == null) {
            return new ArrayList<Tag>();
        }
        return tags.stream()
            .map(e -> Tag.builder()
                .key(e.getKey())
                .value(e.getValue())
                .build())
            .collect(Collectors.toList());
    }

    public static List<TagSpecification> cfnTagsToSdkTagSpecifications(final List<com.aws.ec2.transitgatewayvpcattachment.Tag> tags) {

       if(tags == null) return null;
        List<Tag> listTags = TagUtils.cfnTagsToSdkTags(tags);
        return TagUtils.translateTagsToTagSpecifications(listTags);
    }

    public static List<TagSpecification> translateTagsToTagSpecifications(final List<Tag> newTags) {
        if (newTags == null) {
            return null;
        }
        return Arrays.asList(TagSpecification.builder()
            .resourceType("transit-gateway-attachment")
            .tags(newTags).build());
    }
    public static Set<Tag> listToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }


    public static List<com.aws.ec2.transitgatewayvpcattachment.Tag> mergeResourceModelAndStackTags(List<com.aws.ec2.transitgatewayvpcattachment.Tag> modelTags, Map<String, String> stackTags) {
        if(modelTags == null || modelTags.isEmpty()) {
            modelTags = new ArrayList<com.aws.ec2.transitgatewayvpcattachment.Tag>();
        }
        List<com.aws.ec2.transitgatewayvpcattachment.Tag> tags = new ArrayList<com.aws.ec2.transitgatewayvpcattachment.Tag>();
        if(stackTags!= null)
        for (Map.Entry<String, String> entry : stackTags.entrySet()) {
            com.aws.ec2.transitgatewayvpcattachment.Tag tag = com.aws.ec2.transitgatewayvpcattachment.Tag.builder().key(entry.getKey()).value(entry.getValue()).build();
            boolean isPresent = false;
            for (com.aws.ec2.transitgatewayvpcattachment.Tag t : modelTags) {
                if (t.getKey().equals(entry.getKey())) {
                    isPresent = true;
                    break;
                }
            }
            if(!isPresent)
                tags.add(tag);
            tags.add(tag);
        }
        if(tags.isEmpty()) {
            return modelTags;
        } else if(modelTags == null || modelTags.isEmpty()) {
            return tags;
        } else {
            for(Tag a: )

            return Stream.concat(modelTags.stream(), tags.stream())
                    .collect(Collectors.toList());
        }

    }

    public static List<Tag> difference(List<com.aws.ec2.transitgatewayvpcattachment.Tag> currTags, List<com.aws.ec2.transitgatewayvpcattachment.Tag> prevTags) {
            final List<Tag> sdkTags1 = TagUtils.cfnTagsToSdkTags(currTags);
            final List<Tag> sdkTags2 = TagUtils.cfnTagsToSdkTags(prevTags);
            return Sets.difference(TagUtils.listToSet(sdkTags1), TagUtils.listToSet(sdkTags2)).immutableCopy().asList();
    }
}
