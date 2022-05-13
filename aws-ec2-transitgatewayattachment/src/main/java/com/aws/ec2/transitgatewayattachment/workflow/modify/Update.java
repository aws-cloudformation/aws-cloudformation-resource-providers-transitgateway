package com.aws.ec2.transitgatewayattachment.workflow.modify;

import com.aws.ec2.transitgatewayattachment.CallbackContext;
import com.aws.ec2.transitgatewayattachment.ResourceModel;
import com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper;
import com.aws.ec2.transitgatewayattachment.workflow.read.Read;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayVpcAttachmentRequest;
import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayVpcAttachmentRequestOptions;
import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayVpcAttachmentResponse;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachmentState;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Update {
    AmazonWebServicesClientProxy proxy;
    ResourceHandlerRequest<ResourceModel> request;
    com.aws.ec2.transitgatewayattachment.CallbackContext callbackContext;
    ProxyClient<Ec2Client> client;
    Logger logger;
    ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext>  progress;

    public Update(
            AmazonWebServicesClientProxy proxy,
            ResourceHandlerRequest<ResourceModel> request,
            com.aws.ec2.transitgatewayattachment.CallbackContext callbackContext,
            ProxyClient<Ec2Client> client,
            Logger logger
    ) {
        this.proxy = proxy;
        this.request = request;
        this.callbackContext = callbackContext;
        this.client = client;
        this.logger = logger;
    }

    public ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext>  run(ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext> progress) {
        this.progress = progress;
        return this.proxy.initiate(this.getClass().getSimpleName(), this.client, progress.getResourceModel(), progress.getCallbackContext())
                .translateToServiceRequest(this::translateModelToRequest)
                .makeServiceCall(this::makeServiceCall)
                .stabilize(this::stabilize)
                .handleError(this::handleError)
                .progress();

    }

    private Set<String> listToSet(final List<String> subnets) {
        return CollectionUtils.isEmpty(subnets) ? new HashSet<>() : new HashSet<>(subnets);
    }

    private ModifyTransitGatewayVpcAttachmentRequest translateModelToRequest(ResourceModel model) {
        List<String> oldSubnetIds = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).simpleRequest(model).getSubnetIds();
        List<String> newSubnetIds = model.getSubnetIds();

        List<String> subnetsToAdd = Sets.difference(listToSet(newSubnetIds), listToSet(oldSubnetIds)).immutableCopy().asList();
        List<String> subnetsToRemove = Sets.difference(listToSet(oldSubnetIds), listToSet(newSubnetIds)).immutableCopy().asList();


        logger.log("Options"+model.getVpcId()+","+model.getTransitGatewayId()+","+model.getSubnetIds());
        return ModifyTransitGatewayVpcAttachmentRequest.builder()
                .addSubnetIds(subnetsToAdd)
                .removeSubnetIds(subnetsToRemove)
                .transitGatewayAttachmentId(model.getId())
                .options(this.translateModelToOptions(model))
                .build();
    }

    private ModifyTransitGatewayVpcAttachmentRequestOptions translateModelToOptions(ResourceModel model) {
        //DEFAULT OPTIONS
        String ipv6Support = "disable";
        String applianceModeSupport = "disable";
        String dnsSupport = "enable";
        if(model.getOptions() != null) {
            if(model.getOptions().getIpv6Support() != null) {
                ipv6Support = model.getOptions().getIpv6Support() ;
            }
            if(model.getOptions().getApplianceModeSupport() != null) {
                applianceModeSupport = model.getOptions().getApplianceModeSupport();
            }
            if(model.getOptions().getDnsSupport() != null) {
                dnsSupport = model.getOptions().getDnsSupport();
            }
        }
        return ModifyTransitGatewayVpcAttachmentRequestOptions.builder()
                .ipv6Support(ipv6Support)
                .applianceModeSupport(applianceModeSupport)
                .dnsSupport(dnsSupport)
                .build();
    }

    private ModifyTransitGatewayVpcAttachmentResponse makeServiceCall(ModifyTransitGatewayVpcAttachmentRequest request, ProxyClient<Ec2Client> client) {
        return proxy.injectCredentialsAndInvokeV2(request, client.client()::modifyTransitGatewayVpcAttachment);
    }

    private boolean stabilize (
            ModifyTransitGatewayVpcAttachmentRequest request,
            ModifyTransitGatewayVpcAttachmentResponse response,
            ProxyClient<Ec2Client> client,
            ResourceModel model,
            com.aws.ec2.transitgatewayattachment.CallbackContext context
    ) {
        model.setId(response.transitGatewayVpcAttachment().transitGatewayAttachmentId());
        logger.log(response.transitGatewayVpcAttachment().transitGatewayAttachmentId());
        String currentState = new Read(this.proxy, this.request, this.callbackContext, this.client, this.logger).stateRequest(model).toString();
        return currentState.equals(TransitGatewayAttachmentState.AVAILABLE.toString());
    }

    private ProgressEvent<ResourceModel, com.aws.ec2.transitgatewayattachment.CallbackContext>  handleError(ModifyTransitGatewayVpcAttachmentRequest awsRequest, Exception exception, ProxyClient<Ec2Client> client, ResourceModel model, CallbackContext context) {
        if (exception instanceof ResourceNotFoundException || com.aws.ec2.transitgatewayattachment.workflow.ExceptionMapper.mapToHandlerErrorCode(exception).equals(HandlerErrorCode.NotFound)) {
            return ProgressEvent.defaultSuccessHandler(null);
        } else {
            return ProgressEvent.defaultFailureHandler(exception, ExceptionMapper.mapToHandlerErrorCode(exception));
        }
    }
}
