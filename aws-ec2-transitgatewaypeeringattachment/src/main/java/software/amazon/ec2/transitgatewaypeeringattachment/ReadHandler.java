package software.amazon.ec2.transitgatewaypeeringattachment;

import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.ValidCurrentStateCheck;

public class ReadHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {
            logger.log("READ HANDLER");
            logger.log(request.getDesiredResourceState().toString());
            logger.log("READ HANDLER");
            logger.log(callbackContext.toString());
            return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                    .then(new ValidCurrentStateCheck(proxy, request, callbackContext, proxyClient, logger)::run)
                    .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
