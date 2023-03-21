package software.amazon.ec2.transitgatewayroutetable;

import jdk.nashorn.internal.codegen.CompilerConstants;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.cloudformation.model.ResourceChange;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase{

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;
    @Mock
    Ec2Client ec2Client;
    private ReadHandler handler;


    @BeforeEach
    public void setup() {
        //proxy = mock(AmazonWebServicesClientProxy.class);
        //logger = mock(Logger.class);
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());

        System.setProperty("aws.region", "us-east-1");
        handler = new ReadHandler();
        ec2Client = mock(ec2Client.getClass());
        proxyClient = MOCK_PROXY(proxy, ec2Client);
    }

    public void tear_down() {
        verify(ec2Client, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(ec2Client);
    }
    @Test
    public void handleRequest_SimpleSuccess() {
        String transitGatewayRouteTableId = "tgw-rtb-123";

        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayRouteTableId(transitGatewayRouteTableId)
                .tags(Collections.emptyList())
                .build();

        final DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).state(
                                TransitGatewayRouteTableState.AVAILABLE).build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class))).
                thenReturn(describeTransitGatewayRouteTablesResponse);



        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        tear_down();
    }


    @Test
    public void handle_Transit_Gateway_RouteTable_Id() {

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response.getErrorCode().toString()).isEqualTo(BaseHandlerStd.INVALID_REQUEST);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);

    }

    @Test
    public void handle_Transit_Gateway_RouteTable_Id_Null() {

        final ResourceModel model = null;

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response.getErrorCode().toString()).isEqualTo(BaseHandlerStd.INVALID_REQUEST);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);

    }

    @Test
    public void handleRequest_Error() {
        String transitGatewayRouteTableId = "tgw-rtb-123";

        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayRouteTableId(transitGatewayRouteTableId)
                .tags(Collections.emptyList())
                .build();

        final DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).state(
                        TransitGatewayRouteTableState.DELETED).build())
                .build();

        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRouteTableID.NotFound").build())
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRoute.NotFound").build())

                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
        .thenThrow(exception)
        .thenThrow(exception);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        try{
            final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        } catch (Exception e){
            assertThat(e instanceof CfnNotFoundException);
        }
        tear_down();
    }

    @Test
    public void handleRequest_TestError() {
        String transitGatewayRouteTableId = "tgw-rtb-123";

        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayRouteTableId(transitGatewayRouteTableId)
                .tags(Collections.emptyList())
                .build();

        final DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).state(
                        TransitGatewayRouteTableState.DELETED).build())
                .build();

        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("Test").build())
                .build();
        AwsServiceException exception1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRouteTableID.NotFound").build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenThrow(exception)
                .thenThrow(exception)
                .thenThrow(exception1);


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        try{
            final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        } catch (Exception e){
            assertThat(e instanceof CfnInvalidRequestException);
        }
        tear_down();
    }

    @Test
    public void handleRequest_TestErrorOrCondition() {
        String transitGatewayRouteTableId = "tgw-rtb-123";

        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .transitGatewayRouteTableId(transitGatewayRouteTableId)
                .tags(Collections.emptyList())
                .build();

        final DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).state(
                        TransitGatewayRouteTableState.DELETED).build())
                .build();

        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("TestError").build())
                .build();
        AwsServiceException exception1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRoute.NotFound").build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenThrow(exception1)
                .thenThrow(exception1)
                .thenThrow(exception1);


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        try{
            final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        } catch (Exception e){
            assertThat(e instanceof CfnInvalidRequestException);
        }
        tear_down();
    }
}
