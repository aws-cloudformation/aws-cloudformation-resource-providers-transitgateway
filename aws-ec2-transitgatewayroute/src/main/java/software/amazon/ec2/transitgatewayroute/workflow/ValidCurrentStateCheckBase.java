package software.amazon.ec2.transitgatewayroute.workflow;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteState;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.workflow.read.Read;

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
        Boolean invalidStatesExist = this.invalidStates().isEmpty();
        Boolean validStatesExist = this.validStates().isEmpty();
        List<String> validStates = this.validStates();
        List<String> invalidStates = this.invalidStates();
        if((this.invalidStates().isEmpty() && this.validStates().contains(this.currentState())) || (this.validStates().isEmpty() && !this.invalidStates().contains(this.currentState()))) {
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
        CfnResourceConflictException exception = new CfnResourceConflictException(ResourceModel.TYPE_NAME, this.model.getTransitGatewayRouteTableId() + "::" + this.model.getDestinationCidrBlock(), "STATE: \"" + this.currentState() + "\" cannot be modified by ACTION: \"" + this.action().toUpperCase() + "\"");
        if(this.currentState().equals(TransitGatewayRouteState.DELETED.toString())) {
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
            ResourceModel currentModel = this.makeRequest();
            if(currentModel == null) {
                return this._currentState = TransitGatewayRouteState.DELETED.toString();
            } else {
                return this._currentState = this.makeRequest().getState();
            }
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

}
