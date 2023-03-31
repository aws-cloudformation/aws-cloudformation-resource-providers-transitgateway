package software.amazon.ec2.transitgatewayroutetable;



import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTable;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteTableRequest;
import software.amazon.awssdk.services.ec2.model.TagSpecification;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteTableRequest;
import software.amazon.awssdk.services.ec2.model.ResourceType;
import software.amazon.awssdk.utils.CollectionUtils;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTableState;

public class Translator{

    static DescribeTransitGatewayRouteTablesRequest translateToReadRequest(final String transitGatewayRouteTableId){
        return DescribeTransitGatewayRouteTablesRequest.builder().transitGatewayRouteTableIds(transitGatewayRouteTableId)
                .filters(Filter.builder().name("state").values(TransitGatewayRouteTableState.AVAILABLE.toString()).build())
                .build();
    }



    static ResourceModel translateFromReadResponse(DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse) {
        if(describeTransitGatewayRouteTablesResponse.hasTransitGatewayRouteTables()) {
            TransitGatewayRouteTable transitGatewayRouteTable = describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().get(0);
            return ResourceModel.builder()
                    .transitGatewayId(transitGatewayRouteTable.transitGatewayId())
                    .tags(convertSDKTagsToModelTags(transitGatewayRouteTable.tags()))
                    .transitGatewayRouteTableId(transitGatewayRouteTable.transitGatewayRouteTableId())
                    .build();
        }
        return null;
    }

    /**
     * Convert SDK Tags Map to Model Tags List. Returns a List of Tags.
     * @param sdkTags - a List of Model Tags
     * @return List of EC2 Tags
     */
    static List<Tag> convertSDKTagsToModelTags(final List<software.amazon.awssdk.services.ec2.model.Tag> sdkTags) {
        List<Tag> modelTags = new ArrayList<>();
        for(software.amazon.awssdk.services.ec2.model.Tag t : sdkTags){
            Tag modelTag = new Tag(t.key(), t.value());
            modelTags.add(modelTag);
        }
        return modelTags;
    }


    /**
     * Request to create a resource
     * @param model resource model
     * @return awsRequest the aws service request to create a resource
     */
    static CreateTransitGatewayRouteTableRequest translateToCreateRequest(Map<String, String> tags, final ResourceModel model) {
        if(CollectionUtils.isNullOrEmpty(tags)) {
            return CreateTransitGatewayRouteTableRequest.builder()
                    .transitGatewayId(model.getTransitGatewayId())
                    .build();
        }
        return CreateTransitGatewayRouteTableRequest.builder()
                .transitGatewayId(model.getTransitGatewayId())
                .tagSpecifications(translateToTagSpecifications(tags))
                .build();
    }

    /**
     * Creates TagSpecification from List of tags
     * @param tags map of model Tags
     * @return TagSpecification object
     */
    private static TagSpecification translateToTagSpecifications(Map<String, String> tags) {
        List<software.amazon.awssdk.services.ec2.model.Tag> sdkTagList = streamOfOrEmpty(tags.entrySet())
                .map(e -> software.amazon.awssdk.services.ec2.model.Tag.builder().key(e.getKey()).value(e.getValue()).build())
                .collect(Collectors.toList());

        return TagSpecification.builder().resourceType(ResourceType.TRANSIT_GATEWAY_ROUTE_TABLE).tags(sdkTagList).build();
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    /**
     * Request to delete a resource
     * @param model resource model
     * @return awsRequest the aws service request to delete a resource
     */
    static DeleteTransitGatewayRouteTableRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteTransitGatewayRouteTableRequest.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId().toLowerCase()).build();

    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     * @param describeTransitGatewayRouteTablesResponse the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse) {

        return streamOfOrEmpty(describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables())
                .map(item -> ResourceModel.builder()
                        .transitGatewayRouteTableId(item.transitGatewayRouteTableId())
                        .transitGatewayId(item.transitGatewayId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Request to List resources
     * @param model the resource model
     * @return DescribeVpnGateway response
     */
    static DescribeTransitGatewayRouteTablesRequest translateToListRequest(final ResourceModel model) {
        return DescribeTransitGatewayRouteTablesRequest.builder()
                .filters(Filter.builder().name("state").values(TransitGatewayRouteTableState.AVAILABLE.toString()).build())
                .build();
    }

}
