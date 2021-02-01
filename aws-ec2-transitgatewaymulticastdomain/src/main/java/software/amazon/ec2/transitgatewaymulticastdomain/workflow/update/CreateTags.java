package software.amazon.ec2.transitgatewaymulticastdomain.workflow.update;

import software.amazon.ec2.transitgatewaymulticastdomain.CallbackContext;
import software.amazon.ec2.transitgatewaymulticastdomain.ResourceModel;
import software.amazon.ec2.transitgatewaymulticastdomain.workflow.ExceptionMapper;
import software.amazon.ec2.transitgatewaymulticastdomain.workflow.TagUtils;
import software.amazon.ec2.transitgatewaymulticastdomain.workflow.read.Read;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsResponse;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.proxy.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTags {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, CallbackContext>  progress;
    Map<String, String> stackTags;
    Map<String, String> previousStackTags;
    public CreateTags(
        AmazonWebServicesClientProxy proxy,
        ResourceHandlerRequest<ResourceModel> request,
        CallbackContext callbackContext,
        ProxyClient<Ec2Client> client,
        Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
    }

    public ProgressEvent<ResourceModel, CallbackContext>  run(ProgressEvent<ResourceModel, CallbackContext> progress) {
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
            .translateToServiceRequest(this::translateModelToRequest)
            .makeServiceCall(this::makeServiceCall)
            .handleError(this::handleError)
            .progress();
    }

    private CreateTagsRequest translateModelToRequest(ResourceModel model) {
        List<Tag> tags = this.tagsToCreate(model);
        return CreateTagsRequest.builder()
            .resources(model.getTransitGatewayMulticastDomainId())
            .tags(tags)
            .build();
    }

    private CreateTagsResponse makeServiceCall(CreateTagsRequest request, ProxyClient<Ec2Client> client) {
        if(CollectionUtils.isEmpty(request.tags())) {
            return CreateTagsResponse.builder().build();
        } else {
            return proxy.injectCredentialsAndInvokeV2(request, client.client()::createTags);
        }
    }

    private List<Tag> tagsToCreate(ResourceModel model) {
        List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> prevTags = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getTags();
        List<software.amazon.ec2.transitgatewaymulticastdomain.Tag> currTags = TagUtils.mergeResourceModelAndStackTags(model.getTags(), this.request.getDesiredResourceTags());
        return TagUtils.difference(currTags, prevTags);
    }

    protected ProgressEvent<ResourceModel, CallbackContext>  handleError(CreateTagsRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
       return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
    }
}
