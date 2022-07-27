package com.aws.ec2.transitgateway;

import com.aws.ec2.transitgateway.workflow.TagUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public Instant currentTime;
    public Integer counter;
    public List<String> cidrBlock = new ArrayList<>();
    public Mocks(
    ) {
        this.primaryIdentifier = "tgw-0d88d2d0d5EXAMPLE";;
        this.currentTime = Instant.now();
        this.counter = 0;
        this.cidrBlock.add("1.2.3.2/24");
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(List<Tag> tags, String state) {
        return ResourceModel.builder()
                .id(this.primaryIdentifier)
                .tags(TagUtils.sdkTagsToCfnTags(tags))
                .amazonSideAsn(Long.valueOf(6541))
                .autoAcceptSharedAttachments("enable")
                .defaultRouteTableAssociation("enable")
                .defaultRouteTablePropagation("enable")
                .description("abc")
                .dnsSupport("enable")
                .multicastSupport("disable")
                .vpnEcmpSupport("disable")
                .transitGatewayCidrBlocks(cidrBlock)
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


    public  ResourceModel model(List<Tag> tags, String state) {
        return ResourceModel.builder()
            .id(this.primaryIdentifier)
                .amazonSideAsn(Long.valueOf(6541))
                .autoAcceptSharedAttachments("enable")
                .defaultRouteTableAssociation("enable")
                .defaultRouteTablePropagation("enable")
                .propagationDefaultRouteTableId("rout-table-123")
                .associationDefaultRouteTableId("rout-table-123")
                .dnsSupport("enable")
                .multicastSupport("disable")
                .vpnEcmpSupport("enable")
                .transitGatewayCidrBlocks(cidrBlock)
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

    public TransitGateway sdkModel(List<Tag> tags, String state) {
        return TransitGateway.builder()
                .transitGatewayId(this.primaryIdentifier)
                .state(state)
                .options(this.transitGatewayOptions())
                .tags(tags)
                .build();
    }

    public TransitGatewayOptions transitGatewayOptions(){
       return TransitGatewayOptions.builder()
                .amazonSideAsn(Long.valueOf(6541))
                .associationDefaultRouteTableId("rout-table-123")
                .autoAcceptSharedAttachments("enable")
                .defaultRouteTableAssociation("enable")
                .defaultRouteTablePropagation("enable")
                .dnsSupport("enable")
                .multicastSupport("disable")
                .vpnEcmpSupport("enable")
                .propagationDefaultRouteTableId("rout-table-123")
                .transitGatewayCidrBlocks(cidrBlock)
                .build();

    }


    public TransitGateway sdkModel(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.sdkModel(tags, state);
    }

    public TransitGateway sdkModel(List<Tag> tags) {
        return this.sdkModel(tags, "available");
    }

    public TransitGateway sdkModel() {
        return this.sdkModel(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewaysResponse describeResponse(List<Tag> tags, String state) {
        return DescribeTransitGatewaysResponse.builder()
            .transitGateways(
               this.sdkModel(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewaysResponse describeResponse(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.describeResponse(tags, state);
    }

    public DescribeTransitGatewaysResponse describeResponse(List<Tag> tags) {
        return this.describeResponse(tags, "available");
    }

    public DescribeTransitGatewaysResponse describeResponse() {
        return this.describeResponse(new ArrayList<>(), "available");
    }


    public CreateTagsResponse createTagsResponse() {
        return CreateTagsResponse.builder().build();
    }

    public DeleteTagsResponse deleteTagsResponse() {
        return DeleteTagsResponse.builder().build();
    }

    public DeleteTransitGatewayResponse deleteResponse() {
        return DeleteTransitGatewayResponse.builder()
            .transitGateway(this.sdkModel())
            .build();
    }


    public CreateTransitGatewayResponse createResponse(List<Tag> tags, String state) {
        return CreateTransitGatewayResponse.builder()
                .transitGateway(
                        this.sdkModel(tags, state)
                )
                .build();
    }


    public CreateTransitGatewayResponse createResponse(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.createResponse(tags, state);
    }

    public CreateTransitGatewayResponse createResponse(List<Tag> tags) {
        return this.createResponse(tags, "available");
    }

    public CreateTransitGatewayResponse createResponse() {
        return this.createResponse(new ArrayList<>(), "available");
    }


}
