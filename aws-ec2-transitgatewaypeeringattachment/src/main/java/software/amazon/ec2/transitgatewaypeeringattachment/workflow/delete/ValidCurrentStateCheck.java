package software.amazon.ec2.transitgatewaypeeringattachment.workflow.delete;

import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.ec2.transitgatewaypeeringattachment.CallbackContext;
import software.amazon.ec2.transitgatewaypeeringattachment.ResourceModel;
import software.amazon.ec2.transitgatewaypeeringattachment.workflow.ValidCurrentStateCheckBase;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.PeeringAttachmentStatus;
import software.amazon.cloudformation.proxy.*;

import java.util.ArrayList;
import java.util.List;

public class ValidCurrentStateCheck extends ValidCurrentStateCheckBase {

    public ValidCurrentStateCheck(AmazonWebServicesClientProxy proxy, ResourceHandlerRequest<ResourceModel> request, CallbackContext callbackContext, ProxyClient<Ec2Client> client, Logger logger) {
        super(proxy, request, callbackContext, client, logger);
    }

    @Override
    protected ProgressEvent<ResourceModel, CallbackContext> validate() {
        if(this.validStates().contains(this.currentState()) || this.callbackContext.getAttempts() > 0) {
            return this.progress;
        } else {
            return this.failure();
        }
    }

    @Override
    protected List<String> validStates() {
        List<String> list = new ArrayList<>();
        list.add(TransitGatewayAttachmentState.AVAILABLE.toString());
        list.add(TransitGatewayAttachmentState.PENDING_ACCEPTANCE.toString());
        list.add(TransitGatewayAttachmentState.DELETING.toString());
        return list;
    }

}
