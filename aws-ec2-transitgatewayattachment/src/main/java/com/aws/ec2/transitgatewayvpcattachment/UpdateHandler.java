package com.aws.ec2.transitgatewayvpcattachment;

import com.aws.ec2.transitgatewayvpcattachment.workflow.modify.CreateTags;
import com.aws.ec2.transitgatewayvpcattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {

    protected ProgressEvent<com.aws.ec2.transitgatewayvpcattachment.ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {


        logger.log("Got Update Req:");

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new com.aws.ec2.transitgatewayvpcattachment.workflow.modify.ValidateCurrentState(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new com.aws.ec2.transitgatewayvpcattachment.workflow.modify.ValidatePropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new CreateTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new com.aws.ec2.transitgatewayvpcattachment.workflow.modify.DeleteTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new com.aws.ec2.transitgatewayvpcattachment.workflow.modify.Update(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
