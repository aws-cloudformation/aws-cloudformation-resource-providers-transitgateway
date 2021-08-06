package com.aws.ec2.transitgatewayattachment;

import com.aws.ec2.transitgatewayattachment.ClientBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ClientBuilderTest extends AbstractTestBase {

    @Test
    public void getClient() {
       // Assertions.assertThat(ClientBuilder.getClient().toString().contains("Ec2Client")).isTrue();
    }

    @Test
    public void instance() {
        assertThat(new ClientBuilder().toString().contains("ClientBuilder")).isTrue();
    }

}
