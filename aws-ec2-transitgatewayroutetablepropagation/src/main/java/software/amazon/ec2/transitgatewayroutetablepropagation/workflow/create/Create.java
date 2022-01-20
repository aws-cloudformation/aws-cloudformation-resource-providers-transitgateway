package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.create;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.read.Read;

public class Create {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;

    public Create(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            ProxyClient<Ec2Client> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.logger = logger;
        this.callbackContext = callbackContext;
        this.client = client;
    }

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();
    }

    private EnableTransitGatewayRouteTablePropagationRequest translateModelToRequest(ResourceModel model) {
        return EnableTransitGatewayRouteTablePropagationRequest.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
                .build();
    }

    private EnableTransitGatewayRouteTablePropagationResponse makeServiceCall(EnableTransitGatewayRouteTablePropagationRequest request,
                                                                              ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::enableTransitGatewayRouteTablePropagation);
    }

    private boolean stabilize(
            EnableTransitGatewayRouteTablePropagationRequest awsRequest,
            EnableTransitGatewayRouteTablePropagationResponse awsResponse,
            ProxyClient<Ec2Client> client,
            ResourceModel model,
            CallbackContext context
    ) {
        model.setTransitGatewayRouteTableId(awsResponse.propagation().transitGatewayRouteTableId());
        model.setTransitGatewayAttachmentId(awsResponse.propagation().transitGatewayAttachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger)
                .simpleRequest(model).getState();
        return TransitGatewayPropagationState.ENABLED.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext> handleError(EnableTransitGatewayRouteTablePropagationRequest awsRequest,
                                                                         Exception exception, ProxyClient<Ec2Client> client,
                                                                         ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
