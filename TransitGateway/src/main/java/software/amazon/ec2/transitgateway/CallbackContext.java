package software.amazon.ec2.transitgateway;

import lombok.*;
import software.amazon.cloudformation.proxy.StdCallbackContext;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CallbackContext extends StdCallbackContext {
    private boolean actionStarted;
    private int remainingRetryCount;
    private boolean updateFailed;
}
