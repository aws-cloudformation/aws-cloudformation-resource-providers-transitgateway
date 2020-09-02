package software.amazon.ec2.transitgatewaymulticastdomainassociation;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AssociateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.AssociateTransitGatewayMulticastDomainResponse;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.*;

import java.util.Map;


public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = (Ec2Client) ClientBuilder.getClient();
        final AssociateTransitGatewayMulticastDomainResponse associateTransitGatewayMulticastDomainResponse;

        try {
            associateTransitGatewayMulticastDomainResponse = associateTransitGatewayMulticastDomain(client, model, proxy);
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler((Throwable) e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }
    private AssociateTransitGatewayMulticastDomainResponse associateTransitGatewayMulticastDomain(final Ec2Client client,
                                                                                            final ResourceModel model,
                                                                                            final AmazonWebServicesClientProxy proxy) {
        final AssociateTransitGatewayMulticastDomainRequest associateTransitGatewayMulticastDomainRequest =
                AssociateTransitGatewayMulticastDomainRequest.builder()
                        .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
                .transitGatewayMulticastDomainId(model.getTransitGatewayMulticastDomainId()).build();


        return proxy.injectCredentialsAndInvokeV2(associateTransitGatewayMulticastDomainRequest, client::associateTransitGatewayMulticastDomain);
    }
}
