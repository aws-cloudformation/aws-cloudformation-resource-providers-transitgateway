# AWS::EC2::TransitGatewayMulticastDomainAssociation

The AWS::EC2::TransitGatewayMulticastDomainAssociation type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayMulticastDomainAssociation",
    "Properties" : {
        "<a href="#transitgatewaymulticastdomainid" title="TransitGatewayMulticastDomainId">TransitGatewayMulticastDomainId</a>" : <i>String</i>,
        "<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>" : <i>String</i>,
        "<a href="#state" title="State">State</a>" : <i>String</i>,
        "<a href="#subnetid" title="SubnetId">SubnetId</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayMulticastDomainAssociation
Properties:
    <a href="#transitgatewaymulticastdomainid" title="TransitGatewayMulticastDomainId">TransitGatewayMulticastDomainId</a>: <i>String</i>
    <a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>: <i>String</i>
    <a href="#state" title="State">State</a>: <i>String</i>
    <a href="#subnetid" title="SubnetId">SubnetId</a>: <i>String</i>
</pre>

## Properties

#### TransitGatewayMulticastDomainId

The ID of the transit gateway multicast domain.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayAttachmentId

The ID of the transit gateway attachment.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### State

The state of the subnet association.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SubnetId

The IDs of the subnets to associate with the transit gateway multicast domain.

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ResourceId

The ID of the resource.

#### ResourceType

The type of resource, for example a VPC attachment.

#### Subnet

Returns the <code>Subnet</code> value.
<<<<<<< HEAD

=======
>>>>>>> ac51bc8... Restore create only properties (#33)
