package software.amazon.ec2.transitgatewayroutetable;

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

public class CreateHandler extends BaseHandlerStd {
    private final ReadHandler readHandler;
    private int EVENTUAL_CONSISTENCY_DELAY_SECONDS = 15;
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

        return ProgressEvent.progress(resourceModel, callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-EC2-TransitGatewayRouteTable::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToCreateRequest(mergedTags, model))
                                .makeServiceCall((awsRequest, client) -> {
                                    CreateTransitGatewayRouteTableResponse createTransitGatewayRouteTableResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, client.client()::createTransitGatewayRouteTable);
                                    resourceModel.setTransitGatewayRouteTableId(
                                            createTransitGatewayRouteTableResponse.transitGatewayRouteTable().transitGatewayRouteTableId());
                                    callbackContext.setResourceModel(resourceModel);
                                    logger.log(String.format("Created Transit Gateway Route Table Id %s.", resourceModel.getTransitGatewayRouteTableId()));
                                    return createTransitGatewayRouteTableResponse;
                                })
                                .stabilize((awsRequest, awsResponse, client, model, context) -> stabilizeCreate(proxyClient, callbackContext, logger))
                                .handleError((awsRequest, exception, client, model, context) -> handleError(awsRequest, exception, client, model, context, logger))
                                .done((awsRequest, response, client, Model, context) -> {
                                    resourceModel.setTransitGatewayRouteTableId(response.transitGatewayRouteTable().transitGatewayRouteTableId());
                                    return ProgressEvent.progress(resourceModel, context);
                                })
                )
                .then(progress -> {
                   if (progress.getCallbackContext().isPropagationDelay()) {
                        logger.log("Propagation delay completed");
                        return ProgressEvent.progress(progress.getResourceModel(), progress.getCallbackContext());
                    }
                    progress.getCallbackContext().setPropagationDelay(true);
                    callbackContext.setItFirstTime(false);
                    logger.log("Setting propagation delay");
                    return ProgressEvent.defaultInProgressHandler(progress.getCallbackContext(),
                            EVENTUAL_CONSISTENCY_DELAY_SECONDS, progress.getResourceModel());
                })
                .then(progress -> readHandler.handleRequest(proxy, request, callbackContext, proxyClient, logger));
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
            if ("available".equals(describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().get(0).stateAsString())) {
                logger.log(String.format("Stabilized Transit Gateway Route Table Id %s.", callbackContext.getResourceModel().getTransitGatewayRouteTableId()));
                return true;
            }
        } catch(Exception e){
            if (getErrorCode(e).equals(BaseHandlerStd.THROTTLING)) return false;
            if (getErrorCode(e).equals(BaseHandlerStd.TRANSIT_GATEWAY_STATE_FAILED_STABILIZE)) return false; // stabilizing if not found
            logger.log(getErrorCode(e));
            throw e;
        }
        return false;
    }
}
