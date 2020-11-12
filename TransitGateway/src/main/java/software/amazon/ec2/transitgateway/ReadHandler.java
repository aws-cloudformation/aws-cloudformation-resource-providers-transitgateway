package software.amazon.ec2.transitgateway;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;


import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final Ec2Client client = ClientBuilder.getClient();
        final ResourceModel readResult;

        // Describe TransitGateway
        try {

            final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = Utils.describeTransitGateways(client, model, proxy);

            final TransitGateway transitGateway = describeTransitGatewaysResponse.transitGateways().get(0);
            readResult = Utils.transformTransitGateway(transitGateway);
            final TransitGatewayState stateCode = transitGateway.state();
            if(stateCode.equals(TransitGatewayState.DELETED)){
                logger.log(String.format("%s here", ResourceModel.TYPE_NAME));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .status(OperationStatus.FAILED)
                        .errorCode(HandlerErrorCode.NotFound)
                        .message(HandlerErrorCode.NotFound.getMessage())
                        .build();
            }

        } catch (final AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        } catch (final CfnNotFoundException e) {
            //NotFound returned from Delete handler will be considered by CFN backend service as success
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch (final RuntimeException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        }

        logger.log(String.format("%s read succeeded", ResourceModel.TYPE_NAME));

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(readResult)
                .status(OperationStatus.SUCCESS)
                .build();
    }



}
