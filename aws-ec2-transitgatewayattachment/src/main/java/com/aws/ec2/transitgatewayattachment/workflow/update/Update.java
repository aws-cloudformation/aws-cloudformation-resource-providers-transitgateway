package com.aws.ec2.transitgatewayattachment.workflow.update;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayattachment.workflow.TagUtils;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.HashSet;
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
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private ModifyTransitGatewayVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        return ModifyTransitGatewayVpcAttachmentRequest.builder()
                .transitGatewayAttachmentId(model.getId())
                .addSubnetIds(difference(request.getDesiredResourceState().getSubnetIds(), request.getPreviousResourceState().getSubnetIds()))
                .removeSubnetIds(difference(request.getPreviousResourceState().getSubnetIds(), request.getDesiredResourceState().getSubnetIds()))
                .build();
    }

    private ModifyTransitGatewayVpcAttachmentResponse makeServiceCall(ModifyTransitGatewayVpcAttachmentRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::modifyTransitGatewayVpcAttachment);
    }

    private boolean stabilize(
            ModifyTransitGatewayVpcAttachmentRequest awsRequest,
            ModifyTransitGatewayVpcAttachmentResponse awsResponse,
            ProxyClient<Ec2Client> client,
            ResourceModel model,
            CallbackContext context
    ) {
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
        return TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(ModifyTransitGatewayVpcAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    public static List<String> difference(List<String>  subnetIds1, List<String> subnetIds2) {
        return Sets.difference(listToSet(subnetIds1), listToSet(subnetIds2)).immutableCopy().asList();
    }

    public static java.util.Set<String> listToSet(final List<String> subnetIds) {
        if(subnetIds == null) return null;
        return CollectionUtils.isEmpty(subnetIds) ? new HashSet<>() : new HashSet<>(subnetIds);
    }
}
