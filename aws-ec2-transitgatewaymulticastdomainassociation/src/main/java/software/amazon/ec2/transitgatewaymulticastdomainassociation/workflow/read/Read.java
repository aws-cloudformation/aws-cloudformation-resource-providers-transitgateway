package software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.read;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.ExceptionMapper;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;

public class Read {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public Read(
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
            .handleError(this::handleError)
            .done(this::done);
    }

    public ResourceModel simpleRequest(final ResourceModel model) {
        GetTransitGatewayMulticastDomainAssociationsRequest request = this.translateModelToRequest(model);
        GetTransitGatewayMulticastDomainAssociationsResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getTransitGatewayMulticastDomainAssociations);
        return this.translateResponsesToModel(response, model);
    }


    private GetTransitGatewayMulticastDomainAssociationsRequest translateModelToRequest(ResourceModel model) {
        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("transit-gateway-attachment-id").values(model.getTransitGatewayAttachmentId()).build());
        filters.add(Filter.builder().name("subnet-id").values(model.getSubnetId()).build());

        return GetTransitGatewayMulticastDomainAssociationsRequest.builder()
                .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
                .filters(filters)

            .build();
    }

    private ResourceModel translateResponsesToModel(GetTransitGatewayMulticastDomainAssociationsResponse awsResponse, ResourceModel model) {
        this.logger.log("AWS RESPONSE");
        this.logger.log(awsResponse.toString());
        if(awsResponse.multicastDomainAssociations().isEmpty()) {
            this.logger.log("NO RESPONSE");
            return null;
        } else {
            TransitGatewayMulticastDomainAssociation response = awsResponse.multicastDomainAssociations().get(0);
            return ResourceModel.builder()
                    .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
                    .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
                    .subnetId(response.subnet().subnetId())
                    .state(response.subnet().state().toString())
                    .resourceId(response.resourceId())
                    .resourceType(response.resourceTypeAsString())
                    .build();
        }
    }

    private GetTransitGatewayMulticastDomainAssociationsResponse makeServiceCall(GetTransitGatewayMulticastDomainAssociationsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayMulticastDomainAssociations);
    }


    private ProgressEvent<ResourceModel, CallbackContext>  handleError(GetTransitGatewayMulticastDomainAssociationsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(GetTransitGatewayMulticastDomainAssociationsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayMulitcastDomainAssociationState.DISASSOCIATED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, model));
        }
    }
}
