package software.amazon.ec2.transitgatewayconnect.workflow.create;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.ec2.transitgatewayconnect.CallbackContext;
import software.amazon.ec2.transitgatewayconnect.ResourceModel;
import software.amazon.ec2.transitgatewayconnect.Tag;
import software.amazon.ec2.transitgatewayconnect.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayconnect.workflow.TagUtils;
import software.amazon.ec2.transitgatewayconnect.workflow.read.Read;
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

    private CreateTransitGatewayConnectRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags());
        return CreateTransitGatewayConnectRequest.builder()
            .transportTransitGatewayAttachmentId(model.getTransportTransitGatewayAttachmentId())
            .options(CreateTransitGatewayConnectRequestOptions.builder().protocol(model.getOptions().getProtocol()).build())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(tags))
            .build();
    }

    private CreateTransitGatewayConnectResponse makeServiceCall(CreateTransitGatewayConnectRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayConnect);
    }

    private boolean stabilize(
        CreateTransitGatewayConnectRequest awsRequest,
        CreateTransitGatewayConnectResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayAttachmentId(awsResponse.transitGatewayConnect().transitGatewayAttachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayAttachmentState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayConnectRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

}
