package software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.list;

import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomainAssociation;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastdomainassociation.workflow.ExceptionMapper;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayMulticastDomainAssociationsRequest;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayMulticastDomainAssociationsResponse;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class List {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;

    public List(
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
       this.progress=progress;
        GetTransitGatewayMulticastDomainAssociationsRequest  awsRequest = this.translateModelToRequest(progress.getResourceModel());

        try{
            GetTransitGatewayMulticastDomainAssociationsResponse awsResponse = this.makeServiceCall(awsRequest, this.client);
            java.util.List<ResourceModel> models = this.translateResponseToModel(awsResponse);
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(awsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
        } catch (final Exception e) {
            return this.handleError(awsRequest, e, this.client, this.request.getDesiredResourceState(), this.callbackContext);
        }
    }

    private GetTransitGatewayMulticastDomainAssociationsRequest translateModelToRequest(ResourceModel model) {

        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("transit-gateway-attachment-id").values(model.getTransitGatewayAttachmentId()).build());
        filters.add(Filter.builder().name("subnet-id").values(model.getSubnetId()).build());
        return GetTransitGatewayMulticastDomainAssociationsRequest.builder()
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .filters(filters)
            .maxResults(50)
            .nextToken(this.request.getNextToken()).build();
    }

    private GetTransitGatewayMulticastDomainAssociationsResponse makeServiceCall(GetTransitGatewayMulticastDomainAssociationsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayMulticastDomainAssociations);
    }

    private java.util.List<ResourceModel> translateResponseToModel(GetTransitGatewayMulticastDomainAssociationsResponse awsResponse) {
        return streamOfOrEmpty(awsResponse.multicastDomainAssociations())
            .map(resource -> ResourceModel.builder()
                .transitGatewayAttachmentId(resource.transitGatewayAttachmentId())
                .transitGatewayMulticastDomainId(this.progress.getResourceModel().getTransitGatewayMulticastDomainId())
                .subnetId(resource.subnet().subnetId())
                .build())
            .collect(Collectors.toList());
    }



    private ProgressEvent<ResourceModel, CallbackContext>  handleError(GetTransitGatewayMulticastDomainAssociationsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
    }
}
