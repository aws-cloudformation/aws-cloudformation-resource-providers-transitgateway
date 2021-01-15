package software.amazon.ec2.transitgatewayroutetable.workflow.delete;

import software.amazon.ec2.transitgatewayroutetable.CallbackContext;
import software.amazon.ec2.transitgatewayroutetable.ResourceModel;
import software.amazon.ec2.transitgatewayroutetable.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroutetable.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteTableRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteTableResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTableState;
import software.amazon.cloudformation.proxy.*;

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

    private DeleteTransitGatewayRouteTableRequest translateModelToRequest(ResourceModel model) {
        return  DeleteTransitGatewayRouteTableRequest.builder()
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .build();
    }

    private DeleteTransitGatewayRouteTableResponse makeServiceCall(DeleteTransitGatewayRouteTableRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTransitGatewayRouteTable);
    }

    private boolean stabilize (
        DeleteTransitGatewayRouteTableRequest request,
        DeleteTransitGatewayRouteTableResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayRouteTableId(response.transitGatewayRouteTable().transitGatewayRouteTableId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return currentState.equals(TransitGatewayRouteTableState.DELETED.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteTransitGatewayRouteTableRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
