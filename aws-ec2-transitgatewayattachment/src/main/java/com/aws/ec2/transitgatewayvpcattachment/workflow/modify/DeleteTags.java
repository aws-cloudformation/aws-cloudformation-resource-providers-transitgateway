package com.aws.ec2.transitgatewayvpcattachment.workflow.modify;



import com.aws.ec2.transitgatewayvpcattachment.ResourceModel;
import com.aws.ec2.transitgatewayvpcattachment.CallbackContext;
import com.aws.ec2.transitgatewayvpcattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayvpcattachment.workflow.TagUtils;
import com.aws.ec2.transitgatewayvpcattachment.workflow.read.Read;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DeleteTagsRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTagsResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.util.List;

public class DeleteTags {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    com.aws.ec2.transitgatewayvpcattachment.CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;

    public DeleteTags(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            com.aws.ec2.transitgatewayvpcattachment.CallbackContext callbackContext,
            ProxyClient<Ec2Client> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext>  run(ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .handleError(this::handleError)
                .progress();
    }

    private DeleteTagsRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = this.tagsToDelete(model);
        return DeleteTagsRequest.builder()
                .resources(model.getId())
                .tags(tags)
                .build();
    }

    private DeleteTagsResponse makeServiceCall(DeleteTagsRequest request, ProxyClient<Ec2Client> client) {
        if(CollectionUtils.isEmpty(request.tags())) {
            return DeleteTagsResponse.builder().build();
        } else {
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::deleteTags);
        }
    }

    private List<Tag> tagsToDelete(ResourceModel model) {
        final List<com.aws.ec2.transitgatewayvpcattachment.Tag> prevTags =  new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getTags();
        final List<com.aws.ec2.transitgatewayvpcattachment.Tag> currTags = com.aws.ec2.transitgatewayvpcattachment.workflow.TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags());
        return TagUtils.difference(prevTags, currTags);
    }

    private ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext>  handleError(DeleteTagsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
