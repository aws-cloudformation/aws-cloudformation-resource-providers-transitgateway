package software.amazon.ec2.transitgatewayroutetable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;

    @Mock
    Ec2Client sdkClient;

    private UpdateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        handler = new UpdateHandler();

        sdkClient = mock(Ec2Client.class);
        proxyClient = MOCK_PROXY(proxy, sdkClient);
    }

    @AfterEach
    public void tear_down() {
        verify(sdkClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(sdkClient);
    }

    @Test
    public void handleRequest_CreateTagsSuccess() {

        final List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(MOCKS.describeResponse())
                .thenReturn(MOCKS.describeResponse())
                .thenReturn(MOCKS.describeResponse())
                .thenReturn(MOCKS.describeResponse(newTags));

        when(proxyClient.client().createTags(any(CreateTagsRequest.class))).thenReturn(MOCKS.createTagsResponse());

        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(newTags);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(model.getTags().get(0).getValue());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_DeleteTagsSuccess() {

        final List<Tag> newTags = new ArrayList<>();
        newTags.add(MOCKS.tag());

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(MOCKS.describeResponse(newTags)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(newTags)) //Get Current Create Tags
                .thenReturn(MOCKS.describeResponse(newTags)) //Get Current Delete Tags
                .thenReturn(MOCKS.describeResponse());//Get Final Tags

        when(proxyClient.client().deleteTags(any(DeleteTagsRequest.class))).thenReturn(MOCKS.deleteTagsResponse());

        ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags()).isEmpty();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ChangeTagsSuccess() {

        final List<Tag> tags1 = new ArrayList<>();
        tags1.add(MOCKS.tag("Name", "NAME_1"));

        final List<Tag> tags2 = new ArrayList<>();
        tags2.add(MOCKS.tag("Name", "NAME_2"));


        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(MOCKS.describeResponse(tags1)) //PreCheckUpdate Read
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Current Create Tags
                .thenReturn(MOCKS.describeResponse(tags1)) //Get Current Delete Tags
                .thenReturn(MOCKS.describeResponse(tags2));//Get Final Tags

        when(proxyClient.client().createTags(any(CreateTagsRequest.class))).thenReturn(MOCKS.createTagsResponse());
        when(proxyClient.client().deleteTags(any(DeleteTagsRequest.class))).thenReturn(MOCKS.deleteTagsResponse());

        final ResourceModel model = MOCKS.modelWithoutCreateOnlyProperties(tags2);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, MOCKS.request(model), new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getPrimaryIdentifier().toString()).isEqualTo(model.getPrimaryIdentifier().toString());
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(model.getTags().get(0).getValue());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


}
