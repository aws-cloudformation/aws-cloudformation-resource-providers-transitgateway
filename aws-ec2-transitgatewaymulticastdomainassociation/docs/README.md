# AWS::EC2::TransitGatewayMulticastDomainAssociation

The AWS::EC2::TransitGatewayMulticastDomainAssociation type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayMulticastDomainAssociation",
    "Properties" : {
        "<a href="#state" title="State">State</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayMulticastDomainAssociation
Properties:
    <a href="#state" title="State">State</a>: <i>String</i>
</pre>

## Properties

#### State

The state of the subnet association.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

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

#### TransitGatewayMulticastDomainId

The ID of the transit gateway multicast domain.

#### TransitGatewayAttachmentId

The ID of the transit gateway attachment.

#### SubnetId

The IDs of the subnets to associate with the transit gateway multicast domain.

