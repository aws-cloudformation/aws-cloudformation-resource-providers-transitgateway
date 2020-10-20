package software.amazon.ec2.transitgatewayattachment;

import com.google.common.collect.Lists;

import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  final static int CALlBACK_PERIOD_30_SECONDS = 30;
  final static int MAX_CALLBACK_COUNT = 30;
  final static String TIMED_OUT_MESSAGE = "Timed out waiting for the request to be completed.";
  final static String UNRECOGNIZED_STATE_MESSAGE = "TGW Attachment state is unrecognized, code: %s, message: %s";

  /**
   * Converter method to convert List<Tag1> to List<Tag2>
   * where Tag1 is cloudformation Tag object and Tag2 is TGWAttachment SDK Tag object
   */

  static List<Tag> cfnTagsToSdkTags(final List<software.amazon.ec2.transitgatewayattachment.Tag> tags) {
    if (tags == null) {
      return new ArrayList<Tag>();
    }
    for (final software.amazon.ec2.transitgatewayattachment.Tag tag : tags) {
      if (tag.getKey() == null) {
        throw new CfnInvalidRequestException("Tags cannot have a null key");
      }
      if (tag.getValue() == null) {
        throw new CfnInvalidRequestException("Tags cannot have a null value");
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
   * Converter method to convert List<Tag1> to List<Tag2>
   * where Tag1 is EC2 SDK Tag object and Tag2 is CFN Tag object
   */
  static List<software.amazon.ec2.transitgatewayattachment.Tag> sdkTagsToCfnTags(final List<Tag> tags) {
    if (tags == null) return null;
    final List<software.amazon.ec2.transitgatewayattachment.Tag> cfnTags =
            tags.stream()
                    .map(e -> software.amazon.ec2.transitgatewayattachment.Tag.builder()
                            .key(e.key())
                            .value(e.value())
                            .build())
                    .collect(Collectors.toList());
    return cfnTags;
  }

  static List<TagSpecification> translateTagsToTagSpecifications(final List<software.amazon.awssdk.services.ec2.model.Tag> newTags) {
    if (newTags == null) {
      return null;
    }
    return Arrays.asList(TagSpecification.builder()
            .resourceType("transit-gateway-attachment")
            .tags(newTags).build());
  }

  static List<Tag> translateTagsToTagSpecifications(final Map<String, String> tags) {
    if(tags == null) {
      return null;
    }

    List<Tag> newTags = tags.keySet()
            .stream()
            .map(t -> Tag.builder()
                    .key(t).value(tags.get(t))
                    .build())
            .collect(Collectors.toList());

    return newTags;
  }


  static CreateTransitGatewayVpcAttachmentRequestOptions translateOptionsToTransitGatewayRequestOptions(final Options options) {
    if (options == null) return null;

    return CreateTransitGatewayVpcAttachmentRequestOptions.builder()
            .dnsSupport(options.getDnsSupport())
            .ipv6Support(options.getIpv6Support())
            .build();

  }

  static List<software.amazon.awssdk.services.ec2.model.Filter> cfnFiltersToSdkFilters(final List<software.amazon.ec2.transitgatewayattachment.Filter> filters) {
    if (filters == null) {
      return new ArrayList<software.amazon.awssdk.services.ec2.model.Filter>();
    }
    for (final software.amazon.ec2.transitgatewayattachment.Filter filter : filters) {
      if (filter.getName() == null) {
        throw new CfnInvalidRequestException("Tags cannot have a null key");
      }
      if (filter.getValues() == null) {
        throw new CfnInvalidRequestException("Tags cannot have a null value");
      }
    }
    return filters.stream()
            .map(e -> software.amazon.awssdk.services.ec2.model.Filter.builder()
                    .name(e.getName())
                    .values(e.getValues())
                    .build())
            .collect(Collectors.toList());
  }

  /**
   * Converter method to convert List<Tag1> to List<Tag2>
   * where Tag1 is EC2 SDK Tag object and Tag2 is CFN Tag object
   */
  static List<software.amazon.ec2.transitgatewayattachment.Filter> sdkFiltersToCfnFilters(final List<Filter> filters) {
    if (filters == null) return null;
    final List<software.amazon.ec2.transitgatewayattachment.Filter> cfnFilters =
            filters.stream()
                    .map(e -> software.amazon.ec2.transitgatewayattachment.Filter.builder()
                            .name(e.getName())
                            .values(e.getValues())
                            .build())
                    .collect(Collectors.toList());
    return cfnFilters;
  }



  static DescribeTransitGatewayAttachmentsResponse describeTransitGatewayAttachments(final Ec2Client client,
                                                                 final ResourceModel model,
                                                                 final AmazonWebServicesClientProxy proxy) {
    final DescribeTransitGatewayAttachmentsRequest describeTransitGatewayAttachmentsRequest = DescribeTransitGatewayAttachmentsRequest.builder()
            .transitGatewayAttachmentIds(model.getTransitGatewayAttachmentIds())
            .filters(cfnFiltersToSdkFilters(model.getFilters()))
            .maxResults(model.getMaxResults())
            .nextToken(model.getNextToken())
            .build();

    return proxy.injectCredentialsAndInvokeV2(describeTransitGatewayAttachmentsRequest, client::describeTransitGatewayAttachments);
  }


  static DescribeTransitGatewayVpcAttachmentsResponse describeTransitGatewayVpcAttachments(final Ec2Client client,
                                                                                     final ResourceModel model,
                                                                                     final AmazonWebServicesClientProxy proxy) {
    final DescribeTransitGatewayVpcAttachmentsRequest describeTransitGatewayVpcAttachmentsRequest = DescribeTransitGatewayVpcAttachmentsRequest.builder()
            .transitGatewayAttachmentIds(model.getTransitGatewayAttachmentIds())
            .maxResults(model.getMaxResults())
            .nextToken(model.getNextToken())
            .build();

    return proxy.injectCredentialsAndInvokeV2(describeTransitGatewayVpcAttachmentsRequest, client::describeTransitGatewayVpcAttachments);
  }


  /**
   * Converter method to convert TransitGateway to CFN ResourceModel for LIST/READ request
   * @param transitGatewayAttachment
   */
  static ResourceModel transformTransitGatewayAttachments(final TransitGatewayAttachment transitGatewayAttachment) {
    final ResourceModel resourceModel = ResourceModel.builder().build();

    resourceModel.setTransitGatewayId(transitGatewayAttachment.transitGatewayId());
    resourceModel.setTransitGatewayAttachmentId(transitGatewayAttachment.transitGatewayAttachmentId());
    resourceModel.setTags(Translator.sdkTagsToCfnTags(transitGatewayAttachment.tags()));


    return resourceModel;
  }


  static ResourceModel transformTransitGatewayVpcAttachments(final TransitGatewayVpcAttachment transitGatewayVpcAttachment) {
    final ResourceModel resourceModel = ResourceModel.builder().build();

   resourceModel.setTransitGatewayId(transitGatewayVpcAttachment.transitGatewayId());
    resourceModel.setTransitGatewayAttachmentId(transitGatewayVpcAttachment.transitGatewayAttachmentId());
    resourceModel.setTags(Translator.sdkTagsToCfnTags(transitGatewayVpcAttachment.tags()));
    resourceModel.setVpcId(transitGatewayVpcAttachment.vpcId());
    resourceModel.setSubnetIds(transitGatewayVpcAttachment.subnetIds());

    return resourceModel;
  }



  static ModifyTransitGatewayVpcAttachmentRequestOptions translateOptions(final Options options) {
    if (options == null) return null;

    return ModifyTransitGatewayVpcAttachmentRequestOptions.builder()
            .dnsSupport(options.getDnsSupport())
            .ipv6Support(options.getIpv6Support())
            .build();

  }



}
