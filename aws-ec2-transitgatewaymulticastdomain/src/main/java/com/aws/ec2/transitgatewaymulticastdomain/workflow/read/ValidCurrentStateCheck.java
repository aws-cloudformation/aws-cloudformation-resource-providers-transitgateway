package com.aws.ec2.transitgatewaymulticastdomain.workflow.read;

import com.aws.ec2.transitgatewaymulticastdomain.CallbackContext;
import com.aws.ec2.transitgatewaymulticastdomain.ResourceModel;
import com.aws.ec2.transitgatewaymulticastdomain.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayMulticastDomainState;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {

    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected List<String> invalidStates() {
        List<String> list = new ArrayList<>();
        list.add(TransitGatewayMulticastDomainState.DELETED.toString());
        return list;
    }
}
