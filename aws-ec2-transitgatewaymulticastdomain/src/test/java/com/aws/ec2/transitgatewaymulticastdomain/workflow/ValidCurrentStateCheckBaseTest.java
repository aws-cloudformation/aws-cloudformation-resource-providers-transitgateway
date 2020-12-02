package com.aws.ec2.transitgatewaymulticastdomain.workflow;

import com.aws.ec2.transitgatewaymulticastdomain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TagSpecification;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidCurrentStateCheckBaseTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    public class InvalidDeleteState extends ValidCurrentStateCheckBase {

        public InvalidDeleteState(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
            super(proxy, request, callbackContext, client, logger);
        }

        @Override
        protected List<String> invalidStates() {
            List<String> list = new ArrayList<>();
            list.add("deleted");
            return list;
        }
    }

    public class ValidAvailableState extends ValidCurrentStateCheckBase {

        public ValidAvailableState(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
            super(proxy, request, callbackContext, client, logger);
        }

        @Override
        protected List<String> validStates() {
            List<String> list = new ArrayList<>();
            list.add("available");
            return list;
        }
    }

    public class EmptyState extends ValidCurrentStateCheckBase {

        public EmptyState(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
            super(proxy, request, callbackContext, client, logger);
        }
    }

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForInvalidDeleteState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new InvalidDeleteState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void failedForInvalidDeleteState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags, "deleted"));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new InvalidDeleteState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("WORKFLOW")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);

    }

    @Test
    public void validForEmptyState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new EmptyState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void validForValidAvailableState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void failedForValidAvailableState() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags, "deleting"));
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ResourceConflict);
    }

    @Test
    public void throwError() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenThrow((AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorMessage("NotFound").errorCode("NotFound").build())).build());
        ResourceModel model = MOCKS.model(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void secondCall() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());
        ResourceModel model = MOCKS.model(tags);

        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().describeTransitGatewayMulticastDomains(any(DescribeTransitGatewayMulticastDomainsRequest.class))).thenReturn(MOCKS.describeResponse(tags));

        ProgressEvent<ResourceModel, CallbackContext> response = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        context.setAttempts(2);
        ProgressEvent<ResourceModel, CallbackContext> response2 = new ValidAvailableState(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response2).isNotNull();
        assertThat(response2.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response2.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response2.getResourceModel()).isEqualTo(model);
        assertThat(response2.getResourceModels()).isNull();
        assertThat(response2.getMessage()).isNull();
        assertThat(response2.getErrorCode()).isNull();
    }

}
