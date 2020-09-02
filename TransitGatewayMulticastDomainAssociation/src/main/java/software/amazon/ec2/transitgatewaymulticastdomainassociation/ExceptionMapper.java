package software.amazon.ec2.transitgatewaymulticastdomainassociation;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    public static HandlerErrorCode mapToHandlerErrorCode(final AwsServiceException e) {
        switch (e.awsErrorDetails().errorCode()) {
            case "InvalidTransitGatewayID.NotFound":
            case "InvalidTransitGatewayMulticastDomainId.NotFound":
            case "InvalidTransitGatewayID.Malformed":
            case "CfnNotFoundException":
            case "ResourceNotFoundException":
                return HandlerErrorCode.NotFound;
            case "TransitGatewayMulticastDomainLimitExceeded":
            case "FilterLimitExceeded":
                return HandlerErrorCode.ServiceLimitExceeded;
            case "InvalidParameterValue":
            case "MissingParameter":
            case "TagPolicyViolation":
            case "InvalidPaginationToken":
                return HandlerErrorCode.InvalidRequest;
            case "ServiceUnavailable":
                return HandlerErrorCode.ServiceInternalError;
            case "ServerInternal":
                return HandlerErrorCode.InternalFailure;
            default:
                return HandlerErrorCode.GeneralServiceException;
        }
    }
}
