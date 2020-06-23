package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsResponse;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayMulticastDomainAssociationsResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final ResourceModel readResult;

        // Describe TransitGatewayMulticastDomain
        try {
            final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = describeTransitGatewayMulticastDomain(client, model, proxy);
            final TransitGatewayMulticastDomain transitGatewayMulticastDomain = describeTransitGatewayMulticastDomainsResponse.transitGatewayMulticastDomains().get(0);
            readResult = Utils.transformTransitGatewayMulticastDomain(transitGatewayMulticastDomain);
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s read succeeded", ResourceModel.TYPE_NAME));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(readResult)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomain(final Ec2Client client,
                                                                                            final ResourceModel model,
                                                                                            final AmazonWebServicesClientProxy proxy) {
        final DescribeTransitGatewayMulticastDomainsRequest describeTransitGatewayMulticastDomainsRequest = DescribeTransitGatewayMulticastDomainsRequest.builder()
                .transitGatewayMulticastDomainIds(model.getTransitGatewayMulticastDomainId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewayMulticastDomainsRequest, client::describeTransitGatewayMulticastDomains);
    }
}
