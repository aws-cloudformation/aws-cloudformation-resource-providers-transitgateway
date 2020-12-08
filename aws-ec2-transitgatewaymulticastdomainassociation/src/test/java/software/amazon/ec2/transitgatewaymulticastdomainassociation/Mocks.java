package software.amazon.ec2.transitgatewaymulticastdomainassociation;

import org.apache.commons.lang3.SerializationUtils;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mocks {

    public Integer counter;
    public String TransitGatewayMulticastDomainId;
    public String SubnetId;
    public String TransitGatewayAttachmentId;
    public Mocks(
    ) {

        this.TransitGatewayAttachmentId = "tgw-attach-1231bc";
        this.SubnetId = "subnet-123abc";
        this.TransitGatewayMulticastDomainId = "tgw-mcast-domain-123abc";
        this.counter = 0;
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties(String state) {
        return ResourceModel.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .transitGatewayMulticastDomainId(this.TransitGatewayMulticastDomainId)
                .subnetId(this.SubnetId)
                .state(state)
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }


    public ResourceModel modelWithoutCreateOnlyProperties() {
        return this.modelWithoutCreateOnlyProperties("associated");
    }




    public  ResourceModel modelWithInvalidProperties(String state) {
        return ResourceModel.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .transitGatewayMulticastDomainId(this.TransitGatewayMulticastDomainId)
                .subnetId(this.SubnetId)
                .state(state)
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }


    public ResourceModel modelWithInvalidProperties() {
        return this.modelWithInvalidProperties("associated");
    }



    public  ResourceModel modelWithNullProperties(String state) {
        return ResourceModel.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .transitGatewayMulticastDomainId(this.TransitGatewayMulticastDomainId)
                .subnetId(null)
                .state(state)
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }


    public ResourceModel modelWithNullProperties() {
        return this.modelWithNullProperties("associated");
    }




    public  ResourceModel modelWithFakeProperties(String state) {
        return ResourceModel.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .transitGatewayMulticastDomainId(this.TransitGatewayMulticastDomainId)
                .subnetId(this.SubnetId)
                .state(state)
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }


    public ResourceModel modelWithFakeProperties() {
        return this.modelWithFakeProperties("associated");
    }




    public  ResourceModel model(String state) {
        return ResourceModel.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .transitGatewayMulticastDomainId(this.TransitGatewayMulticastDomainId)
                .subnetId(this.SubnetId)
                .state(state)
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }



    public ResourceModel model() {
        return this.model("associated");
    }



    public TransitGatewayMulticastDomainAssociation sdkModel(String state) {
        return TransitGatewayMulticastDomainAssociation.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .subnet(SubnetAssociation.builder().subnetId(this.SubnetId).state(state).build())
                .resourceId("resource-123-abc")
                .resourceType("vpc")
                .build();
    }



    public TransitGatewayMulticastDomainAssociation sdkModel() {
        return this.sdkModel("associated");
    }

    public GetTransitGatewayMulticastDomainAssociationsResponse describeResponse(String state) {
        return GetTransitGatewayMulticastDomainAssociationsResponse.builder()
            .multicastDomainAssociations(
               this.sdkModel(state)
            )
            .build();
    }


    public GetTransitGatewayMulticastDomainAssociationsResponse describeResponse() {
        return this.describeResponse( "associated");
    }

    public CreateTagsResponse createTagsResponse() {
        return CreateTagsResponse.builder().build();
    }

    public DeleteTagsResponse deleteTagsResponse() {
        return DeleteTagsResponse.builder().build();
    }

    public DisassociateTransitGatewayMulticastDomainResponse deleteResponse() {
        return DisassociateTransitGatewayMulticastDomainResponse.builder()
                .associations(TransitGatewayMulticastDomainAssociations.builder()
                        .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                        .subnets(SubnetAssociation.builder().subnetId(this.SubnetId).state("disassociated").build())
                        .resourceId("resource-123-abc")
                        .resourceType("vpc")
                .build())
            .build();
    }

    public AssociateTransitGatewayMulticastDomainResponse createResponse() {
        return AssociateTransitGatewayMulticastDomainResponse.builder()
                .associations(TransitGatewayMulticastDomainAssociations.builder()
                        .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                        .subnets(SubnetAssociation.builder().subnetId(this.SubnetId).state("associated").build())
                        .resourceId("resource-123-abc")
                        .resourceType("vpc")
                        .build())
            .build();
    }

    public GetTransitGatewayMulticastDomainAssociationsResponse emptyReadResponse() {
        java.util.List<TransitGatewayMulticastDomainAssociation> emptyMulticastAssociations = new ArrayList<>();
        return GetTransitGatewayMulticastDomainAssociationsResponse.builder()
                .multicastDomainAssociations(emptyMulticastAssociations)
                .build();
    }




}
