package software.amazon.ec2.transitgatewaymulticastdomainassociation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AssociateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.DisassociateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayMulticastDomainAssociationsRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

//    @AfterEach
//    public void tear_down() {
//        verify(sdkClient, atLeastOnce()).serviceName();
//        verifyNoMoreInteractions(sdkClient);
//    }

    @Test
    public void handleRequest_SimpleSuccess() {

        ResourceModel model = MOCKS.model();

        when(proxyClient.client().associateTransitGatewayMulticastDomain(any(AssociateTransitGatewayMulticastDomainRequest.class))).thenReturn(MOCKS.createResponse());
        when(proxyClient.client().getTransitGatewayMulticastDomainAssociations(any(GetTransitGatewayMulticastDomainAssociationsRequest.class))).thenReturn(MOCKS.emptyReadResponse()).thenReturn(MOCKS.describeResponse());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }



    @Test
    public void handleRequest_Duplicates() {
        when(proxyClient.client().getTransitGatewayMulticastDomainAssociations(any(GetTransitGatewayMulticastDomainAssociationsRequest.class))).thenReturn(MOCKS.describeResponse());

        ResourceModel model = MOCKS.model();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(model);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("be modified by ACTION: CREATE.")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.AlreadyExists);
    }

}
