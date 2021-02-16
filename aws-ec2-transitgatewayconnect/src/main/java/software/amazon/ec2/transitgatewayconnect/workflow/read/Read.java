package software.amazon.ec2.transitgatewayconnect.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayconnect.CallbackContext;
import software.amazon.ec2.transitgatewayconnect.ResourceModel;
import software.amazon.ec2.transitgatewayconnect.TransitGatewayConnectOptions;
import software.amazon.ec2.transitgatewayconnect.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnect.workflow.TagUtils;

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
        DescribeTransitGatewayConnectsRequest request = this.translateModelToRequest(model);
        DescribeTransitGatewayConnectsResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::describeTransitGatewayConnects);
        return this.translateResponsesToModel(response, model);
    }

    private DescribeTransitGatewayConnectsRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayConnectsRequest.builder()
                   .transitGatewayAttachmentIds(model.getTransitGatewayAttachmentId())
            .build();
    }

    private DescribeTransitGatewayConnectsResponse makeServiceCall(DescribeTransitGatewayConnectsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayConnects);
    }

    private ResourceModel translateResponsesToModel(DescribeTransitGatewayConnectsResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayConnects().isEmpty()) {
            return null;
        } else {
            TransitGatewayConnect response = awsResponse.transitGatewayConnects().get(0);
            return ResourceModel.builder()
                .options(TransitGatewayConnectOptions.builder().protocol(response.options().protocol().toString()).build())
                .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
                .transportTransitGatewayAttachmentId(response.transportTransitGatewayAttachmentId())
                .transitGatewayId(response.transitGatewayId())
                .state(response.state().toString())
                .creationTime(response.creationTime().toString())
            .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
                .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayConnectsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewayConnectsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayAttachmentState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
