package com.aws.ec2.transitgateway.workflow.update;

import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ResourceModel;
import com.aws.ec2.transitgateway.Tag;
import com.aws.ec2.transitgateway.workflow.ExceptionMapper;
import com.aws.ec2.transitgateway.workflow.TagUtils;
import com.aws.ec2.transitgateway.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

public class Update {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext> progress;

    public Update(
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

    private ModifyTransitGatewayRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags());

        return ModifyTransitGatewayRequest.builder()
                .transitGatewayId(model.getId())
                .description(model.getDescription())
                .options(getTransitGatewayModifyOptions(model))
                .build();
    }

    private ModifyTransitGatewayOptions getTransitGatewayModifyOptions(ResourceModel model){

            return ModifyTransitGatewayOptions.builder()
                    .addTransitGatewayCidrBlocks(model.getAddTransitGatewayCidrBlocks())
                    .removeTransitGatewayCidrBlocks(model.getRemoveTransitGatewayCidrBlocks())
                    .associationDefaultRouteTableId(model.getAssociationDefaultRouteTableId())
                    .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                    .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                    .vpnEcmpSupport(model.getVpnEcmpSupport())
                    .dnsSupport(model.getDnsSupport())
                    .propagationDefaultRouteTableId(model.getPropagationDefaultRouteTableId())
                    .build();
    }

    private ModifyTransitGatewayResponse makeServiceCall(ModifyTransitGatewayRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::modifyTransitGateway);
    }

    private boolean stabilize(
            ModifyTransitGatewayRequest awsRequest,
            ModifyTransitGatewayResponse awsResponse,
            ProxyClient<Ec2Client> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
        return TransitGatewayState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(ModifyTransitGatewayRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
