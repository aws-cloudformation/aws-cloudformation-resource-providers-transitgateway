package software.amazon.ec2.transitgatewayroute.workflow.create;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteState;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroute.workflow.read.Read;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Create(
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

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTransitGatewayRouteRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayRouteRequest.builder()
            .destinationCidrBlock(model.getDestinationCidrBlock())
            .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .blackhole(model.getBlackhole())
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .build();
    }

    private CreateTransitGatewayRouteResponse makeServiceCall(CreateTransitGatewayRouteRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayRoute);
    }

    private boolean stabilize(
        CreateTransitGatewayRouteRequest awsRequest,
        CreateTransitGatewayRouteResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        ResourceModel current = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        return current != null && (current.getState().equals(TransitGatewayRouteState.ACTIVE.toString()) || current.getState().equals(TransitGatewayRouteState.BLACKHOLE.toString()));
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayRouteRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
