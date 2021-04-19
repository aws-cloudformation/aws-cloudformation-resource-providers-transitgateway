// This is a generated file. Modifications will be overwritten.
package software.amazon.ec2.transitgatewaypeeringattachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ResourceModel {
    @JsonIgnore
    public static final String TYPE_NAME = "AWS::EC2::TransitGatewayPeeringAttachment";

    @JsonIgnore
    public static final String IDENTIFIER_KEY_TRANSITGATEWAYATTACHMENTID = "/properties/TransitGatewayAttachmentId";

    @JsonProperty("TransitGatewayAttachmentId")
    private String transitGatewayAttachmentId;

    @JsonProperty("TransitGatewayId")
    private String transitGatewayId;

    @JsonProperty("PeerTransitGatewayId")
    private String peerTransitGatewayId;

    @JsonProperty("PeerAccountId")
    private String peerAccountId;

    @JsonProperty("PeerRegion")
    private String peerRegion;

    @JsonProperty("Status")
    private PeeringAttachmentStatus status;

    @JsonProperty("State")
    private String state;

    @JsonProperty("CreationTime")
    private String creationTime;

    @JsonProperty("Tags")
    private List<Tag> tags;

    @JsonIgnore
    public JSONObject getPrimaryIdentifier() {
        final JSONObject identifier = new JSONObject();
        if (this.getTransitGatewayAttachmentId() != null) {
            identifier.put(IDENTIFIER_KEY_TRANSITGATEWAYATTACHMENTID, this.getTransitGatewayAttachmentId());
        }

        // only return the identifier if it can be used, i.e. if all components are present
        return identifier.length() == 1 ? identifier : null;
    }

    @JsonIgnore
    public List<JSONObject> getAdditionalIdentifiers() {
        final List<JSONObject> identifiers = new ArrayList<JSONObject>();
        // only return the identifiers if any can be used
        return identifiers.isEmpty() ? null : identifiers;
    }
}
