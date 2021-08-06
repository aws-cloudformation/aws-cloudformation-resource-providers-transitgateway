package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.TagUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Mocks {
    public String primaryIdentifier;
    public String TGW_ID;
    public String VPC_ID;
    public List<String> subnetId;
    public Instant currentTime;
    public Integer counter;
    public Mocks(
    ) {
        this.primaryIdentifier = "tgw-attach-02bb79002EXAMPLE";
        this.TGW_ID = "tgw-0d88d2d0d5EXAMPLEx";
        this.VPC_ID = "vpc-123";
        this.subnetId = new ArrayList<>();
        subnetId.add("subnet-e4f648c5");
        this.currentTime = Instant.now();
        this.counter = 0;
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model).region("us-east-1")
            .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .id(this.primaryIdentifier)
                .vpcId(this.VPC_ID)
                .transitGatewayId(this.TGW_ID)
                .subnetIds(this.subnetId)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .build();
    }


    public Options transitGatewayVpcAttachmentOptions(){
        return Options.builder()
                .applianceModeSupport("disable")
                .dnsSupport("disable")
                .ipv6Support("disable").build();
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


    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
            .id(this.primaryIdentifier)
            .vpcId(this.VPC_ID)
            .transitGatewayId(this.TGW_ID)
            .subnetIds(this.subnetId)
              //  .addSubnetIds(new ArrayList<>())
            //    .removeSubnetIds(new ArrayList<>())
            .tags(TagUtils.sdkTagsToCfnTags(tags))
           //     .options(this.transitGatewayVpcAttachmentOptions())
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

    public TransitGatewayVpcAttachment sdkModel(List<Tag> tags, String state) {
        return TransitGatewayVpcAttachment.builder()
                .transitGatewayAttachmentId(this.primaryIdentifier)
                .vpcId(this.VPC_ID)
                .transitGatewayId(this.TGW_ID)
                .subnetIds(this.subnetId).tags(tags)

                      //  tags(TagUtils.sdkTagsToCfnTags(tags))
             //   .options(this.options())
                .state(state)
                .tags(tags)
                .build();
    }

    public TransitGatewayVpcAttachmentOptions options(){
        return TransitGatewayVpcAttachmentOptions.builder().dnsSupport("disable").build();
    }


    public TransitGatewayVpcAttachment sdkModel(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.sdkModel(tags, state);
    }

    public TransitGatewayVpcAttachment sdkModel(List<Tag> tags) {
        return this.sdkModel(tags, "available");
    }

    public TransitGatewayVpcAttachment sdkModel() {
        return this.sdkModel(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewayVpcAttachmentsResponse describeResponse(List<Tag> tags, String state) {
        return DescribeTransitGatewayVpcAttachmentsResponse.builder()
            .transitGatewayVpcAttachments(
               this.sdkModel(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewayVpcAttachmentsResponse describeResponse(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.describeResponse(tags, state);
    }

    public DescribeTransitGatewayVpcAttachmentsResponse describeResponse(List<Tag> tags) {
        return this.describeResponse(tags, "available");
    }

    public DescribeTransitGatewayVpcAttachmentsResponse describeResponse() {
        return this.describeResponse(new ArrayList<>(), "available");
    }



    public CreateTagsResponse createTagsResponse() {
        return CreateTagsResponse.builder().build();
    }

    public DeleteTagsResponse deleteTagsResponse() {
        return DeleteTagsResponse.builder().build();
    }

    public DeleteTransitGatewayVpcAttachmentResponse deleteResponse() {
        return DeleteTransitGatewayVpcAttachmentResponse.builder()
            .transitGatewayVpcAttachment(this.sdkModel())
            .build();
    }


    public CreateTransitGatewayVpcAttachmentResponse createResponse(List<Tag> tags, String state) {
        return CreateTransitGatewayVpcAttachmentResponse.builder()
                .transitGatewayVpcAttachment(
                        this.sdkModel(tags, state)
                )
                .build();
    }


    public CreateTransitGatewayVpcAttachmentResponse createResponse(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.createResponse(tags, state);
    }

    public CreateTransitGatewayVpcAttachmentResponse createResponse(List<Tag> tags) {
        return this.createResponse(tags, "available");
    }

    public CreateTransitGatewayVpcAttachmentResponse createResponse() {
        return this.createResponse(new ArrayList<>(), "available");
    }

    public ModifyTransitGatewayVpcAttachmentResponse modifyResponse(List<Tag> tags, String state) {
        return ModifyTransitGatewayVpcAttachmentResponse.builder()
                .transitGatewayVpcAttachment(
                        this.sdkModel(tags, state)
                )
                .build();
    }

    public ModifyTransitGatewayVpcAttachmentResponse modifyResponse(List<Tag> tags) {
        return this.modifyResponse(tags, "available");
    }

    public ModifyTransitGatewayVpcAttachmentResponse modifyResponse(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modifyResponse(tags, state);
    }
}
