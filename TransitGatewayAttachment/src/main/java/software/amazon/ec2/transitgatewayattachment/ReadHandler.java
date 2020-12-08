package software.amazon.ec2.transitgatewayattachment;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {
        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final ResourceModel readResult;

        // Describe TransitGateway
        try {

            final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse = Translator.describeTransitGatewayVpcAttachments(client, model, proxy);

            final TransitGatewayVpcAttachment transitGatewayVpcAttachment = describeTransitGatewayVpcAttachmentsResponse.transitGatewayVpcAttachments().get(0);
            readResult = Translator.transformTransitGatewayVpcAttachments(transitGatewayVpcAttachment);

        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s read succeeded", ResourceModel.TYPE_NAME));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(OperationStatus.SUCCESS)
                .build();
    }



}
