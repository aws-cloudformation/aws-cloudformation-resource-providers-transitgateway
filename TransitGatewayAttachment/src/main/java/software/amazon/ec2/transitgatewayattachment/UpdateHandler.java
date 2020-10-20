package software.amazon.ec2.transitgatewayattachment;

// TODO: replace all usage of SdkClient with your service client type, e.g; YourServiceAsyncClient
// import software.amazon.awssdk.services.yourservice.YourServiceAsyncClient;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static software.amazon.cloudformation.proxy.OperationStatus.FAILED;
import static software.amazon.cloudformation.proxy.OperationStatus.SUCCESS;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<SdkClient> proxyClient,
            final Logger logger) {

        final ResourceModel model;
        if (callbackContext != null && callbackContext.isUpdateFailed()) {
            // CallBack initiated: previous update failed, reverting to the previous resource state
            model = request.getPreviousResourceState();
        } else {
            // Initiate the request for Update
            model = request.getDesiredResourceState();
        }
        final Ec2Client client = ClientBuilder.getClient();
        final ModifyTransitGatewayVpcAttachmentResponse modifyTransitGatewayVpcAttachmentResponse;

        try {

            modifyTransitGatewayVpcAttachmentResponse = modifyTransitGatewayVpcAttachment(client, model, proxy);
            if (model.getTags() != null && !model.getTags().isEmpty()) {
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

        logger.log(String.format("%s [%s] update succeeded", ResourceModel.TYPE_NAME, model.getPrimaryIdentifier()));
        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(SUCCESS)
                .build();
    }

    private ModifyTransitGatewayVpcAttachmentResponse modifyTransitGatewayVpcAttachment(final Ec2Client client,
                                                                                        final ResourceModel model,
                                                                                        final AmazonWebServicesClientProxy proxy) {
        final ModifyTransitGatewayVpcAttachmentRequest modifyTransitGatewayVpcAttachmentRequest =
                ModifyTransitGatewayVpcAttachmentRequest.builder()
                        .transitGatewayAttachmentId(model.getTransitGatewayAttachmentId())
                        .addSubnetIds(model.getAddSubnetIds())
                        .removeSubnetIds(model.getRemoveSubnetIds())
                        .options(Translator.translateOptions(model.getOptions()))
                        .build();


        return proxy.injectCredentialsAndInvokeV2(modifyTransitGatewayVpcAttachmentRequest, client::modifyTransitGatewayVpcAttachment);
    }


    private void handleTagging(final AmazonWebServicesClientProxy proxy,
                               final ResourceHandlerRequest<ResourceModel> request,
                               Ec2Client client) {
        // Add tag
        try {
            final ResourceModel oldModel = request.getPreviousResourceState();
            final ResourceModel newModel = request.getDesiredResourceState();
            final Map<String, String> desiredResourceTags = request.getDesiredResourceTags();
            final Map<String, String> previousResourceTags = request.getPreviousResourceTags();
            List<Tag> newListTag = Translator.translateTagsToTagSpecifications(desiredResourceTags);
            List<Tag> oldListTag = Translator.translateTagsToTagSpecifications(previousResourceTags);
//            final List<Tag> prevTags = Translator.cfnTagsToSdkTags(oldModel.getTags());
//            final List<Tag> currTags = Translator.cfnTagsToSdkTags(newModel.getTags());
            final Set<Tag> prevTagSet = CollectionUtils.isEmpty(oldListTag) ? new HashSet<>() : new HashSet<>(oldListTag);
            final Set<Tag> currTagSet = CollectionUtils.isEmpty(newListTag) ? new HashSet<>() : new HashSet<>(newListTag);

            List<Tag> tagsToCreate = Sets.difference(currTagSet, prevTagSet).immutableCopy().asList();
            List<Tag> tagsToDelete = Sets.difference(prevTagSet, currTagSet).immutableCopy().asList();

            System.out.println("fjgdhfgdjfgjd"+newModel.getTransitGatewayAttachmentId());
            final CreateTagsRequest createTagsRequest =
                    CreateTagsRequest.builder()
                            .resources(newModel.getTransitGatewayAttachmentId())
                            .tags(tagsToCreate)
                            .build();

            if (CollectionUtils.isNotEmpty(tagsToCreate)) {
                proxy.injectCredentialsAndInvokeV2(createTagsRequest, client::createTags);
            }

            final DeleteTagsRequest deleteTagsRequest =
                    DeleteTagsRequest.builder()
                            .resources(newModel.getTransitGatewayAttachmentId())
                            .tags(tagsToDelete)
                            .build();

            if (CollectionUtils.isNotEmpty(tagsToDelete)) {
                proxy.injectCredentialsAndInvokeV2(deleteTagsRequest, client::deleteTags);
            }

        } catch (final Exception e) {
            e.printStackTrace();
            throw new CfnGeneralServiceException("updateTagging", e);
        }
    }

}