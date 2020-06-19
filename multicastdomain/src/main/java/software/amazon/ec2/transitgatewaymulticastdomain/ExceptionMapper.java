package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    public static HandlerErrorCode mapToHandlerErrorCode(final AwsServiceException e) {
        switch (e.awsErrorDetails().errorCode()) {
            case "InvalidTransitGatewayIdNotFoundException":
            case "IncorrectStateException":
            case "InvalidTransitGatewayMulticastDomainIdNotFoundException":
            case "InvalidTransitGatewayIdMalformedException":
            case "InvalidTransitGatewayMulticastDomainIdMalformedException":
                return HandlerErrorCode.NotFound;
            case "TransitGatewayMulticastDomainLimitExceededException":
            case "FilterLimitExceededException":
                return HandlerErrorCode.ServiceLimitExceeded;
            case "InvalidParameterValueException":
            case "MissingParameterException":
            case "TagPolicyViolationException":
            case "InvalidPaginationTokenException":
                return HandlerErrorCode.InvalidRequest;
            case "ServiceUnavailableException":
                return HandlerErrorCode.ServiceInternalError;
            case "ServerInternalException":
                return HandlerErrorCode.InternalFailure;
            default:
                return HandlerErrorCode.GeneralServiceException;
        }
    }
}
