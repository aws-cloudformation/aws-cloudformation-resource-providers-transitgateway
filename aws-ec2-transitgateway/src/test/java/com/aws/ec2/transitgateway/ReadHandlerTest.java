package com.aws.ec2.transitgateway;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    private ReadHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new ReadHandler();
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
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGateways(any(DescribeTransitGatewaysRequest.class))).thenReturn(MOCKS.describeResponse(tags));
        ResourceModel model = MOCKS.model(tags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(MOCKS.model(tags));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


    @Test
    public void handleRequest_Failure() {


        when(proxyClient.client().describeTransitGateways(any(DescribeTransitGatewaysRequest.class))).thenReturn(MOCKS.describeResponse("deleted"));
        ResourceModel model = MOCKS.model("deleted");

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
