package software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.delete;

import software.amazon.ec2.transitgatewaymulticastdomainassociation.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DisassociateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.DisassociateTransitGatewayMulticastDomainResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulitcastDomainAssociationState;
import software.amazon.cloudformation.proxy.*;

public class Delete {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Delete(
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
            .stabilize(this::stabilize)
            .handleError(this::handleError)
            .progress();

    }

    private DisassociateTransitGatewayMulticastDomainRequest translateModelToRequest(ResourceModel model) {
        return  DisassociateTransitGatewayMulticastDomainRequest.builder()
            .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .subnetIds(model.getSubnetId())
            .build();
    }

    private DisassociateTransitGatewayMulticastDomainResponse makeServiceCall(DisassociateTransitGatewayMulticastDomainRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::disassociateTransitGatewayMulticastDomain);
    }

    private boolean stabilize (
        DisassociateTransitGatewayMulticastDomainRequest request,
        DisassociateTransitGatewayMulticastDomainResponse response,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {

        ResourceModel current = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model);
        return current == null || current.getState().equals(TransitGatewayMulitcastDomainAssociationState.DISASSOCIATED.toString());
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DisassociateTransitGatewayMulticastDomainRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
