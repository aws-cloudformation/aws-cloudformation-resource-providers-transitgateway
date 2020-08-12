package software.amazon.ec2.transitgatewaymulticastdomain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayMulticastDomainResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsResponse;
import software.amazon.cloudformation.proxy.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static software.amazon.cloudformation.proxy.HandlerErrorCode.InternalFailure;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.TIMED_OUT_MESSAGE;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends TestBase {
    private DeleteHandler handler;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        handler = new DeleteHandler();
        model = buildResourceModel();
    }

    @Test
    public void handleRequest_DeletionInitiated() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .transitGatewayMulticastDomains(buildAvailableTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final DeleteTransitGatewayMulticastDomainResponse deleteTransitGatewayMulticastDomainResponse = DeleteTransitGatewayMulticastDomainResponse.builder()
                .transitGatewayMulticastDomain(buildTransitGatewayMulticastDomain())
                .build();
        doReturn(deleteTransitGatewayMulticastDomainResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteTransitGatewayMulticastDomainRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionFinalSucceed() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_DeletionInProgress() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .transitGatewayMulticastDomains(buildDeletingTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
    }

    @Test
    public void handleRequest_DeletionSuccessForDeletedState() {
        // if Multicast Domain is in DELETED state, handler returns a SUCCESS status because DELETED is a terminated state
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .transitGatewayMulticastDomains(buildDeletedTransitGatewayMulticastDomain())
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(1).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
    }

    @Test
    public void handleRequest_ResourceNotFound_FirstTimeInvoke() {
        final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = DescribeTransitGatewayMulticastDomainsResponse.builder()
                .build();
        doReturn(describeTransitGatewayMulticastDomainsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayMulticastDomainsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_CreationCallBackExceededMaximumCount() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, CallbackContext.builder().actionStarted(true).remainingRetryCount(0).build(), logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).isEqualTo(TIMED_OUT_MESSAGE);
        assertThat(response.getErrorCode()).isEqualTo(InternalFailure);
    }
}
