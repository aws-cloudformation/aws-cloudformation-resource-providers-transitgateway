package software.amazon.ec2.transitgatewaymulticastdomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CallbackContext {
    private boolean actionStarted;
    private int remainingRetryCount;
}
