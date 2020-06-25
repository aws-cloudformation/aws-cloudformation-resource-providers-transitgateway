package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayMulticastDomainResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();

        //Delete the TransitGatewayMulticastDomain
        try {
            deleteTransitGatewayMulticastDomain(client, model, proxy);
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] deletion succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(model)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private DeleteTransitGatewayMulticastDomainResponse deleteTransitGatewayMulticastDomain(final Ec2Client client,
                                                                                            final ResourceModel model,
                                                                                            final AmazonWebServicesClientProxy proxy) {
        final DeleteTransitGatewayMulticastDomainRequest deleteTransitGatewayMulticastDomainRequest =
                DeleteTransitGatewayMulticastDomainRequest.builder()
                    .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId())
                    .build();
        return proxy.injectCredentialsAndInvokeV2(deleteTransitGatewayMulticastDomainRequest, client::deleteTransitGatewayMulticastDomain);
    }
}
