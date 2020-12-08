package software.amazon.ec2.transitgatewayattachment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {

    private ExceptionMapper() {
    }

    public static HandlerErrorCode mapToHandlerErrorCode(final AwsServiceException e) {
        switch (e.awsErrorDetails().errorCode()) {
            case "InvalidTransitGatewayAttachmentID.NotFound": return HandlerErrorCode.NotFound;
            case "InvalidTransitGatewayAttachmentID.Malformed": return HandlerErrorCode.NotFound;
            case "CfnNotFoundException": return HandlerErrorCode.NotFound;
            case "ResourceNotFoundException": return HandlerErrorCode.NotFound;
            case "TransitGatewayLimitExceeded": return HandlerErrorCode.ServiceLimitExceeded;
            case "FilterLimitExceeded": return HandlerErrorCode.ServiceLimitExceeded;
            case "InvalidParameterValue":     return HandlerErrorCode.InvalidRequest;
            case "MissingParameter":     return HandlerErrorCode.InvalidRequest;
            case "TagPolicyViolation":     return HandlerErrorCode.InvalidRequest;
            case "InvalidPaginationToken":     return HandlerErrorCode.InvalidRequest;
            case "ServiceUnavailable":    return HandlerErrorCode.ServiceInternalError;
            case "ServerInternal":  return HandlerErrorCode.InternalFailure;
            default: return HandlerErrorCode.GeneralServiceException;


        }

    }

}
