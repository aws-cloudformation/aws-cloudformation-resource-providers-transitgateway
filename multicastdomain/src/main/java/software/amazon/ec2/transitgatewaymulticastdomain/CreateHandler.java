package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;


public class CreateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Map<String, String> desiredResourceTags = request.getDesiredResourceTags();
        final Ec2Client client = ClientBuilder.getClient();
        final CreateTransitGatewayMulticastDomainResponse createTransitGatewayMulticastDomainResponse;

        try {
            createTransitGatewayMulticastDomainResponse = createTransitGatewayMulticastDomain(client, model, desiredResourceTags, proxy);
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        model.setTransitGatewayId(createTransitGatewayMulticastDomainResponse.transitGatewayMulticastDomain().transitGatewayId());
        model.setTransitGatewayMulticastDomainId(createTransitGatewayMulticastDomainResponse.transitGatewayMulticastDomain().transitGatewayMulticastDomainId());

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private CreateTransitGatewayMulticastDomainResponse createTransitGatewayMulticastDomain(final Ec2Client client,
                                                                                            final ResourceModel model,
                                                                                            final Map<String, String> desiredResourceTags,
                                                                                            final AmazonWebServicesClientProxy proxy) {
        final CreateTransitGatewayMulticastDomainRequest createTransitGatewayMulticastDomainRequest =
                CreateTransitGatewayMulticastDomainRequest.builder()
                        .tagSpecifications(Utils.translateTagsToTagSpecifications(desiredResourceTags))
                        .transitGatewayId(model.getTransitGatewayId())
                        .build();

        return proxy.injectCredentialsAndInvokeV2(createTransitGatewayMulticastDomainRequest, client::createTransitGatewayMulticastDomain);
    }
}
