package software.amazon.ec2.transitgatewaymulticastdomain;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    private ExceptionMapper() {
    }

    public static HandlerErrorCode mapToHandlerErrorCode(final AwsServiceException e) {
        switch (e.awsErrorDetails().errorCode()) {
            case "InvalidTransitGatewayIdNotFoundException":
            case "InvalidTransitGatewayIdMalformedException":
            case "IncorrectStateException":
            case "InvalidTransitGatewayMulticastDomainIdNotFoundException":
            case "InvalidTransitGatewayMulticastDomainIdMalformedException":
                return HandlerErrorCode.NotFound;
            case "TransitGatewayMulticastDomainLimitExceededException":
                return HandlerErrorCode.ServiceLimitExceeded;
            case "InvalidParameterValueException":
            case "MissingParameterException":
            case "TagPolicyViolationException":
                return HandlerErrorCode.InvalidRequest;
            case "ServerInternalException":
            case "ServiceUnavailableException":
                return HandlerErrorCode.ServiceInternalError;
            default:
                return HandlerErrorCode.GeneralServiceException;
        }
    }
}
