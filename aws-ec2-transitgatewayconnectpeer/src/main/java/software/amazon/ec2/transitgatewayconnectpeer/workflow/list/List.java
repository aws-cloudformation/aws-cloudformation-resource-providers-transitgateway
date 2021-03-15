package software.amazon.ec2.transitgatewayconnectpeer.workflow.list;

import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayConnectPeersRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayConnectPeersResponse;
import software.amazon.ec2.transitgatewayconnectpeer.CallbackContext;
import software.amazon.ec2.transitgatewayconnectpeer.ResourceModel;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.ExceptionMapper;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayConnectsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayConnectsResponse;
import software.amazon.cloudformation.proxy.*;

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
        DescribeTransitGatewayConnectPeersRequest awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            DescribeTransitGatewayConnectPeersResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
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

    private DescribeTransitGatewayConnectPeersRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayConnectPeersRequest.builder()
            .maxResults(50)
            .nextToken(this.request.getNextToken()).build();
    }

    private DescribeTransitGatewayConnectPeersResponse makeServiceCall(DescribeTransitGatewayConnectPeersRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayConnectPeers);
    }

    private java.util.List<ResourceModel> translateResponseToModel(DescribeTransitGatewayConnectPeersResponse awsResponse) {
        return streamOfOrEmpty(awsResponse. transitGatewayConnectPeers())
            .filter(resource -> !(resource.stateAsString().equals("deleted")))
            .map(resource -> ResourceModel.builder()
                .transitGatewayConnectPeerId(resource.transitGatewayConnectPeerId())
                .build())
            .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayConnectPeersRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
