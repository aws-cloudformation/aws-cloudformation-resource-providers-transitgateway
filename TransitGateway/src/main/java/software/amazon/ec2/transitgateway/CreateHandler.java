package software.amazon.ec2.transitgateway;


import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;

import software.amazon.cloudformation.proxy.*;


import java.util.ArrayList;
import java.util.List;

import static software.amazon.cloudformation.proxy.OperationStatus.*;
import static software.amazon.ec2.transitgateway.Utils.*;



public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<SdkClient> proxyClient,
            final Logger logger) {


        final ResourceModel model = ModelAdapter.setDefaults(request.getDesiredResourceState());
        final Ec2Client client = ClientBuilder.getClient();
        final CreateTransitGatewayResponse createTransitGatewayResponse;
        int remainingRetryCount = MAX_CALLBACK_COUNT;


        logger.log(String.valueOf(model));

        if (callbackContext == null || !callbackContext.isActionStarted()) {

            if (hasReadOnlyProperties(request.getDesiredResourceState())) {
                throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property.");
            }
            try {
                createTransitGatewayResponse = createTransitGateway(client, model, proxy);

            } catch (AwsServiceException e) {
                return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
            }
            model.setTransitGatewayId(createTransitGatewayResponse.transitGateway().transitGatewayId());
            model.setTransitGatewayArn(createTransitGatewayResponse.transitGateway().transitGatewayArn());
            model.setPropagationDefaultRouteTableId(createTransitGatewayResponse.transitGateway().options().propagationDefaultRouteTableId());
            model.setAssociationDefaultRouteTableId(createTransitGatewayResponse.transitGateway().options().associationDefaultRouteTableId());

        }

        System.out.println(String.valueOf(model));

        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = describeTransitGateways(client, model, proxy);

        try {

            final TransitGatewayState stateCode = describeTransitGatewaysResponse.transitGateways().get(0).state();

            switch (stateCode) {
                case PENDING:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                case AVAILABLE:
                    logger.log(String.format("%s [%s] creation succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(SUCCESS)
                            .build();
                default:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .build();
            }
        }catch (final AwsServiceException e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .build();
        } catch (final CfnNotFoundException e) {
            //NotFound returned from Delete handler will be considered by CFN backend service as success
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        } catch (final RuntimeException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        }catch (final Exception e) {
            e.printStackTrace();
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.GeneralServiceException);
        }


    }

    private CreateTransitGatewayResponse createTransitGateway(final Ec2Client client,
                                                              final ResourceModel model,
                                                              final AmazonWebServicesClientProxy proxy) {


            final CreateTransitGatewayRequest createTransitGatewayRequest =
                    CreateTransitGatewayRequest.builder()
                            .description(model.getDescription())
                            .tagSpecifications(Utils.translateTagsToTagSpecifications(model.getTags()))
                            .options(Utils.transitGatewayRequestOptions(model)).build();

            return proxy.injectCredentialsAndInvokeV2(createTransitGatewayRequest, client::createTransitGateway);


    }

    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getTransitGatewayId() != null || model.getTransitGatewayArn() != null;
    }


}
