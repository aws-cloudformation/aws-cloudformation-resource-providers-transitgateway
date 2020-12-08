package software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.list;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayMulticastGroupsRequest;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayMulticastGroupsResponse;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupsource.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroupsource.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.ExceptionMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public List(
        AmazonWebServicesClientProxy proxy,
        ResourceHandlerRequest<ResourceModel> request,
        CallbackContext callbackContext,
        ProxyClient<Ec2Client> client,
        Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        this.progress = progress;
        SearchTransitGatewayMulticastGroupsRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            SearchTransitGatewayMulticastGroupsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(awsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }
    }

    private SearchTransitGatewayMulticastGroupsRequest translateModelToRequest(ResourceModel model) {
        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("is-group-source").values("true").build());
        return SearchTransitGatewayMulticastGroupsRequest.builder()
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .filters(filters)
            .maxResults(50)
            .nextToken(this.request.getNextToken()).build();
    }

    private SearchTransitGatewayMulticastGroupsResponse makeServiceCall(SearchTransitGatewayMulticastGroupsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::searchTransitGatewayMulticastGroups);
    }

    private java.util.List<ResourceModel> translateResponseToModel(SearchTransitGatewayMulticastGroupsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.multicastGroups())
            .map(resource -> ResourceModel.builder()
                .transitGatewayMulticastDomainId(this.progress.getResourceModel().getTransitGatewayMulticastDomainId())
                .networkInterfaceId(resource.networkInterfaceId())
                .groupIpAddress(resource.groupIpAddress())
                .groupMember(resource.groupMember())
                .groupSource(resource.groupSource())
                .build())
            .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(SearchTransitGatewayMulticastGroupsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
