package software.amazon.ec2.transitgatewayroutetable.workflow.create;

import software.amazon.ec2.transitgatewayroutetable.CallbackContext;
import software.amazon.ec2.transitgatewayroutetable.ResourceModel;
import software.amazon.ec2.transitgatewayroutetable.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroutetable.workflow.TagUtils;
import software.amazon.ec2.transitgatewayroutetable.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteTableRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteTableResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTableState;
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
        this.logger.log(progress.getResourceModel().toString());
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTransitGatewayRouteTableRequest translateModelToRequest(ResourceModel model) {
        return CreateTransitGatewayRouteTableRequest.builder()
            .transitGatewayId(model.getTransitGatewayId())
            .tagSpecifications(TagUtils.cfnTagsToSdkTagSpecifications(model.getTags()))
            .build();
    }

    private CreateTransitGatewayRouteTableResponse makeServiceCall(CreateTransitGatewayRouteTableRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTransitGatewayRouteTable);
    }

    private boolean stabilize(
        CreateTransitGatewayRouteTableRequest awsRequest,
        CreateTransitGatewayRouteTableResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        model.setTransitGatewayRouteTableId(awsResponse.transitGatewayRouteTable().transitGatewayRouteTableId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return {{Config.State.Available}}.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTransitGatewayRouteTableRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        this.logger.log("handleError");
        System.out.println(exception);
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
