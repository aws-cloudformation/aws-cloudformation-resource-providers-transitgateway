package software.amazon.ec2.transitgatewayroutetable;
import static org.mockito.Mockito.any;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest  extends AbstractTestBase{
    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;
    @Mock
    Ec2Client ec2Client;
    private CreateHandler handler;


    @BeforeEach
    public void setup() {

        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());

        System.setProperty("aws.region", "us-east-1");
        handler = new CreateHandler();
        ec2Client = mock(ec2Client.getClass());
        proxyClient = MOCK_PROXY(proxy, ec2Client);
    }


    public void tear_down() {
        verify(ec2Client, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(ec2Client);
    }
    @Test
    public void handleRequest_SimpleSuccess() {

        String transitGatewayId = "tgw-123";

        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayId(transitGatewayId)
                .tags(Collections.emptyList())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        DescribeTransitGatewayRouteTablesResponse mockDescribePending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayId(transitGatewayId).state(TransitGatewayRouteTableState.PENDING).build())
                .build();


        DescribeTransitGatewayRouteTablesResponse mockDescribeAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayId(transitGatewayId).state(
                        TransitGatewayRouteTableState.AVAILABLE).build()).build();

        CreateTransitGatewayRouteTableResponse mockCreate = CreateTransitGatewayRouteTableResponse.builder()
                .transitGatewayRouteTable(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayId).build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(mockDescribePending)
                .thenReturn(mockDescribeAvailable);

        when(proxyClient.client().createTransitGatewayRouteTable(any(CreateTransitGatewayRouteTableRequest.class)))
                .thenReturn(mockCreate);


        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setPropagationDelay(true);
        callbackContext.setResourceModel(model);


        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();



        callbackContext.setPropagationDelay(false);
        final ProgressEvent<ResourceModel, CallbackContext>  response_false = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response_false).isNotNull();
        assertThat(response_false.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response_false.getCallbackDelaySeconds()).isEqualTo(0);
        tear_down();
    }


    @Test
    public void handleRequest_MissingType(){
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setPropagationDelay(true);
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
        assertThat(response.getMessage()).isEqualTo("Transit Gateway ID cannot be empty");
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        // tear_down();
    }
    @Test
    public void handleRequest_Throttle(){
        setup();
        String transitGatewayId = "tgw-123";
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayId(transitGatewayId)
                .tags(Collections.emptyList())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        CreateTransitGatewayRouteTableResponse mockCreate = CreateTransitGatewayRouteTableResponse.builder()
                .transitGatewayRouteTable(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayId).build())
                .build();



        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("RequestLimitExceeded").build())
                .build();


        DescribeTransitGatewayRouteTablesResponse mockDescribeAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayId(transitGatewayId).state(
                        TransitGatewayRouteTableState.AVAILABLE).build()).build();


        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenThrow(exception)
                .thenReturn(mockDescribeAvailable);

        when(proxyClient.client().createTransitGatewayRouteTable(any(CreateTransitGatewayRouteTableRequest.class)))
                .thenReturn(mockCreate);

        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setPropagationDelay(true);
        callbackContext.setResourceModel(model);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
        tear_down();
    }
    @Test
    public void handleRequest_Error(){
        setup();
        String transitGatewayId = "tgw-123";
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayId(transitGatewayId)
                .tags(Collections.emptyList())
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        CreateTransitGatewayRouteTableResponse mockCreate = CreateTransitGatewayRouteTableResponse.builder()
                .transitGatewayRouteTable(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayId).build())
                .build();



        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.INCORRECT_STATE).build())
                .build();


        DescribeTransitGatewayRouteTablesResponse mockDescribeAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayId(transitGatewayId).state(
                        TransitGatewayRouteTableState.AVAILABLE).build()).build();


        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenThrow(exception)
                .thenReturn(mockDescribeAvailable);

        when(proxyClient.client().createTransitGatewayRouteTable(any(CreateTransitGatewayRouteTableRequest.class)))
                .thenReturn(mockCreate);

        final CallbackContext callbackContext = new CallbackContext();
        callbackContext.setPropagationDelay(true);
        callbackContext.setResourceModel(model);

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, callbackContext, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
        tear_down();
    }

    @Test
    public void handle_Transit_Gateway_RouteTable_Id_Null() {

        final ResourceModel model = null;

        final CreateHandler handler = new CreateHandler();


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response.getErrorCode().toString()).isEqualTo(BaseHandlerStd.INVALID_REQUEST);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
    }
}
