package software.amazon.ec2.transitgatewayprefixlistreference.workflow.read;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayprefixlistreference.CallbackContext;
import software.amazon.ec2.transitgatewayprefixlistreference.ResourceModel;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.ExceptionMapper;

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
        GetTransitGatewayPrefixListReferencesRequest request = this.translateModelToRequest(model);
        GetTransitGatewayPrefixListReferencesResponse response = this.proxy.injectCredentialsAndInvokeV2(request, this.client.client()::getTransitGatewayPrefixListReferences);
        return this.translateResponsesToModel(response, model);
    }

    private GetTransitGatewayPrefixListReferencesRequest translateModelToRequest(ResourceModel model) {
        return GetTransitGatewayPrefixListReferencesRequest.builder()
                   .transitGatewayRouteTableId(model.getTransitGatewayRouteTableId())
            .build();
    }

    private GetTransitGatewayPrefixListReferencesResponse makeServiceCall(GetTransitGatewayPrefixListReferencesRequest awsRequest, ProxyClient<Ec2Client> client) {
        return this.proxy.injectCredentialsAndInvokeV2(awsRequest, client.client()::getTransitGatewayPrefixListReferences);
    }

    private ResourceModel translateResponsesToModel(GetTransitGatewayPrefixListReferencesResponse awsResponse, ResourceModel model) {
        if(awsResponse.transitGatewayPrefixListReferences().isEmpty()) {
            return null;
        } else {
            TransitGatewayPrefixListReference response = awsResponse.transitGatewayPrefixListReferences().get(0);
            return ResourceModel.builder()
                .transitGatewayRouteTableId(response.transitGatewayRouteTableId())
                .prefixListId(response.prefixListId())
                .prefixListOwnerId(response.prefixListOwnerId())
                .state(response.state().toString())
                .blackhole(response.blackhole())
                .transitGatewayAttachmentId(response.transitGatewayAttachment().transitGatewayAttachmentId())
                .build();
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext>  handleError(GetTransitGatewayPrefixListReferencesRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if(exception instanceof ArrayIndexOutOfBoundsException) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }

    private  ProgressEvent<ResourceModel, CallbackContext> done(GetTransitGatewayPrefixListReferencesResponse response) {
        ResourceModel model = this.translateResponsesToModel(response, this.progress.getResourceModel());
        if(model == null || model.getState().equals(TransitGatewayPrefixListReferenceState.DELETING.toString())) {
            AwsServiceException emptyResponseException = AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorCode("NotFound").errorMessage("Not Found").build()).build();
            return ProgressEvent.defaultFailureHandler(emptyResponseException, HandlerErrorCode.NotFound);
        } else {
            return ProgressEvent.defaultSuccessHandler(this.translateResponsesToModel(response, this.progress.getResourceModel()));
        }
    }
}
