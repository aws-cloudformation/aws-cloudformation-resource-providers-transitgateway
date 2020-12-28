package software.amazon.ec2.transitgatewayroutetable.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroutetable.CallbackContext;
import software.amazon.ec2.transitgatewayroutetable.ResourceModel;
import software.amazon.ec2.transitgatewayroutetable.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewayroutetable.workflow.TagUtils;

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

    public ResourceModel simpleRequest(ResourceModel model) {
        DescribeTransitGatewayRouteTablesRequest request = this.translateModelToRequest(model);
        DescribeTransitGatewayRouteTablesResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::describeTransitGatewayRouteTables);
        return this.translateResponsesToModel(response, model);
    }

    private DescribeTransitGatewayRouteTablesRequest translateModelToRequest(ResourceModel model) {
        return DescribeTransitGatewayRouteTablesRequest.builder()
            .transitGatewayRouteTableIds(model.getTransitGatewayRouteTableId())
            .build();
    }

    private DescribeTransitGatewayRouteTablesResponse makeServiceCall(DescribeTransitGatewayRouteTablesRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTransitGatewayRouteTables);
    }

    private ResourceModel translateResponsesToModel(DescribeTransitGatewayRouteTablesResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayRouteTables().isEmpty()) {
            return null;
        } else {
            TransitGatewayRouteTable response = awsResponse.transitGatewayRouteTables().get(0);
            return ResourceModel.builder()
                .transitGatewayRouteTableId(response.transitGatewayRouteTableId())
                .transitGatewayId(response.transitGatewayId())
                .state(response.state().toString())
                .creationTime(response.creationTime().toString())
                .tags(TagUtils.sdkTagsToCfnTags(response.tags()))
                .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(DescribeTransitGatewayRouteTablesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(DescribeTransitGatewayRouteTablesResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayRouteTableState.DELETED.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}