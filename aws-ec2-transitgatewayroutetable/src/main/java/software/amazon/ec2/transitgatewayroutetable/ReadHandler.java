package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;

import software.amazon.cloudformation.proxy.ProxyClient;

import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
public class ReadHandler extends  BaseHandlerStd {

    private Logger logger;

    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        ResourceModel resourceModel = request.getDesiredResourceState();

        if (resourceModel == null || StringUtils.isNullOrEmpty(resourceModel.getTransitGatewayRouteTableId())) {
            return ProgressEvent.failed(resourceModel, callbackContext, HandlerErrorCode.InvalidRequest,
                    "Transit Gateway Route Table ID cannot be empty");
        }

        return proxy.initiate("AWS-EC2-TransitGatewayRouteTable::Read", proxyClient, resourceModel, callbackContext)
                .translateToServiceRequest((transitGatewayRouteTable) -> Translator.translateToReadRequest(
                        resourceModel.getTransitGatewayRouteTableId()))
                .makeServiceCall((awsRequest, client) -> describeTransitGatewayRouteTablesResponse(proxyClient,
                        resourceModel.getTransitGatewayRouteTableId(), awsRequest))
                .handleError(
                        (awsRequest, exception, client, model, context) -> handleError(awsRequest, exception, client,
                                model, context, logger))
                .done(awsResponse -> ProgressEvent.defaultSuccessHandler(
                        Translator.translateFromReadResponse(awsResponse)));
    }

    /**
     * Describe Transit Gateway Route Table Call Chain. This method reads a Route Table and also waits for Stabilization
     * @param proxyClient
     * @param transitGatewayRouteTableId
     * @param awsRequest
     * @return Returns DescribeTransitGatewayRouteTablesResponse once the read is complete.
     */
    private DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse(
            final ProxyClient<Ec2Client> proxyClient, final String transitGatewayRouteTableId,
            final DescribeTransitGatewayRouteTablesRequest awsRequest) {
        try {
            DescribeTransitGatewayRouteTablesResponse response = proxyClient.injectCredentialsAndInvokeV2(awsRequest,
                    proxyClient.client()::describeTransitGatewayRouteTables);
            final String state = response.transitGatewayRouteTables().get(0).stateAsString();
            logger.log(
                    String.format("Reading Transit Route Table Id  %s state is %s", transitGatewayRouteTableId, state));
            return response;
        } catch (Exception e) {
            if (getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND) || getErrorCode(e).equals(
                    BaseHandlerStd.INVALID_ROUTE_NOT_FOUND)) {
                logger.log(String.format(" Read Handler Error Code %s", getErrorCode(e)));
                throw new CfnNotFoundException(ResourceModel.TYPE_NAME, transitGatewayRouteTableId);
            }
            throw new CfnInvalidRequestException(e);
        }
    }

}
