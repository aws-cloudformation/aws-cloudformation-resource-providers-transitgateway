package software.amazon.ec2.transitgatewayconnectpeer.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayconnectpeer.BgpConfigurations;
import software.amazon.ec2.transitgatewayconnectpeer.CallbackContext;
import software.amazon.ec2.transitgatewayconnectpeer.ResourceModel;
import software.amazon.ec2.transitgatewayconnectpeer.TransitGatewayConnectPeerConfiguration;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.TagUtils;

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
        DescribeTransitGatewayConnectPeersRequest request = this.translateModelToRequest(model);
        DescribeTransitGatewayConnectPeersResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::describeTransitGatewayConnectPeers);
        return this.translateResponsesToModel(response, model);
    }

    private DescribeTransitGatewayConnectPeersRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayConnectPeersRequest.builder()
                   .transitGatewayConnectPeerIds(model.getTransitGatewayConnectPeerId())
            .build();
    }

    private DescribeTransitGatewayConnectPeersResponse makeServiceCall(DescribeTransitGatewayConnectPeersRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayConnectPeers);
    }

    private ResourceModel translateResponsesToModel(DescribeTransitGatewayConnectPeersResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayConnectPeers().isEmpty()) {
            return null;
        } else {
            TransitGatewayConnectPeer response = awsResponse.transitGatewayConnectPeers().get(0);
            return ResourceModel.builder()
                .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
                .connectPeerConfiguration(TransitGatewayConnectPeerConfiguration
                        .builder()
                        .peerAddress(response.connectPeerConfiguration().peerAddress())
                        .bgpConfigurations(BgpConfigurations.builder().bgpStatus().build())
                        .build())
                .transitGatewayConnectPeerId(response.transitGatewayConnectPeerId())
                .state(response.state().toString())
                .creationTime(response.creationTime().toString())
                .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
                .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayConnectPeersRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewayConnectPeersResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayAttachmentState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
