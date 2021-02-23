package software.amazon.ec2.transitgatewayprefixlistreference.workflow.create;

import software.amazon.ec2.transitgatewayprefixlistreference.CallbackContext;
import software.amazon.ec2.transitgatewayprefixlistreference.ResourceModel;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayPrefixListReferenceRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayPrefixListReferenceResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayPrefixListReferenceState;
import software.amazon.cloudformation.proxy.*;

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

    private CreateTransitGatewayPrefixListReferenceRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayPrefixListReferenceRequest.builder()
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .prefixListId(model.getPrefixListId())
            .blackhole(model.getBlackhole())
            .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .build();
    }

    private CreateTransitGatewayPrefixListReferenceResponse makeServiceCall(CreateTransitGatewayPrefixListReferenceRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayPrefixListReference);
    }

    private boolean stabilize(
        CreateTransitGatewayPrefixListReferenceRequest awsRequest,
        CreateTransitGatewayPrefixListReferenceResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setPrefixListId(awsResponse.transitGatewayPrefixListReference().prefixListId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayPrefixListReferenceState.AVAILABLE.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayPrefixListReferenceRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
