package com.aws.ec2.transitgateway.workflow.update;

import com.aws.ec2.transitgateway.AbstractTestBase;
import com.aws.ec2.transitgateway.CallbackContext;
import com.aws.ec2.transitgateway.ResourceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTransitGatewayMulticastDomainRequest;
import software.amazon.awssdk.services.ec2.model.DescribeTransitGatewayMulticastDomainsRequest;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.exceptions.ResourceAlreadyExistsException;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidPropertiesCheckTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<Ec2Client> proxyClient;


    @Test
    public void run_WithoutPrimaryKey() {
        final List<Tag> tags = new ArrayList<>();
        tags.add(MOCKS.tag());

        ResourceModel model = MOCKS.modelWithoutPrimaryIdentifier(tags);
        CallbackContext context =  new CallbackContext();
        ProgressEvent<ResourceModel, CallbackContext> response = new ValidPropertiesCheck(proxy, MOCKS.request(model), context, proxyClient, logger).run(ProgressEvent.defaultInProgressHandler(context, 0, model));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage().contains("Invalid request provided:")).isTrue();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

}
