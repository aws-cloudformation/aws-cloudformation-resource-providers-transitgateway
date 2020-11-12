package software.amazon.ec2.transitgateway;


import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.utils.ImmutableMap;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static software.amazon.ec2.transitgateway.Utils.MAX_CALLBACK_COUNT;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    private UpdateHandler handler;
    private ResourceModel model;
    private ResourceModel previousModel;
    private ReadHandler readHandler;
    @BeforeEach
    public void setup() {
        handler = new UpdateHandler();
        readHandler = new ReadHandler();
        model = buildResourceModel();
        previousModel = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {

        // The expected responses
        ModifyTransitGatewayResponse modifyTransitGatewayResponse = ModifyTransitGatewayResponse.builder()
                .transitGateway(buildAvailableTransitGateway())// an Available state should be returned for the final success
                .build();

        doReturn(modifyTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ModifyTransitGatewayRequest.class), any());

        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildTransitGateway())
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = readHandler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getTransitGatewayId()).isEqualTo(TRANSIT_GATEWAY_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }



    @Test
    public void handleRequest_testUpdateFailureCallBack() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildAvailableTransitGateway())
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(buildResourceModel())
                .previousResourceState(buildResourceModel())
                .build();
        final ModifyTransitGatewayResponse modifyTransitGatewayResponse = ModifyTransitGatewayResponse.builder()
                .transitGateway(buildTransitGateway())
                .build();

        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        doReturn(modifyTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ModifyTransitGatewayRequest.class), any());


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().updateFailed(true).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
    }

}
