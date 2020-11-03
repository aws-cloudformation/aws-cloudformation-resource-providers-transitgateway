package software.amazon.ec2.transitgatewaymulticastdomain;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static software.amazon.cloudformation.proxy.OperationStatus.FAILED;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model;
        final CallbackContext context = callbackContext == null ? CallbackContext.builder().build() :
                callbackContext;

        if (context.isUpdateFailed()) {
            // CallBack initiated: previous update failed, reverting to the previous resource state
            model = request.getPreviousResourceState();
        } else {
            // Initiate the request for Update
            model = request.getDesiredResourceState();
        }
        final Ec2Client client = ClientBuilder.getClient();

        try {
            if(model.getTags() != null && !model.getTags().isEmpty()){
                handleTagging(proxy, request, context, client, logger);
            }
            logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .resourceModel(model)
                    .status(SUCCESS)
                    .build();

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
    }

    private void handleTagging(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            Ec2Client client,
            Logger logger ) {
            logger.log("HANDLE TAGGING");
            final ResourceModel oldModel = new ReadHandler().handleRequest(proxy, request, callbackContext, logger).getResourceModel();
            logger.log(oldModel.toString());
            final ResourceModel newModel = request.getDesiredResourceState();
            logger.log(newModel.toString());
            final List<Tag> prevTags = Utils.cfnTagsToSdkTags(oldModel.getTags());
            final List<Tag> currTags = Utils.cfnTagsToSdkTags(newModel.getTags());

            final Set<Tag> prevTagSet = CollectionUtils.isEmpty(prevTags) ? new HashSet<>() : new HashSet<>(prevTags);
            final Set<Tag> currTagSet = CollectionUtils.isEmpty(currTags) ? new HashSet<>() : new HashSet<>(currTags);

            List<Tag> tagsToCreate = Sets.difference(currTagSet, prevTagSet).immutableCopy().asList();
            List<Tag> tagsToDelete = Sets.difference(prevTagSet, currTagSet).immutableCopy().asList();

            final CreateTagsRequest createTagsRequest =
                    CreateTagsRequest.builder()
                            .resources(newModel.getTransitGatewayMulticastDomainId())
                            .tags(tagsToCreate)
                            .build();

            if(CollectionUtils.isNotEmpty(tagsToCreate)) {
                proxy.injectCredentialsAndInvokeV2(createTagsRequest, client::createTags);
            }

            final DeleteTagsRequest deleteTagsRequest =
                    DeleteTagsRequest.builder()
                            .resources(newModel.getTransitGatewayMulticastDomainId())
                            .tags(tagsToDelete)
                            .build();

            if(CollectionUtils.isNotEmpty(tagsToDelete)) {
                proxy.injectCredentialsAndInvokeV2(deleteTagsRequest, client::deleteTags);
            }


    }
}
