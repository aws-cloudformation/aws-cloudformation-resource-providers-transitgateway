package com.aws.ec2.transitgatewayattachment.workflow.delete;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ValidateCurrentStateBase;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class ValidateCurrentState extends ValidateCurrentStateBase {
    public ValidateCurrentState(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        if(this.validStates().contains(this.currentState())) {
            return this.progress;
        } else {
            return this.failure();
        }
    }

    @Override
    protected List<String> validStates() {
        List<String> list = new ArrayList<>();
        list.add(TransitGatewayAttachmentState.AVAILABLE.toString());
        list.add(TransitGatewayAttachmentState.DELETING.toString());
        return list;
    }

}
