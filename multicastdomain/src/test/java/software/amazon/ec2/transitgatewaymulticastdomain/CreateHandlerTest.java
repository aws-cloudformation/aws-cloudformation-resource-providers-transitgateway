package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends TestBase {

    private CreateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        model = buildResourceModel();
        handler = new CreateHandler();
    }

    private void handleError(String errorThrown, HandlerErrorCode expectedErrorResponse) {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode(errorThrown).build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTransitGatewayMulticastDomainRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(expectedErrorResponse);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder().transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        CallbackContext context = CallbackContext.builder().actionStarted(true).remainingRetryCount(1).transitGatewayMulticastDomainId(TRANSIT_GATEWAY_MULTICAST_DOMAIN_ID).build();
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_InvalidIdNotFound() {
        this.handleError("InvalidTransitGatewayID.NotFound", HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_InvalidIdMalformed() {
        this.handleError("InvalidTransitGatewayID.Malformed", HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_LimitedExceeded() {
        this.handleError("TransitGatewayMulticastDomainLimitExceeded", HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_InvalidParam() {
        this.handleError("InvalidParameterValue", HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_IncorrectState() {
        this.handleError("IncorrectState", HandlerErrorCode.GeneralServiceException);
    }

    @Test
    public void handleRequest_MissingParam() {
        this.handleError("MissingParameter", HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_ServerInternalException() {
        this.handleError("ServerInternal", HandlerErrorCode.InternalFailure);
    }

    @Test
    public void handleRequest_ServiceUnavailable() {
        this.handleError("ServiceUnavailable", HandlerErrorCode.ServiceInternalError);
    }

    @Test
    public void handleRequest_TagPolicyViolation() {
        this.handleError("TagPolicyViolation", HandlerErrorCode.InvalidRequest);
    }
}
