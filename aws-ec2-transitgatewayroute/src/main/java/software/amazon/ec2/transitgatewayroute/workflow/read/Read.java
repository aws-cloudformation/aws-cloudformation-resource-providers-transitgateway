package software.amazon.ec2.transitgatewayroute.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import software.amazon.ec2.transitgatewayroute.TransitGatewayRouteAttachment;
import software.amazon.ec2.transitgatewayroute.workflow.ExceptionMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

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

    public ResourceModel simpleRequest(ResourceModel model) {
        SearchTransitGatewayRoutesRequest request = this.translateModelToRequest(model);
        SearchTransitGatewayRoutesResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::searchTransitGatewayRoutes);
        return this.translateResponsesToModel(response, model);
    }

    private SearchTransitGatewayRoutesRequest translateModelToRequest(ResourceModel model) {
        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("route-search.exact-match").values(model.getDestinationCidrBlock()).build());
        return SearchTransitGatewayRoutesRequest.builder()
            .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .filters(filters)
        .build();
    }

    private SearchTransitGatewayRoutesResponse makeServiceCall(SearchTransitGatewayRoutesRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::searchTransitGatewayRoutes);
    }

    private ResourceModel translateResponsesToModel(SearchTransitGatewayRoutesResponse awsResponse, ResourceModel model) {
        if(awsResponse.routes().isEmpty()) {
            return null;
        } else {
            TransitGatewayRoute response = awsResponse.routes().get(0);
            return ResourceModel.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .destinationCidrBlock(response.destinationCidrBlock())
                .blackhole(response.state().toString().equals("blackhole") ? true : false)
                .transitGatewayAttachmentId(response.transitGatewayAttachments().get(0).transitGatewayAttachmentId())
                .transitGatewayAttachments(
                    response.transitGatewayAttachments().stream().map(e ->
                        TransitGatewayRouteAttachment.builder()
                            .resourceId(e.resourceId())
                            .resourceType(e.resourceTypeAsString())
                            .transitGatewayAttachmentId(e.transitGatewayAttachmentId())
                        .build()
                    ).collect(Collectors.toList())
                )
                .prefixListId(response.prefixListId())
                .type(response.type().toString())
                .state(response.state().toString())
            .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(SearchTransitGatewayRoutesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(SearchTransitGatewayRoutesResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayRouteState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
