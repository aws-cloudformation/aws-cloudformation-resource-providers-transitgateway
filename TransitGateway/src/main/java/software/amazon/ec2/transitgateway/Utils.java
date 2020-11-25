package software.amazon.ec2.transitgateway;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.*;

import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    final static int CALlBACK_PERIOD_30_SECONDS = 30;
    final static int MAX_CALLBACK_COUNT = 30;
    final static String TIMED_OUT_MESSAGE = "Timed out waiting for the request to be completed.";
    final static String UNRECOGNIZED_STATE_MESSAGE = "MulticastDomain state is unrecognized, code: %s, message: %s";

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is EC2 SDK Tag object and Tag2 is CFN Tag object
     */
    static List<software.amazon.ec2.transitgateway.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
        if (tags == null) return null;
        final List<software.amazon.ec2.transitgateway.Tag> cfnTags =
                tags.stream()
                        .map(e -> software.amazon.ec2.transitgateway.Tag.builder()
                                .key(e.key())
                                .value(e.value())
                                .build())
                        .collect(Collectors.toList());
        return cfnTags;
    }

    /**
     * Converter method to convert List<Tag1> to List<Tag2>
     * where Tag1 is cloudformation Tag object and Tag2 is NetworkManager SDK Tag object
     */
    static List<Tag> cfnTagsToSdkTags(final List<software.amazon.ec2.transitgateway.Tag> tags) {
        if (tags == null) {
            return new ArrayList<Tag>();
        }
        for (final software.amazon.ec2.transitgateway.Tag tag : tags) {
            if (tag.getKey() == null) {
                throw new CfnInvalidRequestException("Tags cannot have a null key");
            }
        }
        return tags.stream()
                .map(e -> Tag.builder()
                        .key(e.getKey())
                        .value(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * Converter method to convert TransitGateway to CFN ResourceModel for LIST/READ request
     */
    static ResourceModel transformTransitGateway(final TransitGateway transitGateway) {
        final ResourceModel resourceModel = ResourceModel.builder().
                transitGatewayId(transitGateway.transitGatewayId())
                .amazonSideAsn(transitGateway.options().amazonSideAsn().intValue())
                .associationDefaultRouteTableId(transitGateway.options().associationDefaultRouteTableId())
                .autoAcceptSharedAttachments(transitGateway.options().autoAcceptSharedAttachmentsAsString())
                .defaultRouteTableAssociation(transitGateway.options().defaultRouteTableAssociationAsString())
                .defaultRouteTablePropagation(transitGateway.options().defaultRouteTablePropagationAsString())
                .description(transitGateway.description())
                .dnsSupport(transitGateway.options().dnsSupportAsString())
                .multicastSupport(transitGateway.options().multicastSupportAsString())
                .propagationDefaultRouteTableId(transitGateway.options().propagationDefaultRouteTableId())
                .tags(Utils.sdkTagsToCfnTags(transitGateway.tags()))
                .transitGatewayArn(transitGateway.transitGatewayArn())
                .vpnEcmpSupport(transitGateway.options().vpnEcmpSupportAsString())
                .build();

        return resourceModel;
    }



    /**
     * Converter method to convert stack/resource tags to EC2 TagSpecification List
     */
    static List<TagSpecification> translateTagsToTagSpecifications(final List<software.amazon.ec2.transitgateway.Tag> tags) {

        if (tags == null) {
            return null;
        }else{
            List<software.amazon.awssdk.services.ec2.model.Tag> listTags = Utils.cfnTagsToSdkTags(tags);
            return Arrays.asList(TagSpecification.builder()
                    .resourceType("transit-gateway")
                    .tags(listTags).build());
        }

    }



    static DescribeTransitGatewaysResponse describeTransitGatewaysResponse(final Ec2Client client,
                                                                           final ResourceModel model,
                                                                           final AmazonWebServicesClientProxy proxy) {
        final DescribeTransitGatewaysRequest describeTransitGatewaysRequest =
                DescribeTransitGatewaysRequest.builder()
                        .transitGatewayIds(model.getTransitGatewayId())
                        .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewaysRequest, client::describeTransitGateways);
    }





    static DescribeTransitGatewaysResponse describeTransitGateways(final Ec2Client client,
                                                                    final ResourceModel model,
                                                                    final AmazonWebServicesClientProxy proxy) {
        final DescribeTransitGatewaysRequest describeTransitGatewaysRequest = DescribeTransitGatewaysRequest.builder()
                .transitGatewayIds(model.getTransitGatewayId())
                .build();
        return proxy.injectCredentialsAndInvokeV2(describeTransitGatewaysRequest, client::describeTransitGateways);
    }

    static TransitGatewayRequestOptions transitGatewayRequestOptions(ResourceModel model){

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
