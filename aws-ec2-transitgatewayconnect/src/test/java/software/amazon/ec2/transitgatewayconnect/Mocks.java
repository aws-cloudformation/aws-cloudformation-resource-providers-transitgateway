package software.amazon.ec2.transitgatewayconnect;

import software.amazon.ec2.transitgatewayconnect.workflow.TagUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String parentIdentifier;
    public Instant currentTime;
    public Integer counter;
    public String protocol;
    public Mocks(
    ) {
        this.primaryIdentifier = "tgw-mcast-domain-02bb79002EXAMPLE";
        this.parentIdentifier = "tgw-0d88d2d0d5EXAMPLE";;
        this.currentTime = Instant.now();
        this.counter = 0;
        this.protocol = "gre";
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .creationTime(this.currentTime.toString())
                .state(state)
                .options(TransitGatewayConnectOptions.builder().protocol(this.protocol).build())
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }


    public ResourceModel modelWithoutCreateOnlyProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, "available");
    }

    public ResourceModel modelWithoutCreateOnlyProperties(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutCreateOnlyProperties(tags, state);
    }

    public ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags) {
        return this.modelWithoutCreateOnlyProperties(tags, "available");
    }


    public  ResourceModel modelWithInvalidProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .creationTime(this.currentTime.toString())
                .state(state)
                .transitGatewayId(this.parentIdentifier)
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }


    public ResourceModel modelWithInvalidProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithInvalidProperties(tags, "available");
    }

    public ResourceModel modelWithInvalidProperties(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithInvalidProperties(tags, state);
    }

    public ResourceModel modelWithInvalidProperties(List<Tag> tags) {
        return this.modelWithInvalidProperties(tags, "available");
    }

    public  ResourceModel modelWithNullProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .creationTime(this.currentTime.toString())
                .state(state)
                .transitGatewayId(null)
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }


    public ResourceModel modelWithNullProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithNullProperties(tags, "available");
    }

    public ResourceModel modelWithNullProperties(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithNullProperties(tags, state);
    }

    public ResourceModel modelWithNullProperties(List<Tag> tags) {
        return this.modelWithNullProperties(tags, "available");
    }

    public  ResourceModel modelWithFakeProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .creationTime(this.currentTime.toString())
                .state(state)
                .transitGatewayId(null)
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }


    public ResourceModel modelWithFakeProperties() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithFakeProperties(tags, "available");
    }

    public ResourceModel modelWithFakeProperties(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithFakeProperties(tags, state);
    }

    public ResourceModel modelWithFakeProperties(List<Tag> tags) {
        return this.modelWithFakeProperties(tags, "available");
    }


    public  ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .creationTime(this.currentTime.toString())
                .state(state)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutPrimaryIdentifier(tags, "available");
    }

    public ResourceModel modelWithoutPrimaryIdentifier(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutPrimaryIdentifier(tags, state);
    }

    public ResourceModel modelWithoutPrimaryIdentifier(List<Tag> tags) {
        return this.modelWithoutPrimaryIdentifier(tags, "available");
    }




    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
            .transitGatewayId(this.parentIdentifier)
            .options(TransitGatewayConnectOptions.builder().protocol(this.protocol).build())
            .creationTime(this.currentTime.toString())
            .state(state)
            .transitGatewayAttachmentId(this.primaryIdentifier)
            .tags(TagUtils.sdkTagsToCfnTags(tags))
            .build();
    }



    public ResourceModel model() {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, "available");
    }

    public ResourceModel model(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.model(tags, state);
    }

    public ResourceModel model(List<Tag> tags) {
        return this.model(tags, "available");
    }

    public Tag tag(String key, String value) {
        return Tag.builder().key(key).value(value).build();
    }

    public Tag tag() {
        this.counter++;
        return this.tag("KEY_" + this.counter, "VALUE_" + this.counter);
    }


    public TransitGatewayConnect sdkModel(List<Tag> tags, String state) {
        return TransitGatewayConnect.builder()
                .transitGatewayId(this.parentIdentifier)
                .creationTime(this.currentTime)
                .options(software.amazon.awssdk.services.ec2.model.TransitGatewayConnectOptions.builder().protocol(this.protocol).build())
                .state(state)
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .tags(tags)
                .build();
    }


    public TransitGatewayConnect sdkModel(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.sdkModel(tags, state);
    }

    public TransitGatewayConnect sdkModel(List<Tag> tags) {
        return this.sdkModel(tags, "available");
    }

    public TransitGatewayConnect sdkModel() {
        return this.sdkModel(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewayConnectsResponse describeResponse(List<Tag> tags, String state) {
        return DescribeTransitGatewayConnectsResponse.builder()
            .transitGatewayConnects(
               this.sdkModel(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewayConnectsResponse describeResponse(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.describeResponse(tags, state);
    }

    public DescribeTransitGatewayConnectsResponse describeResponse(List<Tag> tags) {
        return this.describeResponse(tags, "available");
    }

    public DescribeTransitGatewayConnectsResponse describeResponse() {
        return this.describeResponse(new ArrayList<>(), "available");
    }

    public CreateTagsResponse createTagsResponse() {
        return CreateTagsResponse.builder().build();
    }

    public DeleteTagsResponse deleteTagsResponse() {
        return DeleteTagsResponse.builder().build();
    }

    public DeleteTransitGatewayConnectResponse deleteResponse() {
        return DeleteTransitGatewayConnectResponse.builder()
            .transitGatewayConnect(this.sdkModel())
            .build();
    }

    public CreateTransitGatewayConnectResponse createResponse() {
        return CreateTransitGatewayConnectResponse.builder()
            .transitGatewayConnect(this.sdkModel())
            .build();
    }



}
