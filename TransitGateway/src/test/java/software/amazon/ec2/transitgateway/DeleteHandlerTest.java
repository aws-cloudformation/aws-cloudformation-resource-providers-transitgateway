package software.amazon.ec2.transitgateway;


import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static software.amazon.cloudformation.proxy.HandlerErrorCode.InternalFailure;
import static software.amazon.ec2.transitgateway.Utils.TIMED_OUT_MESSAGE;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    private DeleteHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new DeleteHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_DeletionInitiated() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildAvailableTransitGateway())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final DeleteTransitGatewayResponse deleteTransitGatewayResponse = DeleteTransitGatewayResponse.builder()
                .transitGateway(buildTransitGateway())
                .build();
        doReturn(deleteTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionFinalSucceed() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildDeletedTransitGateway())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_DeletionInProgress() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildDeletingTransitGateway())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionSuccessForDeletedState() {
        // if transit gateway is in DELETED state, handler returns a SUCCESS status because DELETED is a terminated state
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildDeletedTransitGateway())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayID.NotFound").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }


}
