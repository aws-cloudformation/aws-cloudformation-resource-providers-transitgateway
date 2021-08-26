package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.read;

import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.ExceptionMapper;
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
    ProgressEvent<ResourceModel, CallbackContext> progress;

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

    public ProgressEvent<ResourceModel, CallbackContext> run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .handleError(this::handleError)
                .done(this::done);
    }

    public ResourceModel simpleRequest(final ResourceModel model) {
        GetTransitGatewayRouteTablePropagationsRequest request = this.translateModelToRequest(model);
        GetTransitGatewayRouteTablePropagationsResponse response = this.proxy.injectCredentialsAndInvokeV2(request,
                this.client.client()::getTransitGatewayRouteTablePropagations);
        return this.translateResponsesToModel(response, model);
    }

    private GetTransitGatewayRouteTablePropagationsRequest translateModelToRequest(ResourceModel model) {
        java.util.List<Filter> filters = new ArrayList<>();
        filters.add(Filter.builder().name("transit-gateway-attachment-id").values(model.getTransitGatewayAttachmentId()).build());

        return GetTransitGatewayRouteTablePropagationsRequest.builder()
                .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                .filters(filters)
                .build();
    }

    private ResourceModel translateResponsesToModel(GetTransitGatewayRouteTablePropagationsResponse awsResponse, ResourceModel model) {
        this.logger.log("AWS RESPONSE");
        this.logger.log(awsResponse.toString());
        if(awsResponse.transitGatewayRouteTablePropagations().isEmpty()) {
            this.logger.log("NO RESPONSE");
            return null;
        } else {
            TransitGatewayRouteTablePropagation response = awsResponse.transitGatewayRouteTablePropagations().get(0);
            return ResourceModel.builder()
                    .transitGatewayAttachmentId(response.transitGatewayAttachmentId())
                    .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
                    .state(response.state().toString())
                    .resourceId(response.resourceId())
                    .resourceType(response.resourceTypeAsString())
                    .build();
        }
    }

    private GetTransitGatewayRouteTablePropagationsResponse makeServiceCall(GetTransitGatewayRouteTablePropagationsRequest awsRequest,
                                                                            ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayRouteTablePropagations);
    }

    private ProgressEvent<ResourceModel, CallbackContext> handleError(GetTransitGatewayRouteTablePropagationsRequest awsRequest,
                                                                       Exception exception, ProxyClient<Ec2Client> client,
                                                                       ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder()
                    .awsErrorDetails(AwsErrorDetails.builder()
                            .errorCode("NotFound")
                            .errorMessage("Not Found")
                            .build())
                    .build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> done(GetTransitGatewayRouteTablePropagationsResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayPropagationState.DISABLED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, model));
        }
    }
}
