package com.aws.ec2.transitgatewayattachment;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.*;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  @Override
  public final ProgressEvent<com.aws.ec2.transitgatewayattachment.ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<com.aws.ec2.transitgatewayattachment.ResourceModel> request,
          final CallbackContext callbackContext,
          final Logger logger) {
    CallbackContext context = callbackContext != null ? callbackContext : new CallbackContext();
    context.setAttempts(context.getAttempts() + 1);
    return handleRequest(
            proxy,
            request,
            context,
            proxy.newProxy(ClientBuilder::getClient),
            logger
    );
  }
  protected abstract ProgressEvent<com.aws.ec2.transitgatewayattachment.ResourceModel, CallbackContext> handleRequest(
          final AmazonWebServicesClientProxy proxy,
          final ResourceHandlerRequest<ResourceModel> request,
          final CallbackContext callbackContext,
          final ProxyClient<Ec2Client> proxyClient,
          final Logger logger);
}
