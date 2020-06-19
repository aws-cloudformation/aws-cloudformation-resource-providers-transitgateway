package software.amazon.ec2.transitgatewaymulticastdomain;

import com.amazonaws.services.lambda.runtime.Client;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomain;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final List<ResourceModel> models = new ArrayList<>();
        final Ec2Client client = ClientBuilder.getClient();
        String nextToken = request.getNextToken();

        try {
            final DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomainsResponse = describeTransitGatewayMulticastDomains(client, nextToken, proxy);
            nextToken = describeTransitGatewayMulticastDomainsResponse.nextToken();

            for (final TransitGatewayMulticastDomain transitGatewayMulticastDomain : describeTransitGatewayMulticastDomainsResponse.transitGatewayMulticastDomains()) {
                models.add(Utils.transformTransitGatewayMulticastDomain(transitGatewayMulticastDomain));
            }
        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        }

        logger.log(String.format("%s list succeeded", ResourceModel.TYPE_NAME));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .nextToken(nextToken)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private DescribeTransitGatewayMulticastDomainsResponse describeTransitGatewayMulticastDomains(final Ec2Client client,
                                                                                                  final String nextToken,
                                                                                                  final AmazonWebServicesClientProxy proxy) {
        final List<ResourceModel> readResult = new ArrayList<>();
        final DescribeTransitGatewayMulticastDomainsRequest describeTransitGatewayMulticastDomainsRequest = DescribeTransitGatewayMulticastDomainsRequest.builder()
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewayMulticastDomainsRequest, client::describeTransitGatewayMulticastDomains);
    }
}
