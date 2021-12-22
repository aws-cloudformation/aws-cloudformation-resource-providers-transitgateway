package com.aws.ec2.transitgatewayvpcattachment;

import com.aws.ec2.transitgatewayvpcattachment.ResourceModel;
import com.aws.ec2.transitgatewayvpcattachment.CallbackContext;
import com.aws.ec2.transitgatewayvpcattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayvpcattachment.workflow.read.Read;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;


public class ValidateCurrentStateBase  {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    protected com.aws.ec2.transitgatewayvpcattachment.CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    protected ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> progress;
    ResourceModel model;
    String _currentState;

    public ValidateCurrentStateBase(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            com.aws.ec2.transitgatewayvpcattachment.CallbackContext callbackContext,
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

    public ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> run(ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> progress) {
        this.progress = progress;

        try {
            return this.validate();
        } catch (Exception exception) {
            return this.handleError(exception);
        }
    }

    protected TransitGatewayAttachmentState makeRequest() {
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
        return new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(this.model);
    }

    protected ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> validate() {
        if(this.validStates().contains(this.currentState())) {
            return this.progress;
        } else {
            return this.failure();
        }
    }

    protected ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> failure() {
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString(), "State: " + this.currentState() + " cannot be updated.");
        if(this.currentState().equals("deleted")) {
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.NotFound);
        }
        return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.ResourceConflict);
    }

    protected ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> failureProgress() {
        CfnResourceConflictException exception =  new CfnResourceConflictException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString(), "State: " + this.currentState() + " cannot be updated.");
        if(this.currentState().equals("deleted")) {
            return ProgressEvent.defaultFailureHandler(exception, HandlerErrorCode.NotFound);
        }
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.FAILED)
                .errorCode(HandlerErrorCode.AlreadyExists)
                .message(exception.getMessage())
                .build();
    }

    protected List<String> validStates() {
        return new ArrayList<>();
    }


    protected String currentState() {
        if(this._currentState != null) {
            return this._currentState;
        } else {
            return this._currentState = this.makeRequest().toString();
        }
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(Exception exception) {

        if (exception instanceof AwsServiceException) {
            AwsServiceException awsServiceException = (AwsServiceException) exception;
            String errorCode = awsServiceException.awsErrorDetails().errorCode();
        }
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));

    }

}
