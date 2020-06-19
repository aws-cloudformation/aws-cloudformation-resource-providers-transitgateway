package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsResponse;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends TestBase{

    private ReadHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new ReadHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .transitGatewayMulticastDomains(buildTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel().getTransitGatewayId()).isEqualTo(TRANSIT_GATEWAY_ID);
        assertThat(response.getResourceModel().getTransitGatewayMulticastDomainId()).isEqualTo(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayMulticastDomainIdNotFoundException").build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
