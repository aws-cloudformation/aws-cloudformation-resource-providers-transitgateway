// This is a generated file. Modifications will be overwritten.
package software.amazon.ec2.transitgatewaymulticastdomainassociation;

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
    public static final String TYPE_NAME = "AWS::EC2::TransitGatewayMulticastDomainAssociation";

    @JsonIgnore
    public static final String IDENTIFIER_KEY_TRANSITGATEWAYMULTICASTDOMAINID = "/properties/TransitGatewayMulticastDomainId";
    @JsonIgnore
    public static final String IDENTIFIER_KEY_TRANSITGATEWAYATTACHMENTID = "/properties/TransitGatewayAttachmentId";
    @JsonIgnore
    public static final String IDENTIFIER_KEY_SUBNETID = "/properties/SubnetId";

    @JsonProperty("TransitGatewayMulticastDomainId")
    private String transitGatewayMulticastDomainId;

    @JsonProperty("TransitGatewayAttachmentId")
    private String transitGatewayAttachmentId;

    @JsonProperty("ResourceId")
    private String resourceId;

    @JsonProperty("ResourceType")
    private String resourceType;

    @JsonProperty("State")
    private String state;

    @JsonProperty("SubnetId")
    private String subnetId;

    @JsonIgnore
    public JSONObject getPrimaryIdentifier() {
        final JSONObject identifier = new JSONObject();
        if (this.getTransitGatewayMulticastDomainId() != null) {
            identifier.put(IDENTIFIER_KEY_TRANSITGATEWAYMULTICASTDOMAINID, this.getTransitGatewayMulticastDomainId());
        }

        if (this.getTransitGatewayAttachmentId() != null) {
            identifier.put(IDENTIFIER_KEY_TRANSITGATEWAYATTACHMENTID, this.getTransitGatewayAttachmentId());
        }

        if (this.getSubnetId() != null) {
            identifier.put(IDENTIFIER_KEY_SUBNETID, this.getSubnetId());
        }

        // only return the identifier if it can be used, i.e. if all components are present
        return identifier.length() == 3 ? identifier : null;
    }

    @JsonIgnore
    public List<JSONObject> getAdditionalIdentifiers() {
        final List<JSONObject> identifiers = new ArrayList<JSONObject>();
        // only return the identifiers if any can be used
        return identifiers.isEmpty() ? null : identifiers;
    }
}
