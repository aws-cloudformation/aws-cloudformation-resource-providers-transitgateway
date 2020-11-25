package com.aws.ec2.transitgatewayattachment.workflow.create;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ClientBuilder;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayattachment.workflow.TagUtils;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Create(
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

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTransitGatewayVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayVpcAttachmentRequest.builder()
            .subnetIds(model.getSubnetIds())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .transitGatewayId(model.getTransitGatewayId())
            .vpcId(model.getVpcId())
            .build();
    }

    private CreateTransitGatewayVpcAttachmentResponse makeServiceCall(CreateTransitGatewayVpcAttachmentRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayVpcAttachment);
    }

    private boolean stabilize(
        CreateTransitGatewayVpcAttachmentRequest awsRequest,
        CreateTransitGatewayVpcAttachmentResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        if(!callbackContext.getStarted()){
            if(model.getId()!=null) throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property.");
            model.setId(awsResponse.transitGatewayVpcAttachment().transitGatewayAttachmentId());
            callbackContext.setStarted(true);
        }
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayVpcAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
