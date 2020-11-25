package com.aws.ec2.transitgatewayattachment.workflow.list;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ClientBuilder;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;

    public List(
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

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        DescribeTransitGatewaysRequest  awsRequest = this.translateModelToRequest(progress.getResourceModel());
        try{
            DescribeTransitGatewaysResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(awsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (final AwsServiceException e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }
    }

    private DescribeTransitGatewaysRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewaysRequest.builder()
            .maxResults(50)
            .nextToken(this.request.getNextToken()).build();
    }

    private DescribeTransitGatewaysResponse makeServiceCall(DescribeTransitGatewaysRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGateways);
    }

    private java.util.List<ResourceModel> translateResponseToModel(DescribeTransitGatewaysResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.transitGateways())
            .map(resource -> ResourceModel.builder()
                .id(resource.transitGatewayId())
                .build())
            .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewaysRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
