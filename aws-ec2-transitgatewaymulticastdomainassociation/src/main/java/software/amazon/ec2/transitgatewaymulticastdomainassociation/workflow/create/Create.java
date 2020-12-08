package software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.create;

import software.amazon.ec2.transitgatewaymulticastdomainassociation.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AssociateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.AssociateTransitGatewayMulticastDomainResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulitcastDomainAssociationState;
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

    private AssociateTransitGatewayMulticastDomainRequest translateModelToRequest(ResourceModel model) {
        return AssociateTransitGatewayMulticastDomainRequest.builder()
            .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .subnetIds(model.getSubnetId())
            .build();
    }

    private AssociateTransitGatewayMulticastDomainResponse makeServiceCall(AssociateTransitGatewayMulticastDomainRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::associateTransitGatewayMulticastDomain);
    }

    private boolean stabilize(
        AssociateTransitGatewayMulticastDomainRequest awsRequest,
        AssociateTransitGatewayMulticastDomainResponse awsResponse,
        ProxyClient<Ec2Client> client,
        ResourceModel model,
        CallbackContext context
    ) {

        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getState();
        return TransitGatewayMulitcastDomainAssociationState.ASSOCIATED.toString().equals(currentState);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(AssociateTransitGatewayMulticastDomainRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        this.logger.log("handleError");
        System.out.println(exception);
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
