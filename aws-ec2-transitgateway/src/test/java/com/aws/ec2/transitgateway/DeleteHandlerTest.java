package com.aws.ec2.transitgateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {

    @Mock(lenient = true)
    private AmazonWebServicesClientProxy proxy;

    @Mock(lenient = true)
    private ProxyClient<Ec2Client> proxyClient;

    @Mock(lenient = true)
    Ec2Client sdkClient;

    private DeleteHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new DeleteHandler();

        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

//    @AfterEach
//    public void tear_down() {
//        verify(sdkClient, atLeastOnce()).serviceName();
//        verifyNoMoreInteractions(sdkClient);
//    }

    @Test
    public void handleRequest_SimpleSuccess() {

        when(proxyClient.client().deleteTransitGateway(any(DeleteTransitGatewayRequest.class))).thenReturn(MOCKS.deleteResponse());
        when(proxyClient.client().describeTransitGateways(any(DescribeTransitGatewaysRequest.class))).thenReturn(MOCKS.describeResponse()).thenReturn(MOCKS.describeResponse("deleted"));
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
    public void handleRequest_Failure() {

        ResourceModel model = MOCKS.model("deleted");
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.GeneralServiceException);
    }

    @Test
    public void handleRequest_FailurePending() {

        ResourceModel model = MOCKS.model("pending");
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.GeneralServiceException);
    }

}
