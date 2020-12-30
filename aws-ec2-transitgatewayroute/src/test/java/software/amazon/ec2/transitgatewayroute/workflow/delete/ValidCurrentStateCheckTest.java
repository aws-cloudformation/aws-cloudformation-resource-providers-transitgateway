package software.amazon.ec2.transitgatewayroute.workflow.delete;

import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteState;
import software.amazon.ec2.transitgatewayroute.AbstractTestBase;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayRoutesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidCurrentStateCheckTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForAvailableState() {

        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.listResponse());
        ResourceModel model = MOCKS.model();
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void failedForDeletedCurrentState() {
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("state",  TransitGatewayRouteState.DELETED.toString());
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.listResponse( mockMap));
        ResourceModel model = MOCKS.model();
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
        assertThat(response.getMessage().contains("DELETE")).isTrue();
    }
}
