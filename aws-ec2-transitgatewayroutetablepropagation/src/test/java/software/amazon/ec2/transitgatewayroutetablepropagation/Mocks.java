package software.amazon.ec2.transitgatewayroutetablepropagation;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mocks {
    public Integer counter;
    public String TransitGatewayRouteTableId;
    public String TransitGatewayAttachmentId;
    public Map<String, String> defaults;


    public Mocks(
    ) {
        this.TransitGatewayAttachmentId = "tgw-attach-1231bc";
        this.TransitGatewayRouteTableId = "tgw-rtb-123abc";
        this.defaults = Stream.of(new String[][] {
                { "resourceType", "vpc" },
                { "resourceId", "resource-123-abc" },
                { "enabledState", "enabled" },
                { "disabledState", "disabled" }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        this.counter = 0;
    }

    public ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
    }

    public ResourceModel modelWithoutCreateOnlyProperties() {
        return ResourceModel.builder()
                .transitGatewayRouteTableId(this.TransitGatewayRouteTableId)
                .transitGatewayAttachmentId(null)
                .state(this.defaults.get("enabledState"))
                .resourceId(this.defaults.get("resourceId"))
                .resourceType(this.defaults.get("resourceType"))
                .build();
    }

    public ResourceModel model(String state) {
        return getResourceModel(state);
    }

    public ResourceModel model() {
        return this.model(this.defaults.get("enabledState"));
    }

    public TransitGatewayRouteTablePropagation sdkModel(String state) {
        return TransitGatewayRouteTablePropagation.builder()
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .state(state)
                .resourceId(this.defaults.get("resourceId"))
                .resourceType(this.defaults.get("resourceType"))
                .build();
    }

    public TransitGatewayRouteTablePropagation sdkModel() {
        return this.sdkModel(this.defaults.get("enabledState"));
    }

    public GetTransitGatewayRouteTablePropagationsResponse describeResponse(String state) {
        return GetTransitGatewayRouteTablePropagationsResponse.builder()
                .transitGatewayRouteTablePropagations(
                        this.sdkModel(state)
                )
                .build();
    }

    public GetTransitGatewayRouteTablePropagationsResponse describeResponse() {
        return this.describeResponse(this.defaults.get("enabledState"));
    }

    public DisableTransitGatewayRouteTablePropagationResponse deleteResponse() {
        return DisableTransitGatewayRouteTablePropagationResponse.builder()
                .propagation(this.getTransitGatewayPropagation())
                .build();
    }

    public EnableTransitGatewayRouteTablePropagationResponse createResponse() {
        return EnableTransitGatewayRouteTablePropagationResponse.builder()
                .propagation(this.getTransitGatewayPropagation())
                .build();
    }

    public GetTransitGatewayRouteTablePropagationsResponse emptyReadResponse() {
        java.util.List<TransitGatewayRouteTablePropagation> emptyRouteTablePropagations = new ArrayList<>();
        return GetTransitGatewayRouteTablePropagationsResponse.builder()
                .transitGatewayRouteTablePropagations(emptyRouteTablePropagations)
                .build();
    }

    private TransitGatewayPropagation getTransitGatewayPropagation() {
        return TransitGatewayPropagation.builder()
                .transitGatewayRouteTableId(this.TransitGatewayRouteTableId)
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .state(this.defaults.get("enabledState"))
                .resourceId(this.defaults.get("resourceId"))
                .resourceType(this.defaults.get("resourceType"))
                .build();
    }

    private ResourceModel getResourceModel(String state) {
        return ResourceModel.builder()
                .transitGatewayRouteTableId(this.TransitGatewayRouteTableId)
                .transitGatewayAttachmentId(this.TransitGatewayAttachmentId)
                .state(state)
                .resourceId(this.defaults.get("resourceId"))
                .resourceType(this.defaults.get("resourceType"))
                .build();
    }
}
