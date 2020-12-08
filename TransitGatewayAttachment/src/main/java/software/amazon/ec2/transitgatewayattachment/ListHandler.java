package software.amazon.ec2.transitgatewayattachment;

import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayVpcAttachmentsResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayVpcAttachment;
import software.amazon.awssdk.services.ec2.transform.DescribeTransitGatewayVpcAttachmentsRequestMarshaller;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final List<ResourceModel> models = new ArrayList<>();
        final Ec2Client client = ClientBuilder.getClient();
        String nextToken = request.getNextToken();

        // List the TransitGatewayVpcAttachments
        try {
            final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse = describeTransitGatewayVpcAttachments(client, nextToken, proxy);
            nextToken = describeTransitGatewayVpcAttachmentsResponse.nextToken();

            for (final TransitGatewayVpcAttachment transitGatewayVpcAttachment : describeTransitGatewayVpcAttachmentsResponse.transitGatewayVpcAttachments()) {
                models.add(Translator.transformTransitGatewayVpcAttachments(transitGatewayVpcAttachment));
            }
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s list succeeded", ResourceModel.TYPE_NAME));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(models)
                .nextToken(nextToken)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachments(final Ec2Client client,
                                                                    final String nextToken,
                                                                    final AmazonWebServicesClientProxy proxy) {
        final DescribeTransitGatewayVpcAttachmentsRequest describeTransitGatewaysRequest = DescribeTransitGatewayVpcAttachmentsRequest.builder()
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewaysRequest, client::describeTransitGatewayVpcAttachments);
    }

}
