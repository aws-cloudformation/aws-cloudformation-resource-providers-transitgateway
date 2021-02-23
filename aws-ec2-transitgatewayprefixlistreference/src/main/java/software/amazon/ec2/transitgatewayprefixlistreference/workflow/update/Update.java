package software.amazon.ec2.transitgatewayprefixlistreference.workflow.update;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayprefixlistreference.CallbackContext;
import software.amazon.ec2.transitgatewayprefixlistreference.ResourceModel;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.read.Read;

public class Update {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

    public Update(
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

    private ModifyTransitGatewayPrefixListReferenceRequest translateModelToRequest(ResourceModel model) {
        return  ModifyTransitGatewayPrefixListReferenceRequest.builder()
                .prefixListId(model.getPrefixListId())
                .blackhole(model.getBlackhole())
                .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .build();
    }

    private ModifyTransitGatewayPrefixListReferenceResponse makeServiceCall(ModifyTransitGatewayPrefixListReferenceRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::modifyTransitGatewayPrefixListReference);
    }

    private boolean stabilize (
            ModifyTransitGatewayPrefixListReferenceRequest request,
            ModifyTransitGatewayPrefixListReferenceResponse response,
            ProxyClient<Ec2Client> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return currentState.equals(TransitGatewayPrefixListReferenceState.DELETING.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(ModifyTransitGatewayPrefixListReferenceRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
