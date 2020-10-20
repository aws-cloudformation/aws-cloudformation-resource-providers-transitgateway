package software.amazon.ec2.transitgatewayattachment;

import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsResponse;
import software.amazon.cloudformation.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends AbstractTestBase{
    private ListHandler handler;


    @BeforeEach
    public void setup() {
        handler = new ListHandler();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse= DescribeTransitGatewayVpcAttachmentsResponse.builder()
                .transitGatewayVpcAttachments(buildTransitGatewayVpcAttachment())
                .build();
        doReturn(describeTransitGatewayVpcAttachmentsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .nextToken("nextToken")
                .desiredResourceState(ResourceModel.builder().build())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getTransitGatewayId()).isEqualTo(TRANSIT_GATEWAY_ID);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

    }

    /**
     * A List call MUST return an empty array if there are no resources found
     * Ref: https://quip-amazon.com/bDEqAaLSgP7g/Uluru-Handler-Interface-Contract
     */
    @Test
    public void handleRequest_ResourceNotFound() {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode("InvalidTransitGatewayAttachmentID.NotFound").build();

        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();

        doThrow(awsServiceException)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeTransitGatewayVpcAttachmentsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .nextToken("nextToken")
                .desiredResourceState(ResourceModel.builder().build())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }
}
