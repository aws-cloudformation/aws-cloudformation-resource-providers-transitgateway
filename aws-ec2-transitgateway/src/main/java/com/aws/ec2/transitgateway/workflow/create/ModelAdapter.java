package com.aws.ec2.transitgateway.workflow.create;
import com.aws.ec2.transitgateway.ResourceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ModelAdapter {

    private static final String DEFAULT_ROUTE_TABLE_ASSOCIATION = "enable";
    private static final String DEFAULT_ROUTE_TABLE_PROPOGATION = "enable";
    private static final String DEFAULT_AUTO_SHARED_ATTACHMENTS = "disable";
    private static final String DEFAULT_VPN_ECMP_SUPPORT = "enable";
    private static final String DEFAULT_DNS_SUPPORT = "enable";
    private static final String DEFAULT_MULTICAST_SUPPORT = "disable";
    private static final Integer DEFAULT_AMAZON_SIDE_ASN = 64512;

    public static ResourceModel setDefaults(final ResourceModel model) {


        final String defaultRouteTableAssociation = model.getDefaultRouteTableAssociation();
        final String defaultRouteTablePropagation = model.getDefaultRouteTablePropagation();
        final String autoAcceptSharedAttachments = model.getAutoAcceptSharedAttachments();
        final String vpnEcmpSupport = model.getVpnEcmpSupport();
        final String dnsSupport = model.getDnsSupport();
        final String multicastSupport = model.getMulticastSupport();
        final Long amazonSideAsn = model.getAmazonSideAsn();


        model.setDefaultRouteTableAssociation(defaultRouteTableAssociation == null ? DEFAULT_ROUTE_TABLE_ASSOCIATION : defaultRouteTableAssociation);
        model.setDefaultRouteTablePropagation(defaultRouteTablePropagation == null ? DEFAULT_ROUTE_TABLE_PROPOGATION : defaultRouteTablePropagation);
        model.setAutoAcceptSharedAttachments(autoAcceptSharedAttachments == null ? DEFAULT_AUTO_SHARED_ATTACHMENTS : autoAcceptSharedAttachments);
        model.setVpnEcmpSupport(vpnEcmpSupport == null? DEFAULT_VPN_ECMP_SUPPORT : vpnEcmpSupport);
        model.setDnsSupport(dnsSupport == null? DEFAULT_DNS_SUPPORT : dnsSupport);
        model.setMulticastSupport(multicastSupport == null ? DEFAULT_MULTICAST_SUPPORT : multicastSupport);
        model.setAmazonSideAsn(amazonSideAsn == null ? DEFAULT_AMAZON_SIDE_ASN : amazonSideAsn);

        return model;
    }


}
