package com.aws.ec2.transitgatewayattachment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    private ListHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new ListHandler();
        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).describeTransitGatewayVpcAttachments(any(DescribeTransitGatewayVpcAttachmentsRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {

        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        final ResourceModel expectedResult = ResourceModel.builder()
                .id(MOCKS.primaryIdentifier)
                .build();

        final List<ResourceModel> expectedResults = new ArrayList<>();
        expectedResults.add(expectedResult);

        when(proxyClient.client().describeTransitGatewayVpcAttachments(any(DescribeTransitGatewayVpcAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(MOCKS.model()), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isEqualTo(expectedResults);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

}
