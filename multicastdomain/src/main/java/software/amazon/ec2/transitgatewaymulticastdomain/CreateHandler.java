package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

import static software.amazon.cloudformation.proxy.OperationStatus.IN_PROGRESS;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.MAX_CALLBACK_COUNT;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.CALlBACK_PERIOD_30_SECONDS;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.TIMED_OUT_MESSAGE;
import static software.amazon.ec2.transitgatewaymulticastdomain.Utils.UNRECOGNIZED_STATE_MESSAGE;

public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final CreateTransitGatewayMulticastDomainResponse createTransitGatewayMulticastDomainResponse;
        final CallbackContext context = callbackContext == null ? CallbackContext.builder().build() :
                callbackContext;
        if(!context.isActionStarted()) {
            try {
                createTransitGatewayMulticastDomainResponse = createTransitGatewayMulticastDomain(client, model, proxy, logger);
            } catch (final AwsServiceException e) {
                return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
            }
            model.setTransitGatewayMulticastDomainId(createTransitGatewayMulticastDomainResponse.transitGatewayMulticastDomain().transitGatewayMulticastDomainId());
        } else {
            model.setTransitGatewayMulticastDomainId(context.getTransitGatewayMulticastDomainId());
        }
        return this.handleStatus(client, model, proxy, context, logger);
    }

    public ProgressEvent<ResourceModel, CallbackContext> handleStatus(
            final Ec2Client client,
            final ResourceModel model,
            final AmazonWebServicesClientProxy proxy,
            final CallbackContext callbackContext,
            final Logger logger) {

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
        logger.log(transitGatewayMulticastDomains.toString());
        final TransitGatewayMulticastDomainState stateCode = transitGatewayMulticastDomains.get(0).state();
        switch (stateCode) {
            case AVAILABLE:
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(OperationStatus.SUCCESS)
                        .build();
            case PENDING:
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(IN_PROGRESS)
                        .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS) //callback until multicast domain is deleted
                        .callbackContext(CallbackContext.builder().actionStarted(true).transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId()).remainingRetryCount(remainingRetryCount).build())
                        .build();
            default:
                throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode));
        }
    }


    private CreateTransitGatewayMulticastDomainResponse createTransitGatewayMulticastDomain(
            final Ec2Client client,
            final ResourceModel model,
            final AmazonWebServicesClientProxy proxy,
            final Logger logger) {

        List<Tag> tags = model.getTags();
        List<software.amazon.awssdk.services.ec2.model.Tag> listTags = Utils.cfnTagsToSdkTags(tags);
        final CreateTransitGatewayMulticastDomainRequest createTransitGatewayMulticastDomainRequest =
                CreateTransitGatewayMulticastDomainRequest.builder()
                        .tagSpecifications(Utils.translateTagsToTagSpecifications(listTags))
                        .transitGatewayId(model.getTransitGatewayId())
                        .build();

        return proxy.injectCredentialsAndInvokeV2(createTransitGatewayMulticastDomainRequest, client::createTransitGatewayMulticastDomain);
    }
}
