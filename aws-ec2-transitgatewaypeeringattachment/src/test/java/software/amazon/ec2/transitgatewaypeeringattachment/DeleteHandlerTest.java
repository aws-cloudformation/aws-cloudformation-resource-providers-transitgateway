package software.amazon.ec2.transitgatewaypeeringattachment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayPeeringAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayPeeringAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayPeeringAttachmentsResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    private DeleteHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new DeleteHandler();

        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {

        when(proxyClient.client().deleteTransitGatewayPeeringAttachment(any(DeleteTransitGatewayPeeringAttachmentRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().describeTransitGatewayPeeringAttachments(any(DescribeTransitGatewayPeeringAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse()).thenReturn(MOCKS.describeResponse("deleted"));
        ResourceModel model = MOCKS.model();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Error() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());
        AwsErrorDetails errorDetails = AwsErrorDetails.builder().errorMessage("Something went wrong").errorCode("Invalid Request").build();
        AwsServiceException exception = AwsServiceException.builder().awsErrorDetails(errorDetails).build();
        when(proxyClient.client().describeTransitGatewayPeeringAttachments(any(DescribeTransitGatewayPeeringAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse());
        when(proxyClient.client().deleteTransitGatewayPeeringAttachment(any(DeleteTransitGatewayPeeringAttachmentRequest.class))).thenThrow(exception);

        ResourceModel model = MOCKS.model(tags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_NotFound() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());
        AwsErrorDetails errorDetails = AwsErrorDetails.builder().errorMessage("Something went wrong").errorCode("NotFound").build();
        AwsServiceException exception = AwsServiceException.builder().awsErrorDetails(errorDetails).build();
        when(proxyClient.client().deleteTransitGatewayPeeringAttachment(any(DeleteTransitGatewayPeeringAttachmentRequest.class))).thenThrow(exception);
        when(proxyClient.client().describeTransitGatewayPeeringAttachments(any(DescribeTransitGatewayPeeringAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse()).thenReturn(MOCKS.describeResponse("deleted"));

        ResourceModel model = MOCKS.model(tags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_AlreadyDeletedSuccess() {

        when(proxyClient.client().deleteTransitGatewayPeeringAttachment(any(DeleteTransitGatewayPeeringAttachmentRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().describeTransitGatewayPeeringAttachments(any(DescribeTransitGatewayPeeringAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse("deleted"));
        ResourceModel model = MOCKS.model();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_AlreadyFailedSuccess() {

        when(proxyClient.client().deleteTransitGatewayPeeringAttachment(any(DeleteTransitGatewayPeeringAttachmentRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().describeTransitGatewayPeeringAttachments(any(DescribeTransitGatewayPeeringAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse("failed"));
        ResourceModel model = MOCKS.model();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
