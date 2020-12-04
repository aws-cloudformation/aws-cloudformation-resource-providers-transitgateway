package software.amazon.ec2.transitgatewaymulticastgroup;

import software.amazon.ec2.transitgatewaymulticastgroup.workflow.create.CreateGroupMember;
import software.amazon.ec2.transitgatewaymulticastgroup.workflow.create.CreateGroupSource;
import software.amazon.ec2.transitgatewaymulticastgroup.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new CreateGroupMember(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new CreateGroupSource(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
