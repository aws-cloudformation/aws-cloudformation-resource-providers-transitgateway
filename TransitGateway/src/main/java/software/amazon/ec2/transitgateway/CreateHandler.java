package software.amazon.ec2.transitgateway;


import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.*;

import java.util.List;
import java.util.Map;


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
            createTransitGatewayResponse = createTransitGateway(client, model, proxy);
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }
        Options options = Utils.translateTransitGatewayOptionsToOptions(createTransitGatewayResponse.transitGateway().options());
        model.setTransitGatewayId(createTransitGatewayResponse.transitGateway().transitGatewayId());
        model.setTransitGatewayArn(createTransitGatewayResponse.transitGateway().transitGatewayArn());
        model.setOptions(options);


        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
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
