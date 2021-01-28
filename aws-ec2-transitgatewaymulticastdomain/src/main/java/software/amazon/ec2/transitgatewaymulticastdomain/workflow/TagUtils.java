package software.amazon.ec2.transitgatewaymulticastdomain.workflow;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagUtils {
    public static List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if(tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
            .map(e -> software.amazon.ec2.transitgatewaymulticastdomain.Tag.builder()
                .key(e.key())
                .value(e.value())
                .build())
            .collect(Collectors.toList());
    }

    public static List<Tag> cfnTagsToSdkTags(final List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
            .map(e -> Tag.builder()
                .key(e.getKey())
                .value(e.getValue())
                .build())
            .collect(Collectors.toList());
    }


    public static List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> mergeResourceModelAndStackTags(List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> modelTags, Map<String, String> stackTags) {
        List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> tags = new ArrayList<software.amazon.ec2.transitgatewaymulticastdomain.Tag>();
        for (Map.Entry<String, String> entry : stackTags.entrySet()) {
            software.amazon.ec2.transitgatewaymulticastdomain.Tag tag = software.amazon.ec2.transitgatewaymulticastdomain.Tag.builder().key(entry.getKey()).value(entry.getValue()).build();
            tags.add(tag);
        }
        return Stream.concat(modelTags.stream(), tags.stream())
            .collect(Collectors.toList());
    }


    public static List<TagSpecification> cfnTagsToSdkTagSpecifications(final List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> tags) {
         if(tags == null) return null;
        List<Tag> listTags = TagUtils.cfnTagsToSdkTags(tags);
        return TagUtils.translateTagsToTagSpecifications(listTags);
    }

    public static List<TagSpecification> translateTagsToTagSpecifications(final List<Tag> tags) {
        return Arrays.asList(TagSpecification.builder()
            .resourceType("transit-gateway-multicast-domain")
            .tags(tags).build());
    }

    public static Set<Tag> listToSet(final List<Tag> tags) {
        return CollectionUtils.isEmpty(tags) ? new HashSet<>() : new HashSet<>(tags);
    }

    public static List<Tag> difference(List<software.amazon.ec2.transitgatewaymulticastdomain.Tag>  tags1, List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> tags2) {
        final List<Tag> sdkTags1 = TagUtils.cfnTagsToSdkTags(tags1);
        final List<Tag> sdkTags2 = TagUtils.cfnTagsToSdkTags(tags2);
        return Sets.difference(TagUtils.listToSet(sdkTags1), TagUtils.listToSet(sdkTags2)).immutableCopy().asList();
    }

}
