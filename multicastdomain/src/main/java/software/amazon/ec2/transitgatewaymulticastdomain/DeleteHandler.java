package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.MAX_CALLBACK_COUNT;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.CALlBACK_PERIOD_30_SECONDS;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.TIMED_OUT_MESSAGE;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.UNRECOGNIZED_STATE_MESSAGE;
import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        // Initiate the request
        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();

        try {
            // Refresh remaining retry count
            int remainingRetryCount = MAX_CALLBACK_COUNT;
            if (callbackContext != null) {
                remainingRetryCount = callbackContext.getRemainingRetryCount();
                if (remainingRetryCount == 0) {
                    throw new RuntimeException(TIMED_OUT_MESSAGE);
                }
                remainingRetryCount--;
            }

            final List<TransitGatewayMulticastDomain> transitGatewayMulticastDomains = Utils.describeTransitGatewayMulticastDomainsResponse(client, model, proxy).transitGatewayMulticastDomains();

            // if no Multicast Domains found
            if (transitGatewayMulticastDomains.isEmpty()) {
                if (callbackContext == null) {
                    throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
                } else {
                    // if this is a callback from a previous deletion request, return deletion success
                    logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .status(SUCCESS)
                            .build();
                }
            }
            final TransitGatewayMulticastDomainState stateCode = transitGatewayMulticastDomains.get(0).state();
            switch (stateCode) {
                case AVAILABLE:
                    deleteTransitGatewayMulticastDomain(client, model, proxy);
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
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS) //callback until multicast domain is deleted
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                case DELETED: // return success because DELETED is a terminated state
                    logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(request.getDesiredResourceState())
                            .status(SUCCESS)
                            .build();
                default:
                    throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode));
            }
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        } catch (final CfnNotFoundException e) {
            //NotFound returned from Delete handler will be considered by CFN backend service as success
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch (final RuntimeException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InternalFailure);
        }
    }

    private void deleteTransitGatewayMulticastDomain(final Ec2Client client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final DeleteTransitGatewayMulticastDomainRequest deregisterTransitGatewayRequest =
                DeleteTransitGatewayMulticastDomainRequest.builder()
                        .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
                        .build();
        proxy.injectCredentialsAndInvokeV2(deregisterTransitGatewayRequest, client::deleteTransitGatewayMulticastDomain);
    }
}
