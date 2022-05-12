package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.TagUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String TGW_ID;
    public String VPC_ID;
    public List<String> subnetId;
    public Instant currentTime;
    public Integer counter;
    public  TransitGatewayVpcAttachmentOptions opt;
    public Mocks(
    ) {
        this.primaryIdentifier = "tgw-attach-02bb79002EXAMPLE";
        this.TGW_ID = "tgw-0d88d2d0d5EXAMPLEx";
        this.VPC_ID = "vpc-123";
        this.subnetId = new ArrayList<>();
        subnetId.add("subnet-e4f648c5");
        this.currentTime = Instant.now();
        this.counter = 0;
        this.opt = TransitGatewayVpcAttachmentOptions.builder().dnsSupport("disable").applianceModeSupport("disable").ipv6Support("disable").build();

    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .id(this.primaryIdentifier)
                .vpcId(this.VPC_ID)
                .transitGatewayId(this.TGW_ID)
                .subnetIds(this.subnetId)
                .tags(TagUtils.sdkTagsToCfnTags(tags)).options(Options.builder().ipv6Support("disable").applianceModeSupport("disable").dnsSupport("disable").build())
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
                .state(state)
                .tags(tags)
                .options(this.opt)
                .build();
    }
    public TransitGatewayVpcAttachment sdkModel(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.sdkModel(tags, state);
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

    public CreateTransitGatewayVpcAttachmentResponse createResponse(List<Tag> tags) {
        return this.createResponse(tags, "available");
    }

    public DescribeTransitGatewayVpcAttachmentsResponse emptyReadResponse() {
        return DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(new ArrayList<>())
                .build();
    }

}
