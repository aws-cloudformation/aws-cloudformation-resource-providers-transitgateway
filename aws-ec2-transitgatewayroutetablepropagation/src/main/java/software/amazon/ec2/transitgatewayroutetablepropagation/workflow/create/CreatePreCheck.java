package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.create;


import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.read.Read;

public class CreatePreCheck {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;
    ResourceModel model;

    public CreatePreCheck(
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

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        if(this.callbackContext.getAttempts() > 0) { return progress; } //skip if this not the first attempt by the lambda function

        try {
            this.progress = progress;
            return this.validate();
        } catch (Exception exception) {
            return this.handleError(exception);
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        ResourceModel current = this.makeRequest();
        if(current != null) {
            return this.failedRequest();
        } else {
            return this.progress;
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext> failedRequest() {
        CfnResourceConflictException exception = new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString()
                .replace("/properties/", ""), "Cannot be modified by ACTION: CREATE. A resource with the primary identifier already exists");
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.FAILED)
                .errorCode(HandlerErrorCode.AlreadyExists)
                .message(exception.getMessage())
                .build();
    }

    private ResourceModel makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
