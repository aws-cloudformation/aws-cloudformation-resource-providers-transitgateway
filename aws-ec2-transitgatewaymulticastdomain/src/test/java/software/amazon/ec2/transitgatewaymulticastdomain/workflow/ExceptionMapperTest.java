package software.amazon.ec2.transitgatewaymulticastdomain.workflow;

import software.amazon.ec2.transitgatewaymulticastdomain.AbstractTestBase;
import software.amazon.ec2.transitgatewaymulticastdomain.ResourceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.HandlerErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExceptionMapperTest extends AbstractTestBase {



    @Test
    public void instance() {
        assertThat(new ExceptionMapper().toString().contains("ExceptionMapper")).isTrue();
    }

    @Test
    public void handleRequest_ResourceNotFound() {
        this.errorTest("Something.NotFound", HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequest_LimitExceeded() {
        this.errorTest("Something.LimitExceeded", HandlerErrorCode.ServiceLimitExceeded);
    }

    @Test
    public void handleRequest_InvalidRequest() {
        List<String> list = new ArrayList<>();
        list.add("InvalidParameterValue");
        list.add("MissingParameter");
        list.add("TagPolicyViolation");
        list.add("InvalidPaginationToken");
        list.add("Malformed");
        Random rand = new Random();
        String errorCode = list.get(rand.nextInt(list.size()));

        this.errorTest(errorCode, HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequest_ServiceInternalError() {
        this.errorTest("ServiceUnavailable", HandlerErrorCode.ServiceInternalError);
    }

    @Test
    public void handleRequest_InternalFailure() {
        this.errorTest("ServerInternal", HandlerErrorCode.InternalFailure);
    }

    @Test
    public void handleRequest_GeneralServiceException() {
        this.errorTest("AnyOtherException", HandlerErrorCode.GeneralServiceException);
    }

    @Test
    public void other_Error() {
        IllegalStateException exception = new IllegalStateException("Error occurred");
        assertThat(ExceptionMapper.mapToHandlerErrorCode(exception)).isEqualTo(HandlerErrorCode.GeneralServiceException);
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException(ResourceModel.TYPE_NAME, "Could not find");
        assertThat(ExceptionMapper.mapToHandlerErrorCode(exception)).isEqualTo(HandlerErrorCode.NotFound);
    }

    private void errorTest(String errorCode, HandlerErrorCode expectedException) {
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode(errorCode).build();
        final AwsServiceException awsServiceException = AwsServiceException.builder().awsErrorDetails(awsErrorDetails).build();
        assertThat(ExceptionMapper.mapToHandlerErrorCode(awsServiceException)).isEqualTo(expectedException);
    }

}
