package software.amazon.ec2.transitgatewayattachment;

import java.time.Duration;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static software.amazon.ec2.transitgatewayattachment.Translator.MAX_CALLBACK_COUNT;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {



    private CreateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        model = buildResourceModel();
        handler = new CreateHandler();
    }

    @Test
    public void handleRequest_CreationPending() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildPendingTransitGatewayVpcAttachment()) // an Available state should be returned for the final success
                .build();
        doReturn(describeTransitGatewayVpcAttachmentsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }


    @Test
    public void handleRequest_CreationFinalSucceed() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildAvailableTransitGatewayVpcAttachment()) // an Available state should be returned for the final success
                .build();
        doReturn(describeTransitGatewayVpcAttachmentsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }


    @Test
    public void handleRequest_InvalidIdNotFound() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayAttachmentID.NotFound").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_InvalidIdMalformed() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayAttachmentID.Malformed").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_LimitedExceeded() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("TransitGatewayLimitExceeded").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_InvalidParam() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidParameterValue").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
    @Test
    public void handleRequest_IncorrectState() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("IncorrectState").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.GeneralServiceException);
    }
    @Test
    public void handleRequest_MissingParam() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("MissingParameter").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
    @Test
    public void handleRequest_ServerInternalException() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("ServerInternal").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InternalFailure);
    }
    @Test
    public void handleRequest_ServiceUnavailable() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("ServiceUnavailable").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }
    @Test
    public void handleRequest_TagPolicyViolation() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("TagPolicyViolation").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayVpcAttachmentRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }
}
