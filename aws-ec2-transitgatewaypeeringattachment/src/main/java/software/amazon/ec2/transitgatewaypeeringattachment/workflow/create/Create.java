package software.amazon.ec2.transitgatewaypeeringattachment.workflow.create;

import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.TagUtils;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayPeeringAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayPeeringAttachmentResponse;
import software.amazon.cloudformation.proxy.*;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

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
        this.logger.log(progress.getResourceModel().toString());
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTransitGatewayPeeringAttachmentRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayPeeringAttachmentRequest.builder()
            .transitGatewayId(model.getTransitGatewayId())
            .peerTransitGatewayId(model.getPeerTransitGatewayId())
            .peerAccountId(model.getPeerAccountId())
            .peerRegion(model.getPeerRegion())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .build();
    }


    private CreateTransitGatewayPeeringAttachmentResponse makeServiceCall(CreateTransitGatewayPeeringAttachmentRequest request, ProxyClient<Ec2Client> client) {
        logger.log(request.toString());
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayPeeringAttachment);
    }

    private boolean stabilize(
        CreateTransitGatewayPeeringAttachmentRequest awsRequest,
        CreateTransitGatewayPeeringAttachmentResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayAttachmentId(awsResponse.transitGatewayPeeringAttachment().transitGatewayAttachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
        return TransitGatewayAttachmentState.PENDING_ACCEPTANCE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayPeeringAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        this.logger.log("handleError");
        System.out.println(exception);
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
