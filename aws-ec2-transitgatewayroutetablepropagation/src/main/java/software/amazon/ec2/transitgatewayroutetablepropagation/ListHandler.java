package software.amazon.ec2.transitgatewayroutetablepropagation;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.list.List;
import software.amazon.awssdk.services.ec2.Ec2Client;


public class ListHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient <Ec2Client> proxyClient,
            final Logger logger) {
        logger.log("LIST HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        logger.log("LIST HANDLER");
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(new List(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
