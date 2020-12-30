package software.amazon.ec2.transitgatewayroute.workflow.list;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayRoutesRequest;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayRoutesResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteState;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.workflow.ExceptionMapper;

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
        SearchTransitGatewayRoutesRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            SearchTransitGatewayRoutesResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse, progress.getResourceModel());
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .status(OperationStatus.SUCCESS)
            .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }
    }

    private SearchTransitGatewayRoutesRequest translateModelToRequest(ResourceModel model) {
        return SearchTransitGatewayRoutesRequest.builder()
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .maxResults(50)
            .build();
    }

    private SearchTransitGatewayRoutesResponse makeServiceCall(SearchTransitGatewayRoutesRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::searchTransitGatewayRoutes);
    }

    private java.util.List<ResourceModel> translateResponseToModel(SearchTransitGatewayRoutesResponse awsResponse, ResourceModel model) {
        return streamOfOrEmpty(awsResponse.routes())
            .map(resource -> ResourceModel.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .destinationCidrBlock(resource.destinationCidrBlock())
            .build())
            .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(SearchTransitGatewayRoutesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
