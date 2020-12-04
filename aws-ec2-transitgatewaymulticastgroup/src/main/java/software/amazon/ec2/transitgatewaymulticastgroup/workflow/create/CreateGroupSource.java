package software.amazon.ec2.transitgatewaymulticastgroup.workflow.create;

import software.amazon.ec2.transitgatewaymulticastgroup.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroup.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroup.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastgroup.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupSourcesRequest;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupSourcesResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupSource {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public CreateGroupSource(
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
        if(!progress.getResourceModel().getGroupSource()) {
            return progress;
        } else {
            return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                    .translateToServiceRequest(this::translateModelToRequest)
                    .makeServiceCall(this::makeServiceCall)
                    .stabilize(this::stabilize)
                    .handleError(this::handleError)
                    .progress();
        }
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
        this.logger.log(current.toString());
        this.logger.log("CREATE GROUP SOURCE SIMPLE REQUEST");

        return current != null;
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(RegisterTransitGatewayMulticastGroupSourcesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
