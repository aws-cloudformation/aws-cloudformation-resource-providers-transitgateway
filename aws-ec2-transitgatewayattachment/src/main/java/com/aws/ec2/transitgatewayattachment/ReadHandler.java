package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends com.aws.ec2.transitgatewayattachment.BaseHandlerStd {

    protected ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final com.aws.ec2.transitgatewayattachment.CallbackContext callbackContext,
        final ProxyClient<Ec2Client> client,
        final Logger logger) {
            return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new Read(proxy, request, callbackContext, client, logger)::run);
    }
}
