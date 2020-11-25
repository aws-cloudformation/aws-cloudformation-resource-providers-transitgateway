package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.workflow.create.Create;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.*;

public class CreateHandler extends BaseHandlerStd {

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<Ec2Client> proxyClient,
        final Logger logger) {

        ResourceModel model = request.getDesiredResourceState();
        if(model.getId() != null) throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property.");

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(new Create(proxy, request, callbackContext, logger)::run)
            .then(new Read(proxy, request, callbackContext, proxyClient, logger)::run);
    }
}
