package com.aws.ec2.transitgatewayvpcattachment;

import com.aws.ec2.transitgatewayvpcattachment.Configuration;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ConfigurationTest extends AbstractTestBase {

    @Test
    public void resourceSchemaJSONObject() {

        Configuration config = new Configuration();
        JSONObject json = config.resourceSchemaJSONObject();
        assertThat(json.get("sourceUrl").toString().contains("github.com/aws-cloudformation/aws-cloudformation-resource-providers")).isTrue();
    }

}
