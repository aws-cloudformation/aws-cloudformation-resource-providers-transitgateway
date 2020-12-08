package software.amazon.ec2.transitgatewaymulticastgroupmember;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.create.Create;
import software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.create.CreatePrecheck;
import software.amazon.ec2.transitgatewaymulticastgroupmember.workflow.read.Read;

public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {

        logger.log("CREATE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        logger.log(this.getClass().getSimpleName());
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new CreatePrecheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Create(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
