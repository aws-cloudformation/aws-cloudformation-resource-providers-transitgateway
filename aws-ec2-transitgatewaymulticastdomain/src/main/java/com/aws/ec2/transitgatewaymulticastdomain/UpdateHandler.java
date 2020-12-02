package com.aws.ec2.transitgatewaymulticastdomain;

import com.aws.ec2.transitgatewaymulticastdomain.workflow.read.Read;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.update.CreateTags;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.update.DeleteTags;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.update.ValidCurrentStateCheck;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.update.ValidPropertiesCheck;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class UpdateHandler extends BaseHandlerStd {
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {
        logger.log("UPDATE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        logger.log("UPDATE HANDLER");

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new ValidPropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new CreateTags(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new DeleteTags(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }


}
