package com.aws.ec2.transitgateway;

import com.aws.ec2.transitgateway.workflow.read.Read;
import com.aws.ec2.transitgateway.workflow.read.ValidateCurrentState;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class ReadHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {

            return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidateCurrentState(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
