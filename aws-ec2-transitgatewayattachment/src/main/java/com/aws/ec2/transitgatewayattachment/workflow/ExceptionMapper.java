package com.aws.ec2.transitgatewayattachment.workflow;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

public final class ExceptionMapper {
    public static HandlerErrorCode mapToHandlerErrorCode(Exception e) {
        if (e instanceof AwsServiceException) {

            AwsServiceException awsServiceException = (AwsServiceException) e;
            if(awsServiceException.awsErrorDetails().errorCode().contains("NotFound") || awsServiceException.awsErrorDetails().errorCode().contains(".Malformed")) {
                return HandlerErrorCode.NotFound;
            }
            else if(awsServiceException.awsErrorDetails().errorCode().contains("LimitExceeded")) {
                return HandlerErrorCode.ServiceLimitExceeded;
            }

            switch (awsServiceException.awsErrorDetails().errorCode()) {
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
        } else {
            return HandlerErrorCode.GeneralServiceException;
        }
    }
}
