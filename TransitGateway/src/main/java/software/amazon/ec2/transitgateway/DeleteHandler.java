package software.amazon.ec2.transitgateway;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.util.List;


import static software.amazon.cloudformation.proxy.OperationStatus.*;
import static software.amazon.ec2.transitgateway.Utils.*;

public class DeleteHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();

        try {
            int remainingRetryCount = MAX_CALLBACK_COUNT;
            final List<TransitGateway> transitGateways = Utils.describeTransitGatewaysResponse(client, model, proxy).transitGateways();


            final TransitGatewayState stateCode = transitGateways.get(0).state();
            switch (stateCode) {
                case AVAILABLE:
                    deleteTransitGateway(client, model, proxy);
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
                    if (callbackContext == null || !callbackContext.isActionStarted()) {
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .status(OperationStatus.FAILED)
                                .errorCode(HandlerErrorCode.NotFound)
                                .message(HandlerErrorCode.NotFound.getMessage())
                                .build();
                    }else{
                        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                                .status(SUCCESS)
                                .build();
                    }

                default:
                    throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode));
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

    private void deleteTransitGateway(final Ec2Client client,
                                                     final ResourceModel model,
                                                     final AmazonWebServicesClientProxy proxy) {
        final DeleteTransitGatewayRequest deleteTransitGatewayRequest =
                DeleteTransitGatewayRequest.builder()
                        .transitGatewayId(model.getTransitGatewayId())
                        .build();
        proxy.injectCredentialsAndInvokeV2(deleteTransitGatewayRequest, client::deleteTransitGateway);
    }
}
