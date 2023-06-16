package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.services.ec2.Ec2Client;

import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTableAssociation;

import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteTableRequest;
import software.amazon.awssdk.services.ec2.model.DeleteTransitGatewayRouteTableResponse;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayRouteTableAssociationsResponse;
import software.amazon.awssdk.services.ec2.model.GetTransitGatewayRouteTableAssociationsRequest;
import software.amazon.awssdk.services.ec2.model.DisassociateTransitGatewayRouteTableRequest;
import com.amazonaws.util.StringUtils;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import java.util.List;


public class DeleteHandler extends BaseHandlerStd {


    private software.amazon.cloudformation.proxy.Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        ResourceModel resourceModel = request.getDesiredResourceState();
        if (resourceModel == null || StringUtils.isNullOrEmpty(resourceModel.getTransitGatewayRouteTableId())) {
            return ProgressEvent.failed(resourceModel, callbackContext, HandlerErrorCode.InvalidRequest, "Transit Gateway Route Table ID cannot be empty");
        }
        logger.log(String.format("[StackId %s ] Calling Delete Transit Gateway RouteTable", request.getStackId()));

        return ProgressEvent.progress(resourceModel, callbackContext)
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger))
                .onSuccess(progress ->
                        proxy.initiate("AWS-EC2-TransitGatewayRouteTable::Delete", proxyClient, resourceModel, callbackContext)
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((awsRequest, client) -> deleteTransitGatewayRouteTable(proxyClient, logger, awsRequest, resourceModel))
                                .stabilize((awsRequest, awsResponse, client, model, context) ->
                                        stabilizeDelete(proxyClient, model))
                                .handleError((awsRequest, exception, client, model, context) -> handleError(awsRequest, exception, client, model, context, logger))
                                .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    /**
     * Delete Transit Gateway Route Table Call Chain. This method deletes a Transit Gateway Route Table and also waits for Stabilization
     * @param proxyClient
     * @param logger
     * @param awsRequest
     * @param model
     * @return Returns Success once the delete is completed.
     */

    private DeleteTransitGatewayRouteTableResponse deleteTransitGatewayRouteTable(final ProxyClient<Ec2Client> proxyClient, final Logger logger, final DeleteTransitGatewayRouteTableRequest awsRequest, final ResourceModel model) {
        try {

            DescribeTransitGatewayRouteTablesRequest describeTransitGatewayRouteTablesRequest = DescribeTransitGatewayRouteTablesRequest.builder().transitGatewayRouteTableIds(model.getTransitGatewayRouteTableId()).build();
            DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = proxyClient.injectCredentialsAndInvokeV2(describeTransitGatewayRouteTablesRequest, proxyClient.client()::describeTransitGatewayRouteTables);
            DeleteTransitGatewayRouteTableResponse response = null;
            final String state = describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().get(0).stateAsString();

            if (state!= null && ("pending".equals(state)
                    || disassociateRouteTable(model.getTransitGatewayRouteTableId(), proxyClient))
            ) {
                logger.log(String.format("Transit Gateway Route Table %s did not stabilize", model.getTransitGatewayRouteTableId()));
            }
            if (state!= null && (!"deleting".equals(state)
                    && !"deleted".equals(state))
            ) {
                response = proxyClient.injectCredentialsAndInvokeV2(awsRequest,
                        proxyClient.client()::deleteTransitGatewayRouteTable);
            }
            return  response;
        } catch(Exception e) {
            if(getErrorCode(e).equals(BaseHandlerStd.INCORRECT_STATE))
                logger.log(getErrorCode(e));
            if (!getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_MALFORMED) &&
                    !getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND)
            ) {
                logger.log(getErrorCode(e));
                throw e;
            }
        }
        return null;
    }



    /**
     * Method to disassociate the specified Transit Gateway Route Table ID
     * @param transitGatewayRouteTableId
     * @param proxyClient
     * @return boolean value indicating the status of transit gateway route table attachment
     */

    private Boolean disassociateRouteTable(final String transitGatewayRouteTableId, final ProxyClient<Ec2Client> proxyClient) {
        try {
            GetTransitGatewayRouteTableAssociationsRequest getTransitGatewayRouteTableAssociationsRequest = GetTransitGatewayRouteTableAssociationsRequest.builder()
                    .transitGatewayRouteTableId(transitGatewayRouteTableId).build();

            GetTransitGatewayRouteTableAssociationsResponse response = proxyClient.injectCredentialsAndInvokeV2(
                    getTransitGatewayRouteTableAssociationsRequest,
                    proxyClient.client()::getTransitGatewayRouteTableAssociations);
            List<TransitGatewayRouteTableAssociation> transitGatewayRouteTableAssociations = null;
            DisassociateTransitGatewayRouteTableRequest disassociateTransitGatewayRouteTableRequest = DisassociateTransitGatewayRouteTableRequest.builder()
                    .build();

            if (response != null && response.hasAssociations()) {
                transitGatewayRouteTableAssociations = response.associations();
            }

            if (transitGatewayRouteTableAssociations != null && !transitGatewayRouteTableAssociations.isEmpty()) {
                for (TransitGatewayRouteTableAssociation association : transitGatewayRouteTableAssociations) {

                    if (association.stateAsString().equals("associated")) {

                        DisassociateTransitGatewayRouteTableRequest.builder()
                                .transitGatewayAttachmentId(association.transitGatewayAttachmentId())
                                .transitGatewayRouteTableId(transitGatewayRouteTableId).
                                build();

                        proxyClient.client()
                                .disassociateTransitGatewayRouteTable(disassociateTransitGatewayRouteTableRequest);
                    }
                }
                return true;
            }

        }
        catch (Exception e) {
            if (!getErrorCode(e).equals(BaseHandlerStd.INVALID_ASSOCIATION_NOT_FOUND) &&
                    !getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND) &&
                    !getErrorCode(e).equals(BaseHandlerStd.INVALID_TRANSIT_GATEWAY_ATTACHMENT_ID_NOT_FOUND)) {
                return false;
            }
        }
        return false;
    }


    /**
     * Method to stabilize the deletion of the specified Transit Gateway Route Table ID
     *
     * @param proxyClient
     * @param model
     * @return boolean value indicating the status of the stabilization
     */
    protected  Boolean stabilizeDelete(final ProxyClient<Ec2Client> proxyClient, final ResourceModel model) {
        try {
            DescribeTransitGatewayRouteTablesRequest describeTransitGatewayRouteTablesRequest = DescribeTransitGatewayRouteTablesRequest.builder().transitGatewayRouteTableIds(model.getTransitGatewayRouteTableId()).build();
            DescribeTransitGatewayRouteTablesResponse describeTransitGatewayRouteTablesResponse = proxyClient.injectCredentialsAndInvokeV2(describeTransitGatewayRouteTablesRequest, proxyClient.client()::describeTransitGatewayRouteTables);

            final String state = describeTransitGatewayRouteTablesResponse.transitGatewayRouteTables().get(0).stateAsString();
            logger.log(String.format(" Stabilization state %s", state));

            if ("deleted".equals(state)) {
                return true;
            }
            return false;
        } catch (Exception e)
        {
            logger.log(String.format(" Stabilization Error Code %s",getErrorCode(e)));
            if (getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_TABLE_ID_NOT_FOUND) || getErrorCode(e).equals(BaseHandlerStd.INVALID_ROUTE_NOT_FOUND)
            ) {
                return true;
            }
            return false;
        }
    }
}
