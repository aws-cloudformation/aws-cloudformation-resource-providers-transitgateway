package com.aws.ec2.transitgateway.workflow.update;

import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ResourceModel;
import com.aws.ec2.transitgateway.Tag;
import com.aws.ec2.transitgateway.workflow.ExceptionMapper;
import com.aws.ec2.transitgateway.workflow.TagUtils;
import com.aws.ec2.transitgateway.workflow.read.Read;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
                    .addTransitGatewayCidrBlocks(cidrBlocksToCreate(model))
                    .removeTransitGatewayCidrBlocks(cidrBlocksToDelete(model))
                    .associationDefaultRouteTableId(model.getAssociationDefaultRouteTableId())
                    .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                    .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                    .vpnEcmpSupport(model.getVpnEcmpSupport())
                    .dnsSupport(model.getDnsSupport())
                    .propagationDefaultRouteTableId(model.getPropagationDefaultRouteTableId())
                    .build();
    }

    private List<String> cidrBlocksToCreate(ResourceModel model) {
        List<String> prevCidrBlocks = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getTransitGatewayCidrBlocks();
        List<String> currCidrBlocks = model.getTransitGatewayCidrBlocks();
        return difference(currCidrBlocks, prevCidrBlocks);
    }

    private List<String> cidrBlocksToDelete(ResourceModel model) {
        final List<String> prevCidrBlocks =  new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getTransitGatewayCidrBlocks();
        List<String> currCidrBlocks = model.getTransitGatewayCidrBlocks();
        return difference(prevCidrBlocks, currCidrBlocks);
    }

    public static List<String> difference(List<String>  cidr1, List<String> cidr2) {
        return Sets.difference(listToSet(cidr1), listToSet(cidr2)).immutableCopy().asList();
    }

    public static Set<String> listToSet(final List<String> cidrs) {
        return CollectionUtils.isEmpty(cidrs) ? new HashSet<>() : new HashSet<>(cidrs);
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
