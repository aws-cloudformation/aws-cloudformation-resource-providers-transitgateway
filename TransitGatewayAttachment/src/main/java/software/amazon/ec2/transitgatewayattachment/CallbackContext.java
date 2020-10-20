package software.amazon.ec2.transitgatewayattachment;

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
    private boolean deleteStarted;
}
