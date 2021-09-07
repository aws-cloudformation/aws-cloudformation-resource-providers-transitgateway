package software.amazon.ec2.transitgatewaypeeringattachment.workflow.create;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.PeeringAttachmentStatus;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.TagUtils;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;
    ResourceModel stableResponse;

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
            .done(this::done);
    }

    private CreateTransitGatewayPeeringAttachmentRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayPeeringAttachmentRequest.builder()
            .peerRegion(model.getPeerRegion())
            .peerAccountId(model.getPeerAccountId())
            .peerTransitGatewayId(model.getPeerTransitGatewayId())
            .transitGatewayId(model.getTransitGatewayId())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .build();
    }

    private CreateTransitGatewayPeeringAttachmentResponse makeServiceCall(CreateTransitGatewayPeeringAttachmentRequest request, ProxyClient<Ec2Client> client) {
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
        ResourceModel currentResourceModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        if(currentResourceModel == null) return false;
        boolean isStable = (TransitGatewayAttachmentState.PENDING_ACCEPTANCE.toString().equals(currentResourceModel.getState()) || TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentResourceModel.getState()));
        if (isStable) {
            this.stableResponse = currentResourceModel;
        }
        return isStable;
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayPeeringAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(CreateTransitGatewayPeeringAttachmentResponse response) {
        ResourceModel model = this.stableResponse;
        if(model == null || model.getState().equals(TransitGatewayAttachmentState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(model);
        }
    }
}
