package software.amazon.ec2.transitgatewayroutetablepropagation;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.delete.Delete;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.delete.ValidCurrentStateCheck;

public class DeleteHandler extends BaseHandlerStd {
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {
        logger.log("DELETE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        logger.log("DELETE HANDLER");
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(new Delete(proxy, request, callbackContext, proxyClient, logger)::run)
                .then(_progress -> ProgressEvent.defaultSuccessHandler(null)); //After delete successfully completes we want to return a null response because the model no longer exits
    }
}
