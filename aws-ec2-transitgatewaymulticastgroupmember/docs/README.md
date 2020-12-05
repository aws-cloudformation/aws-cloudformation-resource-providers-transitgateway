# AWS::EC2::TransitGatewayMulticastGroupMember

The AWS::EC2::TransitGatewayMulticastGroupMember registers and deregisters members and sources (network interfaces) with the transit gateway multicast group

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayMulticastGroupMember",
    "Properties" : {
        "<a href="#groupipaddress" title="GroupIpAddress">GroupIpAddress</a>" : <i>String</i>,
        "<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>" : <i>String</i>,
        "<a href="#transitgatewaymulticastdomainid" title="TransitGatewayMulticastDomainId">TransitGatewayMulticastDomainId</a>" : <i>String</i>,
        "<a href="#networkinterfaceid" title="NetworkInterfaceId">NetworkInterfaceId</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayMulticastGroupMember
Properties:
    <a href="#groupipaddress" title="GroupIpAddress">GroupIpAddress</a>: <i>String</i>
    <a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>: <i>String</i>
    <a href="#transitgatewaymulticastdomainid" title="TransitGatewayMulticastDomainId">TransitGatewayMulticastDomainId</a>: <i>String</i>
    <a href="#networkinterfaceid" title="NetworkInterfaceId">NetworkInterfaceId</a>: <i>String</i>
</pre>

## Properties

#### GroupIpAddress

The IP address assigned to the transit gateway multicast group.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayAttachmentId

The ID of the transit gateway attachment.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransitGatewayMulticastDomainId

The ID of the transit gateway multicast domain.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### NetworkInterfaceId

The ID of the transit gateway attachment.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### SubnetId

The ID of the subnet.

#### ResourceId

The ID of the resource.

#### ResourceType

The type of resource, for example a VPC attachment.

#### GroupSource

Indicates that the resource is a transit gateway multicast group member.

#### GroupMember

Indicates that the resource is a transit gateway multicast group member.

#### MemberType

The member type (for example, static).

#### SourceType

The source type.

