package software.amazon.ec2.transitgateway;


import com.google.common.collect.Sets;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static software.amazon.cloudformation.proxy.OperationStatus.FAILED;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;


public class UpdateHandler extends BaseHandlerStd {


    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model;
        if (callbackContext != null && callbackContext.isUpdateFailed()) {
            // CallBack initiated: previous update failed, reverting to the previous resource state
            model = request.getPreviousResourceState();
        } else {
            // Initiate the request for Update
            model = request.getDesiredResourceState();
        }
        final Ec2Client client = ClientBuilder.getClient();
        final ModifyTransitGatewayResponse modifyTransitGatewayResponse;

        try {
            modifyTransitGatewayResponse = modifyTransitGateway(client, model, proxy);

        } catch (final AwsServiceException e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    // For failure update: adding CallBackContext to revert to the previous version
                    .callbackContext(callbackContext == null ? CallbackContext.builder().updateFailed(true).build() : null)
                    .build();
        }

        logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();

    }

    private ModifyTransitGatewayResponse modifyTransitGateway(final Ec2Client client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {
        final ModifyTransitGatewayRequest modifyTransitGatewayRequest =
                ModifyTransitGatewayRequest.builder()
                        .transitGatewayId(model.getTransitGatewayId())
                        .description(model.getDescription())
                        .options(Utils.translateOptions(model.getOptions()))
                        .build();


        return proxy.injectCredentialsAndInvokeV2(modifyTransitGatewayRequest, client::modifyTransitGateway);
    }


}
