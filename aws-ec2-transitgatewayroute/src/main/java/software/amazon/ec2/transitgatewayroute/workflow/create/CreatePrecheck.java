package software.amazon.ec2.transitgatewayroute.workflow.create;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroute.workflow.read.Read;

public class CreatePrecheck {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;
    ResourceModel model;

    public CreatePrecheck(
        AmazonWebServicesClientProxy proxy,
        ResourceHandlerRequest<ResourceModel> request,
        CallbackContext callbackContext,
        ProxyClient<Ec2Client> client,
        Logger logger
    ) {
        this.model = request.getDesiredResourceState();
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        if(this.callbackContext.getAttempts() > 0) { return progress; } //skip if this not the first attempt by the lambda function

        try {
            this.progress = progress;
            return this.validate();
        } catch (Exception exception) {
            return this.handleError(exception);
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        if((this.model.getBlackhole().equals(false) && this.model.getTransitGatewayAttachmentId() == null) || (this.model.getBlackhole().equals(true) && this.model.getTransitGatewayAttachmentId() != null)) {
            return this.invalidModel();
        } else {
            ResourceModel current = this.makeRequest();

            if(current != null) {
                return this.failedRequest();
            } else {
                return this.progress;
            }
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext> failedRequest() {
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, this.model.getTransitGatewayRouteTableId() + "::" + this.model.getDestinationCidrBlock(), "Cannot be modified by ACTION: CREATE. A resource with the primary identifier already exists");
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.AlreadyExists);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> invalidModel() {
        String errorMessage = ResourceModel.TYPE_NAME + "Cannot be modified by ACTION: CREATE. Blackhole must be true or you must provide a TransitGatewayAttachmentId, but Blackhole is set to: " + this.model.getBlackhole() + " and TransitGatewayAttachmentId is set to: " + this.model.getTransitGatewayRouteTableId()  ;
        AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRequest").errorMessage(errorMessage).build()).build();
        return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.InvalidRequest);
    }

    private ResourceModel makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
