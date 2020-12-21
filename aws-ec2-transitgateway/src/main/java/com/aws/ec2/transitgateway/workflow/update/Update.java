package com.aws.ec2.transitgateway.workflow.update;

import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ResourceModel;
import com.aws.ec2.transitgateway.workflow.ExceptionMapper;
import com.aws.ec2.transitgateway.workflow.OptionUtils;
import com.aws.ec2.transitgateway.workflow.TagUtils;
import com.aws.ec2.transitgateway.workflow.read.Read;
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

    private ModifyTransitGatewayRequest translateModelToRequest(ResourceModel model) {
        return ModifyTransitGatewayRequest.builder()
                .transitGatewayId(model.getId())
                .description(model.getDescription())
                .options(OptionUtils.transitGatewayOptions(model))
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
        return TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(ModifyTransitGatewayRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }


}
