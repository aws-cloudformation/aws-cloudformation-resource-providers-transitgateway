package software.amazon.ec2.transitgatewayroute.workflow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.SearchTransitGatewayRoutesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteState;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewayroute.AbstractTestBase;
import software.amazon.ec2.transitgatewayroute.CallbackContext;
import software.amazon.ec2.transitgatewayroute.ResourceModel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
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
            list.add(TransitGatewayRouteState.DELETED.toString());
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
            list.add(TransitGatewayRouteState.ACTIVE.toString());
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
        verify(sdkClient, atLeastOnce()).searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class));
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void validForInvalidDeleteState() {
        ResourceModel model = MOCKS.model();
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse());
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
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("state",  TransitGatewayRouteState.DELETED.toString());
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse(mockMap));
        ResourceModel model = MOCKS.model(mockMap);
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

        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse());
        ResourceModel model = MOCKS.model();
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
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse());
        ResourceModel model = MOCKS.model();
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
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("state",  TransitGatewayRouteState.DELETING.toString());
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse(mockMap));
        ResourceModel model = MOCKS.model();
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

        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenThrow((AwsServiceException.builder().awsErrorDetails(AwsErrorDetails.builder().errorMessage("NotFound").errorCode("NotFound").build())).build());
        ResourceModel model = MOCKS.model();
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
        ResourceModel model = MOCKS.model();

        CallbackContext context =  new CallbackContext();
        when(proxyClient.client().searchTransitGatewayRoutes(any(SearchTransitGatewayRoutesRequest.class))).thenReturn(MOCKS.readResponse());

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
