package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import com.aws.ec2.transitgatewayattachment.workflow.update.CreateTags;
import com.aws.ec2.transitgatewayattachment.workflow.update.DeleteTags;
import com.aws.ec2.transitgatewayattachment.workflow.update.ValidateCurrentState;
import com.aws.ec2.transitgatewayattachment.workflow.update.ValidatePropertiesCheck;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new ValidatePropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new ValidateCurrentState(proxy, request, callbackContext, proxyClient,logger)::run)
            .then(new CreateTags(proxy, request, callbackContext, logger)::run)
            .then(new DeleteTags(proxy, request, callbackContext, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
