package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.modify.*;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {

    protected ProgressEvent<com.aws.ec2.transitgatewayattachment.ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {


        logger.log("Got Update Req:");

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidateCurrentState(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new ValidatePropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new CreateTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new DeleteTags(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Update(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}

