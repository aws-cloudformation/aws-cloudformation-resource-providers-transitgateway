package software.amazon.ec2.transitgateway;


import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.*;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static software.amazon.cloudformation.proxy.OperationStatus.*;
import static software.amazon.ec2.transitgateway.Utils.*;


public class UpdateHandler extends BaseHandlerStd {


    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<SdkClient> proxyClient,
        final Logger logger) {

        final ResourceModel model;
        int remainingRetryCount = MAX_CALLBACK_COUNT;
        final Ec2Client client = ClientBuilder.getClient();
        final ModifyTransitGatewayResponse modifyTransitGatewayResponse;
        final String transitGatewayId;
        try {
            final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = Utils.describeTransitGateways(client, request.getPreviousResourceState(), proxy);

            final TransitGateway transitGateway = describeTransitGatewaysResponse.transitGateways().get(0);
            final TransitGatewayState stateCode = transitGateway.state();
            if(stateCode.equals(TransitGatewayState.DELETED)){
                logger.log(String.format("%s here", ResourceModel.TYPE_NAME));
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .status(OperationStatus.FAILED)
                        .errorCode(HandlerErrorCode.NotFound)
                        .message(HandlerErrorCode.NotFound.getMessage())
                        .build();
            }

            transitGatewayId = request.getPreviousResourceState().getTransitGatewayId();
            if (callbackContext != null && callbackContext.isUpdateFailed()) {
                // CallBack initiated: previous update failed, reverting to the previous resource state
                model = request.getPreviousResourceState();
            } else {
                // Initiate the request for Update
                model = request.getDesiredResourceState();
            }
        }catch (final CfnNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.NotFound);
        }catch (AwsServiceException e) {
            return ProgressEvent.defaultFailureHandler(e, ExceptionMapper.mapToHandlerErrorCode(e));
        } catch (final RuntimeException e) {
            return ProgressEvent.defaultFailureHandler(e, HandlerErrorCode.InvalidRequest);
        }


        try {
            if (hasCreateOnlyProperties(request.getDesiredResourceState(), request.getPreviousResourceState())) {
                throw new CfnInvalidRequestException("Attempting to set a CREATE ONLY Property.");
            }
            model.setTransitGatewayId(transitGatewayId);
            modifyTransitGatewayResponse = modifyTransitGateway(client, model, proxy);
            if(model.getTags() != null && !model.getTags().isEmpty()){
                handleTagging(proxy, request, client);
            }


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

        final DescribeTransitGatewaysResponse describeTransitGatewaysResponse = describeTransitGateways(client, model, proxy);
        ReadHandler readHandler = new ReadHandler();
        try {
            if (describeTransitGatewaysResponse.transitGateways().isEmpty()) {
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, model.getPrimaryIdentifier().toString());
            }
            final TransitGatewayState stateCode = describeTransitGatewaysResponse.transitGateways().get(0).state();

            switch (stateCode) {
                case DELETED:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .message(HandlerErrorCode.NotFound.getMessage())
                            .build();
                case MODIFYING:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(IN_PROGRESS)
                            .callbackDelaySeconds(CALlBACK_PERIOD_30_SECONDS)
                            .callbackContext(CallbackContext.builder().actionStarted(true).remainingRetryCount(remainingRetryCount).build())
                            .build();
                case AVAILABLE:
                    logger.log(String.format("%s [%s] modify succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
                    return readHandler.handleRequest(proxy, request, null, logger);
                default:
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .resourceModel(model)
                            .status(OperationStatus.FAILED)
                            .build();
            }
        }catch (Ec2Exception e) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(OperationStatus.FAILED)
                    .errorCode(ExceptionMapper.mapToHandlerErrorCode(e))
                    .message(e.getMessage())
                    .build();
        }

    }

    private ModifyTransitGatewayResponse modifyTransitGateway(final Ec2Client client,
                                          final ResourceModel model,
                                          final AmazonWebServicesClientProxy proxy) {


        final ModifyTransitGatewayRequest modifyTransitGatewayRequest =
                ModifyTransitGatewayRequest.builder()
                        .transitGatewayId(model.getTransitGatewayId())
                        .description(model.getDescription())
                        .options(modifyTransitGatewayOptions(model))
                        .build();


        return proxy.injectCredentialsAndInvokeV2(modifyTransitGatewayRequest, client::modifyTransitGateway);
    }

    static ModifyTransitGatewayOptions modifyTransitGatewayOptions(ResourceModel model){
        return ModifyTransitGatewayOptions.builder()
                .propagationDefaultRouteTableId(model.getPropagationDefaultRouteTableId())
                .associationDefaultRouteTableId(model.getAssociationDefaultRouteTableId())
                .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
                .dnsSupport(model.getDnsSupport())
                .vpnEcmpSupport(model.getVpnEcmpSupport())
                .build();

    }




    private void handleTagging(final AmazonWebServicesClientProxy proxy,
                                final ResourceHandlerRequest<ResourceModel> request,
                                Ec2Client client) {
        // Add tag
        try {
            final ResourceModel oldModel = request.getPreviousResourceState();
            final ResourceModel newModel = request.getDesiredResourceState();
            final List<Tag> prevTags = Utils.cfnTagsToSdkTags(oldModel.getTags());
            final List<Tag> currTags = Utils.cfnTagsToSdkTags(newModel.getTags());

            final Set<Tag> prevTagSet = CollectionUtils.isEmpty(prevTags) ? new HashSet<>() : new HashSet<>(prevTags);
            final Set<Tag> currTagSet = CollectionUtils.isEmpty(currTags) ? new HashSet<>() : new HashSet<>(currTags);

            List<Tag> tagsToCreate = Sets.difference(currTagSet, prevTagSet).immutableCopy().asList();
            List<Tag> tagsToDelete = Sets.difference(prevTagSet, currTagSet).immutableCopy().asList();

            final CreateTagsRequest createTagsRequest =
                    CreateTagsRequest.builder()
                            .resources(newModel.getTransitGatewayId())
                            .tags(tagsToCreate)
                            .build();

            if(CollectionUtils.isNotEmpty(tagsToCreate)) {
                proxy.injectCredentialsAndInvokeV2(createTagsRequest, client::createTags);
            }

            final DeleteTagsRequest deleteTagsRequest =
                    DeleteTagsRequest.builder()
                            .resources(newModel.getTransitGatewayId())
                            .tags(tagsToDelete)
                            .build();

            if(CollectionUtils.isNotEmpty(tagsToDelete)) {
                proxy.injectCredentialsAndInvokeV2(deleteTagsRequest, client::deleteTags);
            }

        } catch (final Exception e) {
            throw new CfnGeneralServiceException("updateTagging", e);
        }

    }

    private boolean hasCreateOnlyProperties(final ResourceModel previousModel, final ResourceModel currentModel) {
        return (!previousModel.getAmazonSideAsn().equals(currentModel.getAmazonSideAsn()) || (!previousModel.getMulticastSupport().equals(currentModel.getMulticastSupport())));
    }



}
