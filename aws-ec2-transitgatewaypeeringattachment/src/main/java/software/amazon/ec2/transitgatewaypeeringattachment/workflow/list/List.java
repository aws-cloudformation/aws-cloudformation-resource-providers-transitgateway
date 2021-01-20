package software.amazon.ec2.transitgatewaypeeringattachment.workflow.list;

import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.ExceptionMapper;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayPeeringAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayPeeringAttachmentsResponse;
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
        DescribeTransitGatewayPeeringAttachmentsRequest  awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            DescribeTransitGatewayPeeringAttachmentsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(awsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }
    }

    private DescribeTransitGatewayPeeringAttachmentsRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayPeeringAttachmentsRequest.builder()
            .maxResults(50)
            .nextToken(this.request.getNextToken()).build();
    }

    private DescribeTransitGatewayPeeringAttachmentsResponse makeServiceCall(DescribeTransitGatewayPeeringAttachmentsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayPeeringAttachments);
    }

    private java.util.List<ResourceModel> translateResponseToModel(DescribeTransitGatewayPeeringAttachmentsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.transitGatewayPeeringAttachments())
            .filter(resource -> !(resource.stateAsString().equals("deleted")))
            .map(resource -> ResourceModel.builder()
                .transitGatewayAttachmentId(resource.transitGatewayAttachmentId())
                .build())
            .collect(Collectors.toList());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayPeeringAttachmentsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
