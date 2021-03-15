package software.amazon.ec2.transitgatewayconnectpeer.workflow;

import software.amazon.ec2.transitgatewayconnectpeer.AbstractTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TagUtilsTest extends AbstractTestBase {

    @Test
    public void instance() {
        assertThat(new TagUtils().toString().contains("TagUtils")).isTrue();
    }

    @Test
    public void sdkTagsToCfnTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.tag());
        final List<software.amazon.ec2.transitgatewayconnectpeer.Tag> cfnTags = TagUtils.sdkTagsToCfnTags(sdkTags);
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
    }

    @Test
    public void sdkTagsToCfnTagsWhenNull() {
        final List<software.amazon.ec2.transitgatewayconnectpeer.Tag> cfnTags = TagUtils.sdkTagsToCfnTags(null);
        assertThat(cfnTags.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTags() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.tag());
        final List<software.amazon.ec2.transitgatewayconnectpeer.Tag> cfnTags = TagUtils.sdkTagsToCfnTags(sdkTags);
        List<Tag> sdkTagsConvertedBack = TagUtils.cfnTagsToSdkTags(cfnTags);
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTagsConvertedBack.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTagsConvertedBack.get(0).value());
    }

    @Test
    public void cfnTagsToSdkTagsWhenNull() {
        List<Tag> sdkTagsConvertedBack = TagUtils.cfnTagsToSdkTags(null);
        assertThat(sdkTagsConvertedBack.size()).isEqualTo(0);
    }

    @Test
    public void cfnTagsToSdkTagSpecifications() {
        final List<Tag> sdkTags = new ArrayList<>();
        sdkTags.add(MOCKS.tag());
        final List<software.amazon.ec2.transitgatewayconnectpeer.Tag> cfnTags = TagUtils.sdkTagsToCfnTags(sdkTags);
        List<TagSpecification> sdkTagsConvertedToTagSpecifications = TagUtils.cfnTagsToSdkTagSpecifications(cfnTags);
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTags.get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTags.get(0).value());
        assertThat(cfnTags.get(0).getKey()).isEqualTo(sdkTagsConvertedToTagSpecifications.get(0).tags().get(0).key());
        assertThat(cfnTags.get(0).getValue()).isEqualTo(sdkTagsConvertedToTagSpecifications.get(0).tags().get(0).value());
    }

    @Test
    public void difference1() {
        Tag sharedTag = MOCKS.tag();
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.tag());

        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.tag());
        List<Tag> difference = TagUtils.difference(TagUtils.sdkTagsToCfnTags(tags1), TagUtils.sdkTagsToCfnTags(tags2));
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags1.get(1).key());
        assertThat(difference.get(0).value()).isEqualTo(tags1.get(1).value());
    }

    @Test
    public void difference2() {
        Tag sharedTag = MOCKS.tag();
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);
        tags1.add(MOCKS.tag());

        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(sharedTag);
        tags2.add(MOCKS.tag());
        List<Tag> difference = TagUtils.difference(TagUtils.sdkTagsToCfnTags(tags2), TagUtils.sdkTagsToCfnTags(tags1));
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(1).key());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(1).value());
    }

    @Test
    public void difference3() {
        Tag sharedTag = MOCKS.tag();
        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(sharedTag);


        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.tag(sharedTag.key(), "Something Else"));
        List<Tag> difference = TagUtils.difference(TagUtils.sdkTagsToCfnTags(tags2), TagUtils.sdkTagsToCfnTags(tags1));
        assertThat(difference.size()).isEqualTo(1);
        assertThat(difference.get(0).key()).isEqualTo(tags2.get(0).key());
        assertThat(difference.get(0).value()).isEqualTo(tags2.get(0).value());
        assertThat(difference.get(0).value()).isNotEqualTo(tags1.get(0).value());
    }

}
