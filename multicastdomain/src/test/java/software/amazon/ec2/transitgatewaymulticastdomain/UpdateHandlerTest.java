package software.amazon.ec2.transitgatewaymulticastdomain;


import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainRequest;
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
public class UpdateHandlerTest extends TestBase {

    private UpdateHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new UpdateHandler();
        model = buildResourceModel();
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

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
    }

    @Test
    public void handleRequest_SimpleSuccessWhenPreviousTagsExist() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder().transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomainWithTags())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
    }

    @Test
    public void handleRequest_SimpleSuccessWhenPreviousTagsNeedToBeDeleted() {
        model = buildResourceModelWithDifferentTags();
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder().transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomainWithTags())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());

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
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder().transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(buildResourceModel())
                .previousResourceState(buildResourceModel())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().updateFailed(true).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }


    private void handleError(String errorThrown, HandlerErrorCode expectedErrorResponse) {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder().transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());


        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode(errorThrown).build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CreateTagsRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(expectedErrorResponse);
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
}
