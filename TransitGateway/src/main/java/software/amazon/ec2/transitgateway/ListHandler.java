package software.amazon.ec2.transitgateway;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewaysResponse;
import software.amazon.awssdk.services.ec2.model.TransitGateway;
import software.amazon.awssdk.services.ec2.model.TransitGatewayState;
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

        // List the TransitGateways
        try {
            final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = describeTransitGateways(client, nextToken, proxy);
            nextToken = describeTransitGatewaysResponse.nextToken();

            for (final TransitGateway transitGateway : describeTransitGatewaysResponse.transitGateways()) {
                if(!transitGateway.state().equals(TransitGatewayState.DELETED)) models.add(Utils.transformTransitGateway(transitGateway));
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

    private DescribeTransitGatewaysResponse describeTransitGateways(final Ec2Client client,
                                                                                                  final String nextToken,
                                                                                                  final AmazonWebServicesClientProxy proxy) {
        final DescribeTransitGatewaysRequest describeTransitGatewaysRequest = DescribeTransitGatewaysRequest.builder()
                .nextToken(nextToken)
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewaysRequest, client::describeTransitGateways);
    }
}
