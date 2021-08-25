package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.list;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.ExceptionMapper;

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
    ProgressEvent<ResourceModel, CallbackContext> progress;

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
        GetTransitGatewayRouteTablePropagationsRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());
        try{
            GetTransitGatewayRouteTablePropagationsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
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

    private GetTransitGatewayRouteTablePropagationsRequest translateModelToRequest(ResourceModel model) {
        return GetTransitGatewayRouteTablePropagationsRequest.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .maxResults(50)
                .nextToken(this.request.getNextToken()).build();
    }

    private GetTransitGatewayRouteTablePropagationsResponse makeServiceCall(GetTransitGatewayRouteTablePropagationsRequest awsRequest,
                                                                            ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayRouteTablePropagations);
    }

    private java.util.List<ResourceModel> translateResponseToModel(GetTransitGatewayRouteTablePropagationsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.transitGatewayRouteTablePropagations())
                .map(resource -> ResourceModel.builder()
                        .transitGatewayAttachmentId(resource.transitGatewayAttachmentId())
                        .transitGatewayRouteTableId(this.progress.getResourceModel().getTransitGatewayRouteTableId())
                        .resourceId(resource.resourceId())
                        .resourceType(resource.resourceType().toString())
                        .state(resource.state().toString())
                        .build())
                .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(GetTransitGatewayRouteTablePropagationsRequest awsRequest,
                                                                       Exception exception, ProxyClient<Ec2Client> client,
                                                                       ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
