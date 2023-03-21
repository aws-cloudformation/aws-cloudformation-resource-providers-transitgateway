package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;

public class ListHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {


        final ResourceModel resourceModel = request.getDesiredResourceState();

        return proxy.initiate("AWS-EC2-TransitGatewayRouteTable::List", proxyClient, resourceModel, callbackContext)
                .translateToServiceRequest(Translator::translateToListRequest)
                .makeServiceCall((cbRequest, cbProxyClient) -> cbProxyClient.injectCredentialsAndInvokeV2(cbRequest, cbProxyClient.client()::describeTransitGatewayRouteTables))
                .handleError((awsRequest, exception, client, model, context) -> handleError(awsRequest, exception, client, model, context, logger))
                .done((cbRequest, cbResponse, cbClient, cbModel, cbContext) -> ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .resourceModels(Translator.translateFromListResponse(cbResponse))
                        .status(OperationStatus.SUCCESS)
                        .build());
    }
}
