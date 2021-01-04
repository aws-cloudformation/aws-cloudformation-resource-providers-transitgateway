package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ec2.transitgatewaymulticastdomain.workflow.TagUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mocks {
    public String primaryIdentifier;
    public String parentIdentifier;
    public Instant currentTime;
    public Integer counter;
    public Mocks(
    ) {
        this.primaryIdentifier = "tgw-mcast-domain-02bb79002EXAMPLE";
        this.parentIdentifier = "tgw-0d88d2d0d5EXAMPLE";;
        this.currentTime = Instant.now();
        this.counter = 0;
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
                .options(Options.builder()
                    .autoAcceptSharedAssociations("disable")
                    .igmpv2Support("disable")
                    .staticSourcesSupport("disable")
                    .build()
                )
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
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
                .options(Options.builder()
                    .autoAcceptSharedAssociations("disable")
                    .igmpv2Support("disable")
                    .staticSourcesSupport("disable")
                    .build()
                )
                .transitGatewayId(this.parentIdentifier)
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
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
                .options(Options.builder()
                    .autoAcceptSharedAssociations("disable")
                    .igmpv2Support("disable")
                    .staticSourcesSupport("disable")
                    .build()
                )
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
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
                .options(Options.builder()
                    .autoAcceptSharedAssociations("disable")
                    .igmpv2Support("disable")
                    .staticSourcesSupport("disable")
                    .build()
                )
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
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
                .options(Options.builder()
                    .autoAcceptSharedAssociations("disable")
                    .igmpv2Support("disable")
                    .staticSourcesSupport("disable")
                    .build()
                )
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
            .creationTime(this.currentTime.toString())
            .state(state)
            .options(Options.builder()
                .autoAcceptSharedAssociations("disable")
                .igmpv2Support("disable")
                .staticSourcesSupport("disable")
                .build()
            )
            .transitGatewayMulticastDomainId(this.primaryIdentifier)
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


    public  ResourceModel modelWithoutOptions(List<Tag> tags, String state) {
        return ResourceModel.builder()
            .transitGatewayId(this.parentIdentifier)
            .creationTime(this.currentTime.toString())
            .state(state)
            .options(Options.builder()
                .build()
            )
            .transitGatewayMulticastDomainId(this.primaryIdentifier)
            .tags(TagUtils.sdkTagsToCfnTags(tags))
            .build();
    }


    public ResourceModel modelWithoutOptions() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutOptions(tags, "available");
    }

    public ResourceModel modelWithoutOptions(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithoutOptions(tags, state);
    }

    public ResourceModel modelWithoutOptions(List<Tag> tags) {
        return this.modelWithoutOptions(tags, "available");
    }



    public  ResourceModel modelWithNonDefaultOptions(List<Tag> tags, String state) {
        return ResourceModel.builder()
            .transitGatewayId(this.parentIdentifier)
            .creationTime(this.currentTime.toString())
            .state(state)
            .options(Options.builder()
                .autoAcceptSharedAssociations("enable")
                .igmpv2Support("enable")
                .staticSourcesSupport("enable")
                .build()
            )
            .transitGatewayMulticastDomainId(this.primaryIdentifier)
            .tags(TagUtils.sdkTagsToCfnTags(tags))
            .build();
    }

    public ResourceModel modelWithNonDefaultOptions() {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithNonDefaultOptions(tags, "available");
    }

    public ResourceModel modelWithNonDefaultOptions(String state) {
        final List<Tag> tags = new ArrayList<>();
        return this.modelWithNonDefaultOptions(tags, state);
    }

    public ResourceModel modelWithNonDefaultOptions(List<Tag> tags) {
        return this.modelWithNonDefaultOptions(tags, "available");
    }


    public Tag tag(String key, String value) {
        return Tag.builder().key(key).value(value).build();
    }

    public Tag tag() {
        this.counter++;
        return this.tag("KEY_" + this.counter, "VALUE_" + this.counter);
    }

    public TransitGatewayMulticastDomain sdkModel(List<Tag> tags, String state) {
        return TransitGatewayMulticastDomain.builder()
            .transitGatewayId(this.parentIdentifier)
            .creationTime(this.currentTime)
            .state(state)
            .options(TransitGatewayMulticastDomainOptions.builder()
                .autoAcceptSharedAssociations("disable")
                .igmpv2Support("disable")
                .staticSourcesSupport("disable")
                .build())
            .transitGatewayMulticastDomainId(this.primaryIdentifier)
            .tags(tags)
            .build();
    }


    public TransitGatewayMulticastDomain sdkModel(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.sdkModel(tags, state);
    }

    public TransitGatewayMulticastDomain sdkModel(List<Tag> tags) {
        return this.sdkModel(tags, "available");
    }

    public TransitGatewayMulticastDomain sdkModel() {
        return this.sdkModel(new ArrayList<>(), "available");
    }

    public TransitGatewayMulticastDomain sdkModelWithNonDefaultOptions(List<Tag> tags, String state) {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(this.parentIdentifier)
                .creationTime(this.currentTime)
                .state(state)
                .options(TransitGatewayMulticastDomainOptions.builder()
                    .autoAcceptSharedAssociations("enable")
                    .igmpv2Support("enable")
                    .staticSourcesSupport("enable")
                    .build())
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
                .tags(tags)
                .build();
    }

    public TransitGatewayMulticastDomain sdkModelWithoutOptions(List<Tag> tags, String state) {
        return TransitGatewayMulticastDomain.builder()
                .transitGatewayId(this.parentIdentifier)
                .creationTime(this.currentTime)
                .state(state)
                .options(TransitGatewayMulticastDomainOptions.builder()
                    .build())
                .transitGatewayMulticastDomainId(this.primaryIdentifier)
                .tags(tags)
                .build();
    }


    public TransitGatewayMulticastDomain sdkModelWithNonDefaultOptions(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.sdkModelWithNonDefaultOptions(tags, state);
    }

    public TransitGatewayMulticastDomain sdkModelWithNonDefaultOptions(List<Tag> tags) {
        return this.sdkModelWithNonDefaultOptions(tags, "available");
    }

    public TransitGatewayMulticastDomain sdkModelWithNonDefaultOptions() {
        return this.sdkModelWithNonDefaultOptions(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponse(List<Tag> tags, String state) {
        return DescribeTransitGatewayMulticastDomainsResponse.builder()
            .transitGatewayMulticastDomains(
               this.sdkModel(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewayMulticastDomainsResponse describeResponse(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.describeResponse(tags, state);
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponse(List<Tag> tags) {
        return this.describeResponse(tags, "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponse() {
        return this.describeResponse(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithNonDefaultOptions(List<Tag> tags, String state) {
        return DescribeTransitGatewayMulticastDomainsResponse.builder()
            .transitGatewayMulticastDomains(
               this.sdkModelWithNonDefaultOptions(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithNonDefaultOptions(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.describeResponseWithNonDefaultOptions(tags, state);
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithNonDefaultOptions(List<Tag> tags) {
        return this.describeResponseWithNonDefaultOptions(tags, "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithNonDefaultOptions() {
        return this.describeResponseWithNonDefaultOptions(new ArrayList<>(), "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithoutOptions(List<Tag> tags, String state) {
        return DescribeTransitGatewayMulticastDomainsResponse.builder()
            .transitGatewayMulticastDomains(
               this.sdkModelWithoutOptions(tags, state)
            )
            .build();
    }


    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithoutOptions(String state) {
        final List<software.amazon.awssdk.services.ec2.model.Tag> tags = new ArrayList<>();
        return this.describeResponseWithoutOptions(tags, state);
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithoutOptions(List<Tag> tags) {
        return this.describeResponseWithoutOptions(tags, "available");
    }

    public DescribeTransitGatewayMulticastDomainsResponse describeResponseWithoutOptions() {
        return this.describeResponseWithoutOptions(new ArrayList<>(), "available");
    }

    public CreateTagsResponse createTagsResponse() {
        return CreateTagsResponse.builder().build();
    }

    public DeleteTagsResponse deleteTagsResponse() {
        return DeleteTagsResponse.builder().build();
    }

    public DeleteTransitGatewayMulticastDomainResponse deleteResponse() {
        return DeleteTransitGatewayMulticastDomainResponse.builder()
            .transitGatewayMulticastDomain(this.sdkModel())
            .build();
    }

    public CreateTransitGatewayMulticastDomainResponse createResponse() {
        return CreateTransitGatewayMulticastDomainResponse.builder()
            .transitGatewayMulticastDomain(this.sdkModel())
            .build();
    }



}
