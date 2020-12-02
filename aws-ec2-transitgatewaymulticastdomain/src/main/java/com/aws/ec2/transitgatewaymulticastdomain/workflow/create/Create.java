package com.aws.ec2.transitgatewaymulticastdomain.workflow.create;

import com.aws.ec2.transitgatewaymulticastdomain.CallbackContext;
import com.aws.ec2.transitgatewaymulticastdomain.ResourceModel;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.TagUtils;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomainState;
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

    private CreateTransitGatewayMulticastDomainRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayMulticastDomainRequest.builder()
            .transitGatewayId(model.getTransitGatewayId())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .build();
    }

    private CreateTransitGatewayMulticastDomainResponse makeServiceCall(CreateTransitGatewayMulticastDomainRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayMulticastDomain);
    }

    private boolean stabilize(
        CreateTransitGatewayMulticastDomainRequest awsRequest,
        CreateTransitGatewayMulticastDomainResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayMulticastDomainId(awsResponse.transitGatewayMulticastDomain().transitGatewayMulticastDomainId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayMulticastDomainState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayMulticastDomainRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        this.logger.log("handleError");
        System.out.println(exception);
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
