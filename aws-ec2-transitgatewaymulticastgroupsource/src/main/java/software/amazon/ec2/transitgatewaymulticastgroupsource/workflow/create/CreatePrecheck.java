package software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.create;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupsource.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroupsource.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.read.Read;

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
        System.out.println(this.model.getTransitGatewayMulticastDomainId().startsWith("tgw-mcast-domain-"));
        System.out.println( this.model.getGroupIpAddress().split("[.]").length);
        if(!this.model.getTransitGatewayMulticastDomainId().startsWith("tgw-mcast-domain-") || this.model.getGroupIpAddress().split("[.]").length != 4 || !this.model.getNetworkInterfaceId().startsWith("eni-")) {
            this.logger.log("INVALID STUFF");
            return this.invalidModel();
        } else {
            ResourceModel current = this.makeRequest();

            if(current != null) {
                this.logger.log("FAILED");
                this.logger.log(current.toString());
                return this.failedRequest();
            } else {
                this.logger.log("CONTINUE");
                return this.progress;
            }
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext> failedRequest() {
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString().replace("/properties/", ""), "Cannot be modified by ACTION: CREATE. A resource with the primary identifier already exists");
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.AlreadyExists);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> invalidModel() {
        String errorMessage = ResourceModel.TYPE_NAME + this.model.getPrimaryIdentifier().toString().replace("/properties/", "") + "Cannot be modified by ACTION: CREATE. At least one primary identifier is invalid";
        AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRequest").errorMessage(errorMessage).build()).build();
        return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.InvalidRequest);
       /* CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString().replace("/properties/", ""), "Cannot be modified by ACTION: CREATE. All primary identifier fields are required");
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.InvalidRequest);*/
    }

    private ResourceModel makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        System.out.println(exception.toString());
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
