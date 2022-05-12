package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import com.aws.ec2.transitgatewayattachment.workflow.read.ValidateCurrentState;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;


public class ReadHandler extends BaseHandlerStd {

    protected ProgressEvent<com.aws.ec2.transitgatewayattachment.ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext> handleRequest(
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
