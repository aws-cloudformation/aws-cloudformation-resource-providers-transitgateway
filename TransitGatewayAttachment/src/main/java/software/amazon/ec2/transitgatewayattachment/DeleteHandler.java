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

import java.util.List;

import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;
import static software.amazon.ec2.transitgatewayattachment.Translator.*;

public class DeleteHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();

        final List<TransitGatewayVpcAttachment> transitGatewayVpcAttachments = Translator.describeTransitGatewayVpcAttachments(client, model, proxy).transitGatewayVpcAttachments();

        try {
            int remainingRetryCount = MAX_CALLBACK_COUNT;

            final TransitGatewayAttachmentState stateCode = transitGatewayVpcAttachments.get(0).state();
            switch (stateCode) {
                case AVAILABLE:
                    deleteTransitGatewayAttachment(client, model, proxy);
                    logger.log(String.format("%s [%s] deletion in progress", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .status(IN_PROGRESS)
                            .build();
                case PENDING:
                case DELETING:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS) //callback until TransitGateway is deleted
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .status(IN_PROGRESS)
                            .build();
                case DELETED: // return success because DELETED is a terminated state
                    logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .status(SUCCESS)
                            .build();
                default:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .build();
            }

        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        } catch (final CfnNotFoundException e) {
            //NotFound returned from Delete handler will be considered by CFN backend service as success
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch (final RuntimeException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        }
    }

    private void deleteTransitGatewayAttachment(final Ec2Client client,
                                      final ResourceModel model,
                                      final AmazonWebServicesClientProxy proxy) {
        final DeleteTransitGatewayVpcAttachmentRequest deleteTransitGatewayVpcAttachmentRequest =
                DeleteTransitGatewayVpcAttachmentRequest.builder()
                        .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
                        .build();
        proxy.injectCredentialsAndInvokeV2(deleteTransitGatewayVpcAttachmentRequest, client::deleteTransitGatewayVpcAttachment);
    }
}
