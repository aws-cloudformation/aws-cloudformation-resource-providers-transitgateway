package com.aws.ec2.transitgateway.workflow.create;

import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ResourceModel;
import com.aws.ec2.transitgateway.workflow.ExceptionMapper;
import com.aws.ec2.transitgateway.workflow.TagUtils;
import com.aws.ec2.transitgateway.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRequestOptions;
import software.amazon.awssdk.services.ec2.model.TransitGatewayState;
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
        ProxyClient<Ec2Client> proxyClient,
        Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = proxyClient;
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

    private CreateTransitGatewayRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayRequest.builder()
            .description(model.getDescription())
            .options(TransitGatewayRequestOptions.builder()
                .amazonSideAsn(model.getAmazonSideAsn().longValue())
                .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
                .dnsSupport(model.getDnsSupport())
                .multicastSupport(model.getMulticastSupport())
                .vpnEcmpSupport(model.getVpnEcmpSupport())
                .build())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .build();
    }

    private CreateTransitGatewayResponse makeServiceCall(CreateTransitGatewayRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGateway);
    }

    private boolean stabilize(
        CreateTransitGatewayRequest awsRequest,
        CreateTransitGatewayResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setId(awsResponse.transitGateway().transitGatewayId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
        return TransitGatewayState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
