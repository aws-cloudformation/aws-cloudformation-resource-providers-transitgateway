package software.amazon.ec2.transitgatewaypeeringattachment;

import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.create.Create;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {
        logger.log("CREATE HANDLER");
        logger.log(request.getDesiredResourceState().toString());
        logger.log("CREATE HANDLER");
        if(callbackContext.getAttempts() == 0 &&  (request.getDesiredResourceState().getTransitGatewayAttachmentId() != null)) throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property.");
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new Create(proxy, request, callbackContext, proxyClient, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
