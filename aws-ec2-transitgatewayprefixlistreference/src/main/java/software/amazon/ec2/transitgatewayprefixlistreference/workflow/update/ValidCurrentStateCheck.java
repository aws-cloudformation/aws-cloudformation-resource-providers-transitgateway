package software.amazon.ec2.transitgatewayprefixlistreference.workflow.update;

import software.amazon.ec2.transitgatewayprefixlistreference.CallbackContext;
import software.amazon.ec2.transitgatewayprefixlistreference.ResourceModel;
import software.amazon.ec2.transitgatewayprefixlistreference.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayPrefixListReferenceState;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {

    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected List<String> validStates() {
        List<String> list = new ArrayList<>();
        list.add(TransitGatewayPrefixListReferenceState.AVAILABLE.toString());
        return list;
    }
}
