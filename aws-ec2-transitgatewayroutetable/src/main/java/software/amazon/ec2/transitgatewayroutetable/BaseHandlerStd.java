package software.amazon.ec2.transitgatewayroutetable;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Ec2Request;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
import software.amazon.cloudformation.exceptions.CfnInvalidCredentialsException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.cloudformation.proxy.ProxyClient;

import java.util.Arrays;
import static java.util.Objects.requireNonNull;

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

    protected static final String TRANSIT_GATEWAY_STATE_FAILED_STABILIZE = "Failed to stabilize Transit Gateway Route Table with id %s.";

    private final Ec2Client ec2Client;

    protected BaseHandlerStd() {
        this(ClientBuilder.getClient());
    }

    protected BaseHandlerStd(Ec2Client ec2Client) {
        this.ec2Client = requireNonNull(ec2Client);
    }

    private Ec2Client getEC2Client() {
        return ec2Client;
    }

    protected static String UNAUTHORIZED_OPERATION= "UnauthorizedOperation";
    protected static String INVALID_TRANSIT_GATEWAY_STATE = "InvalidTransitGatewayState";
    protected static String INVALID_TRANSIT_GATEWAY_ID = "InvalidTransitGatewayID.NotFound";
    protected static String TRANSIT_GATEWAY_LIMIT_EXCEEDED = "TransitGatewayLimitExceeded";
    protected static String AUTH_FAILURE = "AuthFailure";
    protected static String INVALID_PARAMETER_VALUE = "InvalidParameterValue";
    protected static String THROTTLING = "RequestLimitExceeded";
    protected static String INVALID_ID = "InvalidID";
    protected static String INVALID_REQUEST = "InvalidRequest";
    protected static final String INVALID_ASSOCIATION_NOT_FOUND = "InvalidAssociation.NotFound";
    protected static final String INVALID_ROUTE_TABLE_ID_NOT_FOUND = "InvalidRouteTableID.NotFound";

    protected static final String INVALID_ROUTE_NOT_FOUND = "InvalidRoute.NotFound";
    protected static final String INVALID_TRANSIT_GATEWAY_ATTACHMENT_ID_NOT_FOUND = "InvalidTransitGatewayAttachmentID.NotFound";
    protected static final String INCORRECT_STATE = "IncorrectState";

    protected static final String INVALID_ROUTE_TABLE_ID_MALFORMED = "InvalidRouteTableId.Malformed";
    @Override
    public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        return handleRequest(
                proxy,
                request,
                callbackContext != null ? callbackContext : new CallbackContext(),
                proxy.newProxy(this::getEC2Client),
                logger
        );
    }
    protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<Ec2Client> proxyClient,
            final Logger logger);

    protected ProgressEvent<ResourceModel, CallbackContext> handleError(
            final Ec2Request request,
            final Exception e,
            final ProxyClient<Ec2Client> proxyClient,
            final ResourceModel resourceModel,
            final CallbackContext callbackContext,
            final Logger logger
           ) {

        BaseHandlerException ex = null;

        logger.log(String.format("Handling Error %s for Request [%s] during operation %s", e.toString(), request.toString(), this.getClass().getSimpleName()));
        logger.log(Arrays.toString(e.getStackTrace()));
        if (UNAUTHORIZED_OPERATION.equals(getErrorCode(e))) {
            ex = new CfnAccessDeniedException(e);
        } else if (INVALID_TRANSIT_GATEWAY_STATE.equals(getErrorCode(e))) {
            ex = new CfnInvalidRequestException(e);
        } else if(INVALID_REQUEST.equals(getErrorCode(e))){
            ex = new CfnInvalidRequestException(e);
        }
        else if (INVALID_PARAMETER_VALUE.equals(getErrorCode(e))) {
            ex = new CfnInvalidRequestException(e);
        } else if (INVALID_TRANSIT_GATEWAY_ID.equals(getErrorCode(e))) {
            ex = new CfnNotFoundException(e);
        } else if (INVALID_ID.equals(getErrorCode(e))){
            ex = new CfnNotFoundException(e);
        } else if (TRANSIT_GATEWAY_LIMIT_EXCEEDED.equals(getErrorCode(e))) {
            ex = new CfnServiceLimitExceededException(e);
        } else if (AUTH_FAILURE.equals(getErrorCode(e))) {
            ex = new CfnInvalidCredentialsException(e);
        } else if (THROTTLING.equals(getErrorCode(e))){
            ex = new CfnThrottlingException(e);
        } else {
            ex = new CfnGeneralServiceException(e);
        }
        return ProgressEvent.failed(resourceModel, callbackContext, ex.getErrorCode(), ex.getMessage());
    }

    protected static String getErrorCode(Exception e) {
        if (e instanceof AwsServiceException) {
            return ((AwsServiceException) e).awsErrorDetails().errorCode();
        }
        return e.getMessage();
    }

}
