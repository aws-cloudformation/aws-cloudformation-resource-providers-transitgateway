package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest  extends AbstractTestBase{

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;
    private ListHandler handler;

    @Mock
    Ec2Client ec2Client;


    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());

        System.setProperty("aws.region", "us-east-1");
        handler = new ListHandler();
        ec2Client = mock(ec2Client.getClass());
        proxyClient = MOCK_PROXY(proxy, ec2Client);
    }

    public void tear_down() {
        verify(ec2Client, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(ec2Client);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponseAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesResponseAvailable);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
