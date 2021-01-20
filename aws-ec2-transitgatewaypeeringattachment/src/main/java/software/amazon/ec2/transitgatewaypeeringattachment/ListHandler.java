package software.amazon.ec2.transitgatewaypeeringattachment;

import software.amazon.ec2.transitgatewaypeeringattachment.workflow.list.List;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

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
