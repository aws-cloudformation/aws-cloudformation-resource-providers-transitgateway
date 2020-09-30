package software.amazon.ec2.transitgateway;


import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayRequest;
import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayResponse;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    private UpdateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new UpdateHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ModifyTransitGatewayResponse modifyTransitGatewayResponse = ModifyTransitGatewayResponse.builder()
                .transitGateway(buildTransitGateway())
                .build();
        doReturn(modifyTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ModifyTransitGatewayRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
    }


    @Test
    public void handleRequest_testUpdateFailureCallBack() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(buildResourceModel())
                .previousResourceState(buildResourceModel())
                .build();
        final ModifyTransitGatewayResponse modifyTransitGatewayResponse = ModifyTransitGatewayResponse.builder()
                .transitGateway(buildTransitGateway())
                .build();
        doReturn(modifyTransitGatewayResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(ModifyTransitGatewayRequest.class), any());


        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().updateFailed(true).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

}
