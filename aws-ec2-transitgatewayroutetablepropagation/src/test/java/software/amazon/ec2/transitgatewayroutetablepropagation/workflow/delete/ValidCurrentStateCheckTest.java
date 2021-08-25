package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.delete;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayRouteTablePropagationsRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.ec2.transitgatewayroutetablepropagation.AbstractTestBase;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

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
        verify(sdkClient, atLeastOnce()).getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForAvailableState() {
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class))).thenReturn(MOCKS.describeResponse());
        ResourceModel model = MOCKS.model();
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy, MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void failedForDisabledCurrentState() {
        when(proxyClient.client().getTransitGatewayRouteTablePropagations(any(GetTransitGatewayRouteTablePropagationsRequest.class)))
                .thenReturn(MOCKS.describeResponse( "disabled"));
        ResourceModel model = MOCKS.model();
        CallbackContext context =  new CallbackContext();

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidCurrentStateCheck(proxy, MOCKS.request(model), context, proxyClient, logger)
                .run(ProgressEvent.defaultInProgressHandler(context, 0, model));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
