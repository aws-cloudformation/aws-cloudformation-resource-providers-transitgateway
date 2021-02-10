package software.amazon.ec2.transitgatewayconnect.workflow.delete;

import software.amazon.ec2.transitgatewayconnect.CallbackContext;
import software.amazon.ec2.transitgatewayconnect.ResourceModel;
import software.amazon.ec2.transitgatewayconnect.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnect.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayConnectRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayConnectResponse;
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

    private DeleteTransitGatewayConnectRequest translateModelToRequest(ResourceModel model) {
        return  DeleteTransitGatewayConnectRequest.builder()
                   .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .build();
    }

    private DeleteTransitGatewayConnectResponse makeServiceCall(DeleteTransitGatewayConnectRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTransitGatewayConnect);
    }

    private boolean stabilize (
        DeleteTransitGatewayConnectRequest request,
        DeleteTransitGatewayConnectResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return currentState.equals(TransitGatewayAttachmentState.DELETED.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteTransitGatewayConnectRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
