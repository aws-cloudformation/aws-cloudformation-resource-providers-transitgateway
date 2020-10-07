package software.amazon.ec2.transitgateway;


import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;

import software.amazon.cloudformation.proxy.*;

import java.util.List;
import java.util.Map;


import static software.amazon.cloudformation.proxy.OperationStatus.*;
import static software.amazon.ec2.transitgateway.Utils.*;



public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final CreateTransitGatewayResponse createTransitGatewayResponse;

        try {

            int remainingRetryCount = MAX_CALLBACK_COUNT;
            // Call NetworkManager api to register TransitGateway
            if (callbackContext != null) {
                createTransitGatewayResponse = createTransitGateway(client, model, proxy);
                Options options = Utils.translateTransitGatewayOptionsToOptions(createTransitGatewayResponse.transitGateway().options());
                model.setTransitGatewayId(createTransitGatewayResponse.transitGateway().transitGatewayId());
                model.setTransitGatewayArn(createTransitGatewayResponse.transitGateway().transitGatewayArn());
                model.setOptions(options);
                logger.log(String.format("%s [%s] creation pending", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModel(model)
                        .status(IN_PROGRESS)
                        .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                        .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                        .build();
            }
            // Refresh remaining retry count
            if (callbackContext == null) {
                remainingRetryCount = callbackContext.getRemainingRetryCount();
                if (remainingRetryCount == 0) {
                    throw new RuntimeException(TIMED_OUT_MESSAGE);
                }
                remainingRetryCount--;
            }

            final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = Utils.describeTransitGateways(client, model, proxy);
            if (describeTransitGatewaysResponse.transitGateways().isEmpty()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
            }
            final TransitGatewayState stateCode = describeTransitGatewaysResponse.transitGateways().get(0).state();

            switch (stateCode) {
                case AVAILABLE:
                    logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(SUCCESS)
                            .build();
                case PENDING:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                default:
                    throw new RuntimeException(String.format(UNRECOGNIZED_STATE_MESSAGE, stateCode));
            }

        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

    }

    private CreateTransitGatewayResponse createTransitGateway(final Ec2Client client,
                                                        final ResourceModel model,
                                                        final AmazonWebServicesClientProxy proxy) {
        List<Tag> tags = model.getTags();
        List<software.amazon.awssdk.services.ec2.model.Tag> listTags = Utils.cfnTagsToSdkTags(tags);
        final CreateTransitGatewayRequest createTransitGatewayRequest =
                CreateTransitGatewayRequest.builder()
                        .description(model.getDescription())
                        .tagSpecifications(Utils.translateTagsToTagSpecifications(listTags))
                        .options(Utils.translateOptionsToTransitGatewayRequestOptions(model.getOptions())).build();

        return proxy.injectCredentialsAndInvokeV2(createTransitGatewayRequest, client::createTransitGateway);


    }


}
