package software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.create;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupMembersRequest;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupMembersResponse;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupmember.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroupmember.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.read.Read;

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

    private RegisterTransitGatewayMulticastGroupMembersRequest translateModelToRequest(ResourceModel model) {
        String[] networkInterfaceIds = { model.getNetworkInterfaceId() };
        return RegisterTransitGatewayMulticastGroupMembersRequest.builder()
            .groupIpAddress(model.getGroupIpAddress())
            .networkInterfaceIds(networkInterfaceIds)
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .build();
    }

    private RegisterTransitGatewayMulticastGroupMembersResponse makeServiceCall(RegisterTransitGatewayMulticastGroupMembersRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::registerTransitGatewayMulticastGroupMembers);
    }

    private boolean stabilize(
        RegisterTransitGatewayMulticastGroupMembersRequest awsRequest,
        RegisterTransitGatewayMulticastGroupMembersResponse awsResponse,
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

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(RegisterTransitGatewayMulticastGroupMembersRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
