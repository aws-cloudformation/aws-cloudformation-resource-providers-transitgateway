package software.amazon.ec2.transitgatewayprefixlistreference;

import software.amazon.ec2.transitgatewayprefixlistreference.workflow.read.Read;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.update.Update;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.update.ValidCurrentStateCheck;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.update.ValidPropertiesCheck;
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
            .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new ValidPropertiesCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Update(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
