package com.aws.ec2.transitgatewayattachment.workflow.create;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.Tag;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayattachment.workflow.TagUtils;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayVpcAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayVpcAttachmentResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;
    ResourceModel stableResponse;

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
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTransitGatewayVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = (model.getTags() != null) ? com.aws.ec2.transitgatewayattachment.workflow.TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags())
                : new ArrayList<Tag>();
        logger.log("request parameters"+model.getSubnetIds()+","+model.getTransitGatewayId()+","+model.getVpcId()+"+"+TagUtils.cfnTagsToSdkTagSpecifications(tags));

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
        model.setId(awsResponse.transitGatewayVpcAttachment().transitGatewayAttachmentId());
        String currentState;
        ResourceModel currentResourceModel;
        try {
            currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
            currentResourceModel = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        } catch (Exception e) {
            // If we got this far, this means CREATION was successful, and any failures to READ are most
            // likely due to failures unrelated to the integrity of the attachment (EX eventual consistency of read).
            // We should not suddenly get validation errors (EX malformed request) because create would have failed.
            currentResourceModel = null;
            currentState = null;
        }
        boolean isStable = currentState != null && currentResourceModel != null && TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
        if (isStable) {
            this.stableResponse = currentResourceModel;
        }
        return isStable;
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayVpcAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
