package software.amazon.ec2.transitgatewaymulticastgroupsource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.RegisterTransitGatewayMulticastGroupSourcesRequest;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayMulticastGroupsRequest;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    private CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new CreateHandler();
        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccessGroupSource() {
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("groupMember", "false");
        mockMap.put("groupSource", "true");
        ResourceModel model = MOCKS.model(mockMap);

        when(proxyClient.client().registerTransitGatewayMulticastGroupSources(any(RegisterTransitGatewayMulticastGroupSourcesRequest.class))).thenReturn(MOCKS.createResponse(mockMap));
        when(proxyClient.client().searchTransitGatewayMulticastGroups(any(SearchTransitGatewayMulticastGroupsRequest.class))).thenReturn(MOCKS.emptyReadResponse()).thenReturn(MOCKS.readResponse(mockMap));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        verify(sdkClient, atLeastOnce()).serviceName();
    }


    @Test
    public void handleRequest_Duplicates() {
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("groupMember", "false");
        mockMap.put("groupSource", "true");

        when(proxyClient.client().searchTransitGatewayMulticastGroups(any(SearchTransitGatewayMulticastGroupsRequest.class))).thenReturn(MOCKS.readResponse(mockMap));

        ResourceModel model = MOCKS.model(mockMap);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("be modified by ACTION: CREATE.")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }

    @Test
    public void handleRequest_InvalidPrimaryIdentifier() {
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("groupMember", "false");
        mockMap.put("groupSource", "true");
        mockMap.put("groupIpAddress", "224.0.0");

        ResourceModel model = MOCKS.model(mockMap);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("be modified by ACTION: CREATE.")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_ErrorGroupSource() {
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("groupMember", "false");
        mockMap.put("groupSource", "true");
        AwsErrorDetails errorDetails = AwsErrorDetails.builder().errorMessage("Something went wrong").errorCode("Invalid Request").build();
        AwsServiceException exception = AwsServiceException.builder().awsErrorDetails(errorDetails).build();
        when(proxyClient.client().registerTransitGatewayMulticastGroupSources(any(RegisterTransitGatewayMulticastGroupSourcesRequest.class))).thenThrow(exception);
        when(proxyClient.client().searchTransitGatewayMulticastGroups(any(SearchTransitGatewayMulticastGroupsRequest.class))).thenReturn(MOCKS.emptyReadResponse());
        ResourceModel model = MOCKS.model(mockMap);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("Something went wrong")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
        verify(sdkClient, atLeastOnce()).serviceName();

    }

}
