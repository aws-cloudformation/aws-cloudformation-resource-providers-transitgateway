package software.amazon.ec2.transitgatewayroute.workflow.delete;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteResponse;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroute.workflow.read.Read;

public class Delete {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Delete(
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
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();

    }

    private DeleteTransitGatewayRouteRequest translateModelToRequest(ResourceModel model) {
        return  DeleteTransitGatewayRouteRequest.builder()
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .destinationCidrBlock(model.getDestinationCidrBlock())
            .build();
    }

    private DeleteTransitGatewayRouteResponse makeServiceCall(DeleteTransitGatewayRouteRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTransitGatewayRoute);
    }

    private boolean stabilize (
        DeleteTransitGatewayRouteRequest request,
        DeleteTransitGatewayRouteResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        ResourceModel current = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        return current == null;
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteTransitGatewayRouteRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
