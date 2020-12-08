package software.amazon.ec2.transitgatewayattachment;

import java.time.Duration;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsResponse;
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
public class ReadHandlerTest extends AbstractTestBase {


    private ReadHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ReadHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewaysResponse = DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildTransitGatewayVpcAttachment())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getTransitGatewayId()).isEqualTo(TRANSIT_GATEWAY_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayAttachmentID.NotFound").build();

        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();
        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
