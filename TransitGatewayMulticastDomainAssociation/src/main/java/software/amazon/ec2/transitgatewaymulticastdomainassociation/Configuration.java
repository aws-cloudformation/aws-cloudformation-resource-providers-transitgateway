package software.amazon.ec2.transitgatewaymulticastdomainassociation;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-ec2-transitgatewaymulticastdomainassociation.json");
    }

    public JSONObject resourceSchemaJSONObject() {
        return new JSONObject(new JSONTokener(this.getClass().getClassLoader().getResourceAsStream(schemaFilename)));
    }



}
