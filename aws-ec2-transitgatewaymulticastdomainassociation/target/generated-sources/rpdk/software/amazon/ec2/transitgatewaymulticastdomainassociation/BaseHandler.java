// This is a generated file. Modifications will be overwritten.
package software.amazon.ec2.transitgatewaymulticastdomainassociation;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public abstract class BaseHandler<CallbackT> {

    public abstract ProgressEvent<ResourceModel, CallbackT> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackT callbackContext,
        final Logger logger);

}
