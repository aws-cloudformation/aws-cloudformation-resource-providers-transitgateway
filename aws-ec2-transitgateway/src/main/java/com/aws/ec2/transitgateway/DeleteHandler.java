package com.aws.ec2.transitgateway;

import com.aws.ec2.transitgateway.workflow.delete.Delete;
import com.aws.ec2.transitgateway.workflow.delete.ValidateCurrentState;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class DeleteHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {

        logger.log(String.valueOf(request.getDesiredResourceState()));
        logger.log("--- delete Handler ---");
        logger.log(callbackContext.toString());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new ValidateCurrentState(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Delete(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(_progress -> ProgressEvent.defaultSuccessHandler(null)); //After delete successfully completes we want to return a null response because the model no longer exits
    }
}
