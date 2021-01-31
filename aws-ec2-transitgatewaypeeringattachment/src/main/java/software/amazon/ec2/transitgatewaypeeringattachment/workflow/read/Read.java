package software.amazon.ec2.transitgatewaypeeringattachment.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.PeeringAttachmentStatus;
import software.amazon.ec2.transitgatewaypeeringattachment.PeeringTgwInfo;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.TagUtils;

import java.util.ArrayList;

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

    public ResourceModel simpleRequest(ResourceModel model) {
        DescribeTransitGatewayPeeringAttachmentsRequest request = this.translateModelToRequest(model);
        DescribeTransitGatewayPeeringAttachmentsResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::describeTransitGatewayPeeringAttachments);
        return this.translateResponsesToModel(response, model);
    }

    private DescribeTransitGatewayPeeringAttachmentsRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayPeeringAttachmentsRequest.builder()
                   .transitGatewayAttachmentIds(model.getTransitGatewayAttachmentId())
            .build();
    }

    private DescribeTransitGatewayPeeringAttachmentsResponse makeServiceCall(DescribeTransitGatewayPeeringAttachmentsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayPeeringAttachments);
    }

    private ResourceModel translateResponsesToModel(DescribeTransitGatewayPeeringAttachmentsResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayPeeringAttachments().isEmpty()) {
            return null;
        } else {
            TransitGatewayPeeringAttachment response = awsResponse.transitGatewayPeeringAttachments().get(0);
            return ResourceModel.builder()
                .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
            .accepterTgwInfo(PeeringTgwInfo.builder()
                    .ownerId(response.accepterTgwInfo().ownerId())
                    .region(response.accepterTgwInfo().region())
                    .transitGatewayId(response.accepterTgwInfo().transitGatewayId()).build())
            .requesterTgwInfo(PeeringTgwInfo.builder()
                    .ownerId(response.requesterTgwInfo().ownerId())
                    .region(response.requesterTgwInfo().region())
                    .transitGatewayId(response.requesterTgwInfo().transitGatewayId()).build())
            .status(PeeringAttachmentStatus.builder()
                    .code(response.status().code())
                    .message(response.status().message())
                    .build())
            .state(response.state().toString())
            .creationTime(response.creationTime().toString())
            .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
            .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayPeeringAttachmentsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewayPeeringAttachmentsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayAttachmentState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
