package software.amazon.ec2.transitgatewayroute;

import org.apache.commons.lang3.SerializationUtils;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteAttachment;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mocks {
    public Instant currentTime;
    public Integer counter;

    public Map<String, String> defaults;

    public Mocks() {
        this.currentTime = Instant.now();
        this.counter = 0;

        this.defaults = Stream.of(new String[][] {
            { "transitGatewayRouteTableId", "tgw-rtb-0ce6c384EXAMPLE" },
            { "transitGatewayAttachmentId", "tgw-att-02bb79002EXAMPLE" },
            { "prefixListId", "prefixListId-02bb79002EXAMPLE" },
            { "blackhole", "false" },
            { "state", "active" },
            { "type", "static" },
            { "destinationCidrBlock", "172.0.0.0/24" },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public Map<String, String> modelMap() {
        return this.modelMap(Collections.emptyMap());
    }

    public Map<String, String> modelMap(Map<String, String> newMap) {
        HashMap<String, String> defaultsHashMap = new HashMap<>(this.defaults);
        Map<String, String> defaultsCopy = SerializationUtils.clone(defaultsHashMap);
        defaultsCopy.putAll(newMap);
        return defaultsCopy;
    }
    public  ResourceHandlerRequest<ResourceModel> request(ResourceModel model) {
        return ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
    }

    public  ResourceModel model() {
        return this.model(Collections.emptyMap());
    }

    public  ResourceModel model(Map<String, String> newMap) {
        Map<String, String> values = this.modelMap(newMap);
        return ResourceModel.builder()
            .transitGatewayRouteTableId(values.get("transitGatewayRouteTableId"))
            .transitGatewayAttachmentId(values.get("transitGatewayAttachmentId"))
            .destinationCidrBlock(values.get("destinationCidrBlock"))
            .blackhole(values.get("blackhole").equals("true"))
            .type(values.get("type"))
            .prefixListId(values.get("prefixListId"))
            .state(values.get("blackhole").equals("true") ? "blackhole" : values.get("state"))
            .build();
    }



    public TransitGatewayRoute sdkModel() {
        return this.sdkModel(Collections.emptyMap());
    }

    public TransitGatewayRoute sdkModel(Map<String, String> newMap) {
        Map<String, String> values = this.modelMap(newMap);
        if(values.get("transitGatewayAttachmentId") == null) {
            return TransitGatewayRoute.builder()
                .destinationCidrBlock(values.get("destinationCidrBlock"))
                .prefixListId(values.get("prefixListId"))
                .state(values.get("blackhole").equals("true") ? "blackhole" : values.get("state"))
                .type(values.get("type"))
                .build();
        } else {
            TransitGatewayRouteAttachment attachment = TransitGatewayRouteAttachment.builder()
                .transitGatewayAttachmentId(values.get("transitGatewayAttachmentId"))
                .resourceType("vpc")
                .resourceId("vpc-asfefa4")
                .build();
            Collection<TransitGatewayRouteAttachment> attachments = new ArrayList<>();
            attachments.add(attachment);
            return TransitGatewayRoute.builder()
                .transitGatewayAttachments(attachment)
                .destinationCidrBlock(values.get("destinationCidrBlock"))
                .prefixListId(values.get("prefixListId"))
                .state(values.get("blackhole").equals("true") ? "blackhole" : values.get("state"))
                .type(values.get("type"))
                .build();
        }
    }


    public SearchTransitGatewayRoutesResponse readResponse() {
        return this.readResponse(Collections.emptyMap());

    }

    public SearchTransitGatewayRoutesResponse readResponse(Map<String, String> newMap) {
        return SearchTransitGatewayRoutesResponse.builder()
            .routes(
                this.sdkModel(newMap)
            )
            .build();
    }

    public SearchTransitGatewayRoutesResponse emptyReadResponse() {
        java.util.List<TransitGatewayRoute> emptyRoutes = new ArrayList<>();
        return SearchTransitGatewayRoutesResponse.builder()
            .routes(emptyRoutes)
            .build();
    }

    public SearchTransitGatewayRoutesResponse listResponse() {
        return this.listResponse(Collections.emptyMap());

    }

    public SearchTransitGatewayRoutesResponse listResponse(Map<String, String> newMap) {
        return SearchTransitGatewayRoutesResponse.builder()
            .routes(
                this.sdkModel(newMap)
            )
            .build();
    }


    public DeleteTransitGatewayRouteResponse deleteResponse() {
        return this.deleteResponse(Collections.emptyMap());
    }

    public DeleteTransitGatewayRouteResponse deleteResponse(Map<String, String> newMap) {
        Map<String, String> values = this.modelMap(newMap);
        return DeleteTransitGatewayRouteResponse.builder().route(
            TransitGatewayRoute.builder()
                .destinationCidrBlock(values.get("destinationCidrBlock"))
                .state(values.get("state"))
                .build()
        ).build();
    }


    public CreateTransitGatewayRouteResponse createResponse() {
        return this.createResponse(Collections.emptyMap());

    }
    public CreateTransitGatewayRouteResponse createResponse(Map<String, String> newMap) {
        Map<String, String> values = this.modelMap(newMap);

        return CreateTransitGatewayRouteResponse.builder()
            .route(
                TransitGatewayRoute.builder()
                .destinationCidrBlock(values.get("destinationCidrBlock"))
                .state(values.get("state"))
                .build()
            )
            .build();
    }



}
