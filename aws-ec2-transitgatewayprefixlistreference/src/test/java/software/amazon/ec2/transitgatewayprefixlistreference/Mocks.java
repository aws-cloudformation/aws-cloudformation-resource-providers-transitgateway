package software.amazon.ec2.transitgatewayprefixlistreference;

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
    public String routTableId;
    public Boolean blackhole;
    public Mocks(
    ) {
        this.primaryIdentifier = "prefix-list-123abc";
        this.parentIdentifier = "tgw-attach-0d88d2d0d5EXAMPLE";
        this.routTableId = "rt-123abc";
        this.currentTime = Instant.now();
        this.counter = 0;
        this.blackhole = false;
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
    }

    public  ResourceModel modelWithoutCreateOnlyProperties( String state) {
        return ResourceModel.builder()
                .state(state)
                .prefixListId(this.primaryIdentifier)
                .build();
    }


    public ResourceModel modelWithoutCreateOnlyProperties() {
        return this.modelWithoutCreateOnlyProperties("available");
    }





    public  ResourceModel modelWithInvalidProperties(String state) {
        return ResourceModel.builder()
                .state(state)
                .prefixListId(this.primaryIdentifier)
                .transitGatewayAttachmentId(this.parentIdentifier)
                .transitGatewayRouteTableId(this.routTableId)
                .build();
    }


    public ResourceModel modelWithInvalidProperties() {

        return this.modelWithInvalidProperties("available");
    }




    public  ResourceModel modelWithNullProperties(String state) {
        return ResourceModel.builder()
                .state(state)
                .prefixListId(this.primaryIdentifier)
                .transitGatewayAttachmentId(this.parentIdentifier)
                .transitGatewayRouteTableId(this.routTableId)
                .build();
    }


    public ResourceModel modelWithNullProperties() {
        return this.modelWithNullProperties("available");
    }



    public  ResourceModel modelWithFakeProperties(String state) {
        return ResourceModel.builder()
                .state(state)
                .transitGatewayAttachmentId(this.parentIdentifier)
                .prefixListId(this.primaryIdentifier)
                .transitGatewayRouteTableId(this.routTableId)
                .build();
    }


    public ResourceModel modelWithFakeProperties() {
        return this.modelWithFakeProperties( "available");
    }




    public  ResourceModel modelWithoutPrimaryIdentifier( String state) {
        return ResourceModel.builder()
                .state(state)
                .build();
    }

    public ResourceModel modelWithoutPrimaryIdentifier() {
        return this.modelWithoutPrimaryIdentifier("available");
    }






    public  ResourceModel model(String state) {
        return ResourceModel.builder()
            .transitGatewayAttachmentId(this.parentIdentifier)
            .state(state)
            .blackhole(this.blackhole)
            .prefixListOwnerId("1234")
            .transitGatewayRouteTableId("rt-123abc")
            .prefixListId(this.primaryIdentifier)
            .build();
    }



    public ResourceModel model() {
        return this.model("available");
    }


    public Tag tag(String key, String value) {
        return Tag.builder().key(key).value(value).build();
    }

    public Tag tag() {
        this.counter++;
        return this.tag("KEY_" + this.counter, "VALUE_" + this.counter);
    }

    public TransitGatewayPrefixListReference sdkModel(String state) {
        return TransitGatewayPrefixListReference.builder()
                .state(state)
                .blackhole(this.blackhole)
                .prefixListOwnerId("1234")
                .transitGatewayRouteTableId("rt-123abc")
                .transitGatewayAttachment(TransitGatewayPrefixListAttachment.builder().transitGatewayAttachmentId(this.parentIdentifier).build())
                .prefixListId(this.primaryIdentifier)
                .build();
    }



    public TransitGatewayPrefixListReference sdkModel() {
        return this.sdkModel("available");
    }

    public TransitGatewayPrefixListReference deletedSdkModel() {
        return this.sdkModel(null);
    }

    public GetTransitGatewayPrefixListReferencesResponse describeResponse(String state) {
        return GetTransitGatewayPrefixListReferencesResponse.builder()
            .transitGatewayPrefixListReferences(
               this.sdkModel( state)
            )
            .build();
    }



    public GetTransitGatewayPrefixListReferencesResponse describeResponse() {
        return this.describeResponse("available");
    }



    public DeleteTransitGatewayPrefixListReferenceResponse deleteResponse() {
        return DeleteTransitGatewayPrefixListReferenceResponse.builder()
            .transitGatewayPrefixListReference(this.deletedSdkModel())
            .build();
    }

    public CreateTransitGatewayPrefixListReferenceResponse createResponse() {
        return CreateTransitGatewayPrefixListReferenceResponse.builder()
            .transitGatewayPrefixListReference(this.sdkModel())
            .build();
    }



}
