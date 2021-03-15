package software.amazon.ec2.transitgatewayconnectpeer.workflow.create;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.ec2.transitgatewayconnectpeer.CallbackContext;
import software.amazon.ec2.transitgatewayconnectpeer.ResourceModel;
import software.amazon.ec2.transitgatewayconnectpeer.Tag;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.TagUtils;
import software.amazon.ec2.transitgatewayconnectpeer.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

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

    private CreateTransitGatewayConnectPeerRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags());
        return CreateTransitGatewayConnectPeerRequest.builder()
            .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .bgpOptions(TransitGatewayConnectRequestBgpOptions
                    .builder()
                    .peerAsn(model.getBgpOptions().getPeerAsn().longValue())
                    .build())
            .insideCidrBlocks(model.getConnectPeerConfiguration().getInsideCidrBlocks())
            .peerAddress(model.getConnectPeerConfiguration().getPeerAddress())
            .transitGatewayAddress(model.getConnectPeerConfiguration().getBgpConfigurations().getTransitGatewayAddress())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(tags))
            .build();
    }

    private CreateTransitGatewayConnectPeerResponse makeServiceCall(CreateTransitGatewayConnectPeerRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayConnectPeer);
    }

    private boolean stabilize(
        CreateTransitGatewayConnectPeerRequest awsRequest,
        CreateTransitGatewayConnectPeerResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayAttachmentId(awsResponse.transitGatewayConnectPeer().transitGatewayConnectPeerId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayConnectPeerRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

}
