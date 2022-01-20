package com.aws.ec2.transitgatewayvpcattachment;

import com.aws.ec2.transitgatewayvpcattachment.workflow.read.Read;
import com.aws.ec2.transitgatewayvpcattachment.workflow.read.ValidateCurrentState;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;


public class ReadHandler extends BaseHandlerStd {

    protected ProgressEvent<com.aws.ec2.transitgatewayvpcattachment.ResourceModel, com.aws.ec2.transitgatewayvpcattachment.CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> client,
        final Logger logger) {
            return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidateCurrentState(proxy, request, callbackContext, client, logger)::run)
                .then(new Read(proxy, request, callbackContext, client, logger)::run);
    }
}
