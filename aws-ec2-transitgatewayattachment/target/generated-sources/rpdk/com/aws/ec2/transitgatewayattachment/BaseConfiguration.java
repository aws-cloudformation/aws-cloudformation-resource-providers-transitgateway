// This is a generated file. Modifications will be overwritten.
package com.aws.ec2.transitgatewayattachment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

@Data
@AllArgsConstructor
public abstract class BaseConfiguration {

    protected final String schemaFilename;

    public JSONObject resourceSchemaJSONObject() {
        return new JSONObject(new JSONTokener(this.getClass().getClassLoader().getResourceAsStream(schemaFilename)));
    }

    /**
     * Providers should override this method if their resource has a 'Tags' property to define resource-level tags
     * @return
     */
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        return null;
    }
}
