package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.ImmutableList;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest  extends AbstractTestBase{

    @Mock
    private AmazonWebServicesClientProxy proxy;


    @Mock
    private ProxyClient<Ec2Client> proxyClient;
    private DeleteHandler handler;

    @Mock
    Ec2Client ec2Client;


    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(5).toMillis());
        System.setProperty("aws.region", "us-east-1");
        handler = new DeleteHandler();
        ec2Client = mock(ec2Client.getClass());
        proxyClient = MOCK_PROXY(proxy, ec2Client);
    }

    public void tear_down() {
        verify(ec2Client, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(ec2Client);
    }


    @Test
    public void handleRequest_SimpleSuccess() {
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rtb-0f636567a482d2f9f";

        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesDeleted = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.DELETED).build())
                .build();

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder()
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesDeleted);

        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }

    @Test
    public void handleRequest_PendingCondition() {
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rtb-0ff2c1407ba595fb9";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.PENDING).build())
                .build();


        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(5);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_AvailableCondition() {
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rtb-0ff2c1407ba595fb9";

        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();


        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(5);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ErrorCondition() {
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rtb-0ff2c1407ba595fb9";

        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();


        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();

        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.INCORRECT_STATE).build())
                .build();
        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenThrow(exception);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
    }


    @Test
    public void handleRequest_NoId()
    {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        final ResourceModel model = ResourceModel.builder().build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder().desiredResourceState(model).build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode().toString()).isEqualTo(BaseHandlerStd.INVALID_REQUEST);

    }


    @Test
    public void handleRequest_Error() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesDeleted = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().build();
        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRouteTableID.NotFound").build())
                .build();

        AwsServiceException exception1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRoute.NotFound").build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesDeleted)
                .thenThrow(exception)
                .thenThrow(exception1);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_Exception() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().build();
        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.TRANSIT_GATEWAY_LIMIT_EXCEEDED).build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesAvailable)
                .thenThrow(exception);
        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse)
                .thenThrow(exception);
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();

    }

    @Test
    public void handleRequest_ExceptionStabilize() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();
        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRoute.NotFound").build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesAvailable)
                .thenThrow(exception_1);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
    }

    @Test
    public void handleRequest_ExceptionStabilizeInvalidRoute() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();
        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND).build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesAvailable)
                .thenThrow(exception_1);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


    @Test
    public void handleRequest_ExceptionStabilizeOtherException() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesAvailable = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();
        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.INVALID_REQUEST).build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesAvailable)
                .thenThrow(exception_1);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
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
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
    }



    @Test
    public void handleRequest_disassociateRouteTable() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();


        AwsServiceException exception = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidAssociation.NotFound").build())
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidRoute.NotFound").build())
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("InvalidTransitGatewayAttachmentID.NotFound").build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending)
                .thenThrow(exception);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNotNull();
        assertThat(response.getErrorCode()).isNotNull();
    }


    @Test
    public void handleRequest_disassociateRouteTableAssociated() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).

                build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.DELETED).build())
                .build();

        List<TransitGatewayRouteTableAssociation> test = ImmutableList.of(
                TransitGatewayRouteTableAssociation.builder()
                        .transitGatewayAttachmentId("TransitGatewayRoutTableAttachment1")
                        .state("associated")
                        .build(),
                TransitGatewayRouteTableAssociation.builder()
                        .transitGatewayAttachmentId(null)
                        .state("disassociated")
                        .build());

        GetTransitGatewayRouteTableAssociationsResponse routeTableAssociationsResponse =
                GetTransitGatewayRouteTableAssociationsResponse.builder().associations(test).build();
        GetTransitGatewayRouteTableAssociationsResponse associationsResponse = GetTransitGatewayRouteTableAssociationsResponse
                .builder()
                .associations(TransitGatewayRouteTableAssociation.builder().transitGatewayAttachmentId("1").build())
                .associations(TransitGatewayRouteTableAssociation.builder().state("associated").build()).build();

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().
                build();

        DisassociateTransitGatewayRouteTableResponse disassociateTransitGatewayRouteTableResponse =
                DisassociateTransitGatewayRouteTableResponse.builder().build();

        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND).build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        Mockito.lenient().when(proxyClient.client().getTransitGatewayRouteTableAssociations(
                        any(GetTransitGatewayRouteTableAssociationsRequest.class))).
                thenReturn(routeTableAssociationsResponse);


        Mockito.lenient().when(proxyClient.client().disassociateTransitGatewayRouteTable(any(DisassociateTransitGatewayRouteTableRequest.class)))
                .thenReturn(disassociateTransitGatewayRouteTableResponse);


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_disassociateRouteTableFalse() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).

                build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.DELETED).build())
                .build();

        List<TransitGatewayRouteTableAssociation> test = null;
        GetTransitGatewayRouteTableAssociationsResponse routeTableAssociationsResponse = null;
        GetTransitGatewayRouteTableAssociationsResponse associationsResponse = null;
        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().
                build();
        DisassociateTransitGatewayRouteTableResponse disassociateTransitGatewayRouteTableResponse =
                DisassociateTransitGatewayRouteTableResponse.builder().build();
        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("Test").build())
                .build();
        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);
        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


    @Test
    public void handleRequest_disassociateRouteTableNull() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).

                build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.DELETED).build())
                .build();

        List<TransitGatewayRouteTableAssociation> test = null;

        GetTransitGatewayRouteTableAssociationsResponse routeTableAssociationsResponse =
                GetTransitGatewayRouteTableAssociationsResponse.builder().associations(test).build();
        GetTransitGatewayRouteTableAssociationsResponse associationsResponse = GetTransitGatewayRouteTableAssociationsResponse
                .builder()
                .associations(TransitGatewayRouteTableAssociation.builder().transitGatewayAttachmentId("1").build())
                .associations(TransitGatewayRouteTableAssociation.builder().state("associated").build()).build();

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().
                build();

        DisassociateTransitGatewayRouteTableResponse disassociateTransitGatewayRouteTableResponse =
                DisassociateTransitGatewayRouteTableResponse.builder().build();

        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("Test").build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        Mockito.lenient().when(proxyClient.client().getTransitGatewayRouteTableAssociations(
                        any(GetTransitGatewayRouteTableAssociationsRequest.class))).
                thenReturn(routeTableAssociationsResponse);


        Mockito.lenient().when(proxyClient.client().disassociateTransitGatewayRouteTable(any(DisassociateTransitGatewayRouteTableRequest.class)))
                .thenReturn(disassociateTransitGatewayRouteTableResponse);


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_disassociateRouteTableException() {
        setup();
        final DeleteHandler handler = new DeleteHandler();
        String transitGatewayRouteTableId = "tgw-rt-123";
        final ResourceModel model = ResourceModel.builder().transitGatewayRouteTableId(transitGatewayRouteTableId).

                build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesPending = DescribeTransitGatewayRouteTablesResponse.builder()
                .transitGatewayRouteTables(TransitGatewayRouteTable.builder().state(TransitGatewayRouteTableState.AVAILABLE).build())
                .build();


        GetTransitGatewayRouteTableAssociationsResponse routeTableAssociationsResponse = null;

        GetTransitGatewayRouteTableAssociationsResponse associationsResponse = null;

        DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTableResponse = DeleteTransitGatewayRouteTableResponse.builder().
                build();

        DisassociateTransitGatewayRouteTableResponse disassociateTransitGatewayRouteTableResponse =
                DisassociateTransitGatewayRouteTableResponse.builder().build();

        AwsServiceException exception_1 = AwsServiceException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("test").build())
                .build();

        when(proxyClient.client().describeTransitGatewayRouteTables(any(DescribeTransitGatewayRouteTablesRequest.class)))
                .thenReturn(describeTransitGatewayRouteTablesPending);

        Mockito.lenient().when(proxyClient.client().deleteTransitGatewayRouteTable(any(DeleteTransitGatewayRouteTableRequest.class)))
                .thenReturn(deleteTransitGatewayRouteTableResponse);

        Mockito.lenient().when(proxyClient.client().getTransitGatewayRouteTableAssociations(
                        any(GetTransitGatewayRouteTableAssociationsRequest.class)))
                .thenThrow(exception_1);


        Mockito.lenient().when(proxyClient.client().disassociateTransitGatewayRouteTable(any(DisassociateTransitGatewayRouteTableRequest.class)))
                .thenThrow(exception_1);


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isNotNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(5);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }


}
