package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteTableRequest;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import com.amazonaws.util.StringUtils;
import com.google.common.collect.Maps;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayRouteTableResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesResponse;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.Optional;
import java.util.Collections;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
public class CreateHandler extends BaseHandlerStd {
    private final ReadHandler readHandler;
    private software.amazon.cloudformation.proxy.Logger logger;

    public CreateHandler() {
        super();
        readHandler = new ReadHandler();
    }

    @VisibleForTesting
    protected CreateHandler(Ec2Client ec2Client, ReadHandler readHandler) {
        super(ec2Client);
        this.readHandler = readHandler;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {

        final ResourceModel resourceModel = request.getDesiredResourceState();
        this.logger = logger;

        if (resourceModel == null || StringUtils.isNullOrEmpty(resourceModel.getTransitGatewayId())) {
            return ProgressEvent.failed(resourceModel, callbackContext, HandlerErrorCode.InvalidRequest, "Transit Gateway ID cannot be empty");
        }
        Map<String, String> mergedTags = Maps.newHashMap();
        mergedTags.putAll(Optional.ofNullable(request.getDesiredResourceTags()).orElse(Collections.emptyMap()));
        mergedTags.putAll(Optional.ofNullable(request.getSystemTags()).orElse(Collections.emptyMap()));
        logger.log(String.format("[StackId %s ClientRequestToken: %s] Calling Create Transit Gateway RouteTable", request.getStackId(), request.getClientRequestToken()));
        return ProgressEvent.progress(resourceModel, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-EC2-TransitGatewayRouteTable::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToCreateRequest(mergedTags, model))
                                .makeServiceCall((awsRequest, client) -> createTransitGatewayRouteTable(awsRequest, proxyClient, callbackContext, resourceModel))
                                .stabilize((awsRequest, awsResponse, client, model, context) -> stabilizeCreate(proxyClient, callbackContext, logger))
                                .handleError((awsRequest, exception, client, model, context) ->
                                {
                                    if(ACCESS_DENIED_ERROR_CODE.equals(getErrorCode(exception))) {
                                        throw new CfnUnauthorizedTaggingOperationException();
                                    }
                                    return handleError(awsRequest, exception,client, model,
                                            context, logger);
                                })
                                .done(awsResponse -> ProgressEvent.defaultSuccessHandler(resourceModel)));


    }
    /**
     * Create Transit Gateway Route Table Call Chain. This method creates a Transit Gateway  Route Table and also waits for Stabilization
     * @param proxyClient
     * @param callbackContext
     * @param logger
     * @return Returns Success once the stabilization is completed.
     */

    protected static Boolean stabilizeCreate(final ProxyClient<Ec2Client> proxyClient, final CallbackContext callbackContext, final Logger logger) {
        try {
            logger.log(String.format("Stabilizing Transit Gateway Route Table Id %s.", callbackContext.getResourceModel().getTransitGatewayRouteTableId()));
            DescribeTransitGatewayRouteTablesRequest describeTransitGatewayRouteTablesRequest= DescribeTransitGatewayRouteTablesRequest.builder().transitGatewayRouteTableIds(callbackContext.getResourceModel().getTransitGatewayRouteTableId()).build();
            DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse  = proxyClient.injectCredentialsAndInvokeV2(describeTransitGatewayRouteTablesRequest, proxyClient.client()::describeTransitGatewayRouteTables);
            final String state = describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().get(0).stateAsString();
            if(state == null || describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables() == null || describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().size() == 0)
            {
                return null;

            }
            if ("available".equals(state)) {
                logger.log(String.format("Stabilized Transit Gateway Route Table Id %s.", callbackContext.getResourceModel().getTransitGatewayRouteTableId()));
                return true;
            }
        } catch(Exception e) {
            if (!getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND)
                    && !getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_MALFORMED))
                throw e;
        }
        return false;
    }


    /**
     * Create Transit Gateway Route Table Call Chain. This method creates a Transit Gateway  Route Table.
     * @param awsRequest
     * @param proxyClient
     * @param callbackContext
     * @param resourceModel
     * @return Returns CreateTransitGatewayRouteTableResponse once the Transit Gateway  Route Table is created.
     */
    protected static CreateTransitGatewayRouteTableResponse createTransitGatewayRouteTable(final CreateTransitGatewayRouteTableRequest awsRequest, final ProxyClient<Ec2Client> proxyClient, final CallbackContext callbackContext, final ResourceModel resourceModel)
    {
        try
        {
            CreateTransitGatewayRouteTableResponse createTransitGatewayRouteTableResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::createTransitGatewayRouteTable);
            resourceModel.setTransitGatewayRouteTableId(
                    createTransitGatewayRouteTableResponse.transitGatewayRouteTable().transitGatewayRouteTableId());
            callbackContext.setResourceModel(resourceModel);
            return createTransitGatewayRouteTableResponse;
        }
        catch(Exception e) {
            if(!getErrorCode(e).equals(BaseHandlerStd.INCORRECT_STATE))
                throw e;
        }
        return null;
    }
}
