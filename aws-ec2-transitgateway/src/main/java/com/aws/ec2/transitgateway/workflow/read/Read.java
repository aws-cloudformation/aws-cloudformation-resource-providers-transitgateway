package com.aws.ec2.transitgateway.workflow.read;

import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ClientBuilder;
import com.aws.ec2.transitgateway.ResourceModel;
import com.aws.ec2.transitgateway.workflow.ExceptionMapper;
import com.aws.ec2.transitgateway.workflow.TagUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.TransitGatewayState;
import software.amazon.cloudformation.proxy.*;

public class Read {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Read(
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

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .handleError(this::handleError)
            .done(this::done);
    }

    public ResourceModel simpleRequest(final ResourceModel model) {
        return this.translateResponseToModel(
            this.proxy.injectCredentialsAndInvokeV2(
                this.translateModelToRequest(model),
                this.client.client()::describeTransitGateways
            )
        );
    }

    public TransitGatewayState stateRequest(final ResourceModel model){
        return this.proxy.injectCredentialsAndInvokeV2(
                this.translateModelToRequest(model),
                this.client.client()::describeTransitGateways
        ).transitGateways().get(0).state();
    }

    private DescribeTransitGatewaysRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewaysRequest.builder()
            .transitGatewayIds(model.getId())
            .build();
    }

    private DescribeTransitGatewaysResponse makeServiceCall(DescribeTransitGatewaysRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGateways);
    }

    private ResourceModel translateResponseToModel(DescribeTransitGatewaysResponse awsResponses) {
        TransitGateway response = awsResponses.transitGateways().get(0);
        return ResourceModel.builder()
                .id(response.transitGatewayId())
                .amazonSideAsn(response.options().amazonSideAsn())
                .autoAcceptSharedAttachments(response.options().autoAcceptSharedAttachmentsAsString())
                .defaultRouteTableAssociation(response.options().defaultRouteTableAssociationAsString())
                .defaultRouteTablePropagation(response.options().defaultRouteTablePropagationAsString())
                .description(response.description())
                .dnsSupport(response.options().dnsSupportAsString())
                .multicastSupport(response.options().multicastSupportAsString())
                .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
                .vpnEcmpSupport(response.options().vpnEcmpSupportAsString())
                .transitGatewayCidrBlocks(response.options().transitGatewayCidrBlocks())
                .build();
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewaysRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewaysResponse response) {
        return ProgressEvent.defaultSuccessHandler(this.translateResponseToModel(response));
    }

}
