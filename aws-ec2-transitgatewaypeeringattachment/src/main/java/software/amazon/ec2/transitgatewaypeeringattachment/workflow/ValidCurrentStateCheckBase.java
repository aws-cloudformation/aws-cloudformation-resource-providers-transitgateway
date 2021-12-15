package software.amazon.ec2.transitgatewaypeeringattachment.workflow;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.Read;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheckBase {
    protected AmazonWebServicesClientProxy proxy;
    protected ResourceHandlerRequest<ResourceModel> request;
    protected CallbackContext callbackContext;
    protected ProxyClient<Ec2Client> client;
    protected Logger logger;
    protected ProgressEvent<ResourceModel, CallbackContext>  progress;
    protected ResourceModel model;
    String _currentState;

    public ValidCurrentStateCheckBase(
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

    protected ResourceModel makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        String state = this.currentState();
        if((this.invalidStates().isEmpty() && this.validStates().contains(state)) || (this.validStates().isEmpty() && !this.invalidStates().contains(state))) {
            return this.progress;
        } else {
            return this.failure();
        }
    }

    protected String action() {
        String packageName =  this.getClass().getPackage().getName();
        String[] packageParts = packageName.split("\\.");
        return packageParts[packageParts.length - 1];
    }

    protected ProgressEvent<ResourceModel, CallbackContext> failure() {
        String state = this.currentState();
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString().replace("/properties/", ""), "STATE: \"" + state + "\" cannot be modified by ACTION: \"" + this.action().toUpperCase() + "\"");
        if(state == null || state.equals("deleted")) {
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.ResourceConflict);
        }
    }

    protected List<String> validStates() {
        return new ArrayList<>();
    }

    protected List<String> invalidStates() {return new ArrayList<>(); }

    protected String currentState() {
        if(this._currentState != null) {
            return this._currentState;
        } else {

            ResourceModel model;
            try{
                model = this.makeRequest();
            } catch (Exception exception){
                if (!ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)){
                    // NotFound is equivalent to deleted. Any other error we don't know how to handle properly.
                    throw exception;
                } else {
                    model = null;
                }
            }
            // Nothing returned implies that the model was deleted.
            // This leaves us with a (valid) null state. We can use
            // the null state to handle deleted/failed modes.
            return model == null ? null : model.getState();
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
