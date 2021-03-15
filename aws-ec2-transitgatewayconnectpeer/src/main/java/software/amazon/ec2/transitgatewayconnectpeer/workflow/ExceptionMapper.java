package software.amazon.ec2.transitgatewayconnectpeer.workflow;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ExceptionMapper {
    public static HandlerErrorCode mapToHandlerErrorCode(Exception e) {
        if (e instanceof AwsServiceException) {
            AwsServiceException awsServiceException = (AwsServiceException) e;
            String errorCode = awsServiceException.awsErrorDetails().errorCode();

            if(errorCode.contains("NotFound")) {
                return HandlerErrorCode.NotFound;
            } else if(errorCode.contains("LimitExceeded")) {
                return HandlerErrorCode.ServiceLimitExceeded;
            } else if (listContains(invalidRequestCodes(),  errorCode)) {
                return HandlerErrorCode.InvalidRequest;
            } else if(errorCode.equals("ServiceUnavailable")) {
                return HandlerErrorCode.ServiceInternalError;
            } else if(errorCode.equals("ServerInternal")) {
                return HandlerErrorCode.InternalFailure;
            } else {
                return HandlerErrorCode.GeneralServiceException;
            }
        } else {
            if(e instanceof ResourceNotFoundException) {
                return HandlerErrorCode.NotFound;
            } else {
                return HandlerErrorCode.GeneralServiceException;
            }
        }
    }

    private static List<String> invalidRequestCodes() {
        List<String> codes = new ArrayList<>();
        codes.add("Invalid");
        codes.add("Missing");
        codes.add("Violation");
        codes.add("Malformed");
        return codes;
    }

    public static boolean listContains(List<String> haystack, String needle) {
        return haystack.stream().filter(k -> needle.contains(k)).collect(Collectors.toList()).size() > 0;
    }
}
