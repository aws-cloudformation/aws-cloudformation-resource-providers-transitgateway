package software.amazon.ec2.transitgatewayconnect;

import software.amazon.ec2.transitgatewayconnect.workflow.delete.Delete;
import software.amazon.ec2.transitgatewayconnect.workflow.delete.ValidCurrentStateCheck;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class DeleteHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Delete(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(_progress -> ProgressEvent.defaultSuccessHandler(null)); //After delete successfully completes we want to return a null response because the model no longer exits
    }
}
