package com.aws.ec2.transitgatewayattachment.workflow.delete;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ClientBuilder;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
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
        Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = this.proxy.newProxy(ClientBuilder::getClient);
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

    private DeleteTransitGatewayVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        return  DeleteTransitGatewayVpcAttachmentRequest.builder()
            .transitGatewayAttachmentId(model.getId())
            .build();
    }

    private DeleteTransitGatewayVpcAttachmentResponse makeServiceCall(DeleteTransitGatewayVpcAttachmentRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTransitGatewayVpcAttachment);
    }

    private boolean stabilize (
        DeleteTransitGatewayVpcAttachmentRequest request,
        DeleteTransitGatewayVpcAttachmentResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setId(response.transitGatewayVpcAttachment().transitGatewayAttachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return currentState.equals(TransitGatewayAttachmentState.DELETED.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DeleteTransitGatewayVpcAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (exception instanceof ResourceNotFoundException || ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
