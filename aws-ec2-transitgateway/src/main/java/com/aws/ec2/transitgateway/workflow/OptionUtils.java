package com.aws.ec2.transitgateway.workflow;

import com.aws.ec2.transitgateway.ResourceModel;
import software.amazon.awssdk.services.ec2.model.ModifyTransitGatewayOptions;
import software.amazon.awssdk.services.ec2.model.TransitGatewayOptions;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRequestOptions;

public class OptionUtils {


    public static TransitGatewayRequestOptions transitGatewayRequestOptions(ResourceModel model){

        if(model == null) return null;

        return TransitGatewayRequestOptions.builder()
                .amazonSideAsn(model.getAmazonSideAsn().longValue())
                .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
                .dnsSupport(model.getDnsSupport())
                .multicastSupport(model.getMulticastSupport())
                .vpnEcmpSupport(model.getVpnEcmpSupport())
                .build();
    }


    public static ModifyTransitGatewayOptions transitGatewayOptions(ResourceModel model){

        if(model == null) return null;

        return ModifyTransitGatewayOptions.builder()
                .autoAcceptSharedAttachments(model.getAutoAcceptSharedAttachments())
                .defaultRouteTableAssociation(model.getDefaultRouteTableAssociation())
                .defaultRouteTablePropagation(model.getDefaultRouteTablePropagation())
                .dnsSupport(model.getDnsSupport())
                .vpnEcmpSupport(model.getVpnEcmpSupport())
                .build();
    }

}
