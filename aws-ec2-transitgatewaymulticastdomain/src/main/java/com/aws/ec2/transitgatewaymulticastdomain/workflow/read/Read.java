package com.aws.ec2.transitgatewaymulticastdomain.workflow.read;

import com.aws.ec2.transitgatewaymulticastdomain.CallbackContext;
import com.aws.ec2.transitgatewaymulticastdomain.ResourceModel;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.TagUtils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomainState;
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
        ProxyClient<Ec2Client> client,
        Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
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
        return this.translateResponsesToModel(
            this.proxy.injectCredentialsAndInvokeV2(
                this.translateModelToRequest(model),
                this.client.client()::describeTransitGatewayMulticastDomains
            )
        );
    }

    private DescribeTransitGatewayMulticastDomainsRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayMulticastDomainsRequest.builder()
            .transitGatewayMulticastDomainIds(model.getTransitGatewayMulticastDomainId())
            .build();
    }

    private DescribeTransitGatewayMulticastDomainsResponse makeServiceCall(DescribeTransitGatewayMulticastDomainsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayMulticastDomains);
    }

    private ResourceModel translateResponsesToModel(DescribeTransitGatewayMulticastDomainsResponse awsResponse) {
        TransitGatewayMulticastDomain response = awsResponse.transitGatewayMulticastDomains().get(0);

        return ResourceModel.builder()
            .transitGatewayMulticastDomainId(response.transitGatewayMulticastDomainId())
            .transitGatewayId(response.transitGatewayId())
            .state(response.state().toString())
            .creationTime(response.creationTime().toString())
            .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
            .build();
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayMulticastDomainsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewayMulticastDomainsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response);
        if(model.getState().equals(TransitGatewayMulticastDomainState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response));
        }
    }
}
