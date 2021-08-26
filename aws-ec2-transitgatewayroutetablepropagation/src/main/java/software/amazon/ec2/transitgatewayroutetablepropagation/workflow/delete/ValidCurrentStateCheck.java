package software.amazon.ec2.transitgatewayroutetablepropagation.workflow.delete;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayPropagationState;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.ec2.transitgatewayroutetablepropagation.CallbackContext;
import software.amazon.ec2.transitgatewayroutetablepropagation.ResourceModel;
import software.amazon.ec2.transitgatewayroutetablepropagation.workflow.ValidCurrentStateCheckBase;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {
    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request,
                                  CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }


    @Override
    protected List<String> validStates() {
        List<String> list = new ArrayList<>();
        list.add(TransitGatewayPropagationState.ENABLED.toString());
        list.add(TransitGatewayPropagationState.DISABLING.toString());
        return list;
    }
}
