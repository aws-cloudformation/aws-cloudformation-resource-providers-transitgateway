package software.amazon.ec2.transitgatewayconnectpeer.workflow.delete;

import software.amazon.ec2.transitgatewayconnectpeer.CallbackContext;
import software.amazon.ec2.transitgatewayconnectpeer.ResourceModel;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayConnectPeerRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayConnectPeerResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
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

    private DeleteTransitGatewayConnectPeerRequest translateModelToRequest(ResourceModel model) {
        return  DeleteTransitGatewayConnectPeerRequest.builder()
                   .transitGatewayConnectPeerId(model.getTransitGatewayConnectPeerId())
            .build();
    }

    private DeleteTransitGatewayConnectPeerResponse makeServiceCall(DeleteTransitGatewayConnectPeerRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTransitGatewayConnectPeer);
    }

    private boolean stabilize (
        DeleteTransitGatewayConnectPeerRequest request,
        DeleteTransitGatewayConnectPeerResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return currentState.equals(TransitGatewayAttachmentState.DELETED.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteTransitGatewayConnectPeerRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
