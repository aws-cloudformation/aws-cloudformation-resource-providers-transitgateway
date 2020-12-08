package software.amazon.ec2.transitgatewayattachment;

import java.time.Duration;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayVpcAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayVpcAttachmentResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsResponse;
import software.amazon.awssdk.services.ec2.transform.DescribeTransitGatewayVpcAttachmentsRequestMarshaller;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewaysResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildAvailableTransitGatewayVpcAttachment())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final DeleteTransitGatewayVpcAttachmentResponse deleteTransitGatewayResponse = DeleteTransitGatewayVpcAttachmentResponse.builder()
                .transitGatewayVpcAttachment(buildTransitGatewayVpcAttachment())
                .build();
        doReturn(deleteTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteTransitGatewayVpcAttachmentRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionFinalSucceed() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewaysResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_DeletionInProgress() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewaysResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildDeletingTransitGatewayVpcAttachment())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
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
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewaysResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildDeletedTransitGatewayVpcAttachment())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
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
                .injectCredentialsAndInvokeV2(any(DeleteTransitGatewayVpcAttachmentRequest.class), any());
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
