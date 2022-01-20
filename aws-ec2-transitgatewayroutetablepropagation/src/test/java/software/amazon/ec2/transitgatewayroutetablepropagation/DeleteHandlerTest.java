package software.amazon.ec2.transitgatewayroutetablepropagation;

import java.time.Duration;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DisableTransitGatewayRouteTablePropagationRequest;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayRouteTablePropagationsRequest;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

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
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        when(proxyClient.client().disableTransitGatewayRouteTablePropagation(any(DisableTransitGatewayRouteTablePropagationRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class)))
                .thenReturn(MOCKS.describeResponse()).thenReturn(MOCKS.describeResponse()).thenReturn(MOCKS.describeResponse("disassociated")).thenReturn(MOCKS.emptyReadResponse());
        ResourceModel model = MOCKS.model();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_Error() {
        AwsErrorDetails errorDetails = AwsErrorDetails.builder().errorMessage("Something went wrong").errorCode("Invalid Request").build();
        AwsServiceException exception = AwsServiceException.builder().awsErrorDetails(errorDetails).build();
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class))).thenReturn(MOCKS.describeResponse());
        when(proxyClient.client().disableTransitGatewayRouteTablePropagation(any(DisableTransitGatewayRouteTablePropagationRequest.class))).thenThrow(exception);
        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
        verify(sdkClient, atLeastOnce()).serviceName();
    }

    @Test
    public void handleRequest_NotFound() {
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class)))
                .thenReturn(MOCKS.describeResponse("disabled"));
        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void failedForNonExistResource() {
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class)))
                .thenReturn(MOCKS.emptyReadResponse());
        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
