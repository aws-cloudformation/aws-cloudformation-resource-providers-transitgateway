package software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayMulticastGroupsRequest;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayMulticastGroupsResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastGroup;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupsource.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastgroupsource.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastgroupsource.workflow.ExceptionMapper;

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
        this.logger.log("REAL READ REQUEST");
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .handleError(this::handleError)
            .done(this::done);
    }

    public ResourceModel simpleRequest(ResourceModel model) {
        SearchTransitGatewayMulticastGroupsRequest request = this.translateModelToRequest(model);
        SearchTransitGatewayMulticastGroupsResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::searchTransitGatewayMulticastGroups);
        return this.translateResponsesToModel(response, model);
    }

    private SearchTransitGatewayMulticastGroupsRequest translateModelToRequest(ResourceModel model) {
        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("group-ip-address").values(model.getGroupIpAddress()).build());
        filters.add(Filter.builder().name("network-interface-id").values(model.getNetworkInterfaceId()).build());
        filters.add(Filter.builder().name("is-group-source").values("true").build());
        this.logger.log("READ REQUEST");
        this.logger.log(model.getTransitGatewayMulticastDomainId());
        this.logger.log(filters.toString());
        this.logger.log("READ REQUEST");
        return SearchTransitGatewayMulticastGroupsRequest.builder()
            .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
            .filters(filters)
            .build();
    }

    private SearchTransitGatewayMulticastGroupsResponse makeServiceCall(SearchTransitGatewayMulticastGroupsRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::searchTransitGatewayMulticastGroups);
    }

    private ResourceModel translateResponsesToModel(SearchTransitGatewayMulticastGroupsResponse awsResponse, ResourceModel model) {
        this.logger.log("AWS RESPONSE");
        this.logger.log(awsResponse.toString());
        if(awsResponse.multicastGroups().isEmpty()) {
            this.logger.log("NO RESPONSE");
            return null;
        } else {
            TransitGatewayMulticastGroup response = awsResponse.multicastGroups().get(0);
            return ResourceModel.builder()
                .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
                .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
                .groupIpAddress(response.groupIpAddress())
                .groupMember(response.groupMember())
                .subnetId(response.subnetId())
                .groupSource(response.groupSource())
                .groupIpAddress(response.groupIpAddress())
                .networkInterfaceId(response.networkInterfaceId())
                .memberType(response.memberTypeAsString())
                .resourceId(response.resourceId())
                .resourceType(response.resourceTypeAsString())
                .sourceType(response.sourceTypeAsString())
                .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(SearchTransitGatewayMulticastGroupsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(SearchTransitGatewayMulticastGroupsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
