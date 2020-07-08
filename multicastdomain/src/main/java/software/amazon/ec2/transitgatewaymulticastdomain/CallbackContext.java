package software.amazon.ec2.transitgatewaymulticastdomain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class CallbackContext {
    private boolean createStarted;
    private boolean deleteStarted;

    private List<Tag> tagsToCreate;
    private List<Tag> tagsToDelete;
}
