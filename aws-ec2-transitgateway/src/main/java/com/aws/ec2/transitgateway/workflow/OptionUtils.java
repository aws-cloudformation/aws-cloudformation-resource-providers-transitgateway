package com.aws.ec2.transitgateway.workflow;

import com.aws.ec2.transitgateway.ResourceModel;
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
}

