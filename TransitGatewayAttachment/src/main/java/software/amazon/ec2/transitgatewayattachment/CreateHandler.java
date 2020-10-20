package software.amazon.ec2.transitgatewayattachment;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;
import static software.amazon.ec2.transitgatewayattachment.Translator.*;

import java.util.List;

import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;
import static software.amazon.ec2.transitgatewayattachment.Translator.CALlBACK_PERIOD_30_SECONDS;


public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final CreateTransitGatewayVpcAttachmentResponse createTransitGatewayVpcAttachmentResponse;
        int remainingRetryCount = MAX_CALLBACK_COUNT;

        if (callbackContext == null || !callbackContext.isActionStarted()) {
            try {
                createTransitGatewayVpcAttachmentResponse = createTransitGatewayVpcAttachment(client, model, proxy);

            } catch (final AwsServiceException e) {
                return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
            }
            model.setTransitGatewayAttachmentId(createTransitGatewayVpcAttachmentResponse.transitGatewayVpcAttachment().transitGatewayAttachmentId());
            model.setTransitGatewayId(createTransitGatewayVpcAttachmentResponse.transitGatewayVpcAttachment().transitGatewayId());
            model.setVpcId(createTransitGatewayVpcAttachmentResponse.transitGatewayVpcAttachment().vpcId());
            model.setSubnetIds(createTransitGatewayVpcAttachmentResponse.transitGatewayVpcAttachment().subnetIds());

        }

        final DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachmentsResponse = Translator.describeTransitGatewayVpcAttachments(client, model, proxy);

        try {
            if (describeTransitGatewayVpcAttachmentsResponse.transitGatewayVpcAttachments().isEmpty()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
            }

           final TransitGatewayAttachmentState stateCode = describeTransitGatewayVpcAttachmentsResponse.transitGatewayVpcAttachments().get(0).state();

            switch (stateCode) {
                case PENDING:
                    logger.log(String.format("creation pending"));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .status(IN_PROGRESS)
                            .resourceModel(model)
                            .build();
                case AVAILABLE:
                    logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(SUCCESS)
                            .build();
                case FAILED:
                    logger.log(String.format("creation failed"));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .build();
                default:
                    logger.log(String.format("creation default"));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .build();
            }
        }catch (Ec2Exception e) {
            e.printStackTrace();
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    .build();
        }

    }

    private CreateTransitGatewayVpcAttachmentResponse createTransitGatewayVpcAttachment(final Ec2Client client,
                                                              final ResourceModel model,
                                                              final AmazonWebServicesClientProxy proxy) {
        List<Tag> tags = model.getTags();
        List<software.amazon.awssdk.services.ec2.model.Tag> listTags = Translator.cfnTagsToSdkTags(tags);
        final CreateTransitGatewayVpcAttachmentRequest createTransitGatewayVpcAttachmentRequest =
                CreateTransitGatewayVpcAttachmentRequest.builder()
                        .transitGatewayId(model.getTransitGatewayId())
                .tagSpecifications(Translator.translateTagsToTagSpecifications(listTags))
                .vpcId(model.getVpcId())
                .subnetIds(model.getSubnetIds())
                .options(Translator.translateOptionsToTransitGatewayRequestOptions(model.getOptions())).build();

        return proxy.injectCredentialsAndInvokeV2(createTransitGatewayVpcAttachmentRequest, client::createTransitGatewayVpcAttachment);


    }

}
