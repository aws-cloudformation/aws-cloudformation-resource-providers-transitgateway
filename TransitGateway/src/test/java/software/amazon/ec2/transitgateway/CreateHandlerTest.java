package software.amazon.ec2.transitgateway;

import java.time.Duration;


import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.ec2.transitgateway.Utils.MAX_CALLBACK_COUNT;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {



    private CreateHandler handler;
    private ReadHandler readHandler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        model = buildResourceModel();
        handler = new CreateHandler();

        readHandler = new ReadHandler();

    }

    @Test
    public void handleRequest_CreationPending() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildPendingTransitGateway()) // an Available state should be returned for the final success
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }


    @Test
    public void handleRequest_CreationFinalSucceed() {
        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = DescribeTransitGatewaysResponse.builder()
                .transitGateways(buildAvailableTransitGateway()) // an Available state should be returned for the final success
                .build();
        doReturn(describeTransitGatewaysResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewaysRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(MAX_CALLBACK_COUNT).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);

    }


   
    


}
