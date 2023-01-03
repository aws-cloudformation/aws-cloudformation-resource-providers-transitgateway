package com.aws.ec2.transitgatewayattachment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayVpcAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void handleRequest_SimpleSuccess() {

        final List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.tag());
        ResourceModel model = MOCKS.model(newTags);

        when(proxyClient.client().createTransitGatewayVpcAttachment(any(CreateTransitGatewayVpcAttachmentRequest.class))).thenReturn(MOCKS.createResponse(newTags));
        when(proxyClient.client().describeTransitGatewayVpcAttachments(any(DescribeTransitGatewayVpcAttachmentsRequest.class))).thenReturn(MOCKS.emptyReadResponse()).thenReturn(MOCKS.describeResponse(newTags));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        System.out.println(response.getResourceModel());
        System.out.println(model);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_SuccessForPendingAcceptance() {
        final List<Tag> newTags = new ArrayList<>();
        final String state = "pendingAcceptance";
        ResourceModel model = MOCKS.model(newTags, state);

        when(proxyClient.client().createTransitGatewayVpcAttachment(any(CreateTransitGatewayVpcAttachmentRequest.class))).thenReturn(MOCKS.createResponse(newTags, state));
        when(proxyClient.client().describeTransitGatewayVpcAttachments(any(DescribeTransitGatewayVpcAttachmentsRequest.class))).thenReturn(MOCKS.emptyReadResponse()).thenReturn(MOCKS.describeResponse(newTags, state));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_Duplicates() {
        when(proxyClient.client().describeTransitGatewayVpcAttachments(any(DescribeTransitGatewayVpcAttachmentsRequest.class))).thenReturn(MOCKS.describeResponse());

        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }
}
