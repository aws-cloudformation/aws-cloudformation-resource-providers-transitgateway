package software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.create;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupSourcesRequest;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupSourcesResponse;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupsource.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroupsource.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.read.Read;

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

    private RegisterTransitGatewayMulticastGroupSourcesRequest translateModelToRequest(ResourceModel model) {
        String[] networkInterfaceIds = { model.getNetworkInterfaceId() };
        return RegisterTransitGatewayMulticastGroupSourcesRequest.builder()
            .groupIpAddress(model.getGroupIpAddress())
            .networkInterfaceIds(networkInterfaceIds)
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .build();
    }

    private RegisterTransitGatewayMulticastGroupSourcesResponse makeServiceCall(RegisterTransitGatewayMulticastGroupSourcesRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::registerTransitGatewayMulticastGroupSources);
    }

    private boolean stabilize(
        RegisterTransitGatewayMulticastGroupSourcesRequest awsRequest,
        RegisterTransitGatewayMulticastGroupSourcesResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {
        this.logger.log("CREATE GROUP SOURCE SIMPLE REQUEST");

        ResourceModel current = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        this.logger.log("CREATE GROUP SOURCE SIMPLE REQUEST");
        if(current != null) {
            this.logger.log(current.toString());
        }
        return current != null;
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(RegisterTransitGatewayMulticastGroupSourcesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
