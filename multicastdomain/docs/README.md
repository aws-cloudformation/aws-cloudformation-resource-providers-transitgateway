# AWS::EC2::TransitGatewayMulticastDomain

The AWS::EC2::TransitGatewayMulticastDomain type segments the multicast network into different domains and makes the transit gateway act as multiple multicast routers
An example resource schema demonstrating some basic constructs and validation rules.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayMulticastDomain",
    "Properties" : {
        "<a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>" : <i>String</i>,
        "<a href="#tagset" title="TagSet">TagSet</a>" : <i>[ <a href="tagset.md">TagSet</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayMulticastDomain
Properties:
    <a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>: <i>String</i>
    <a href="#tagset" title="TagSet">TagSet</a>: <i>
      - <a href="tagset.md">TagSet</a></i>
</pre>

## Properties

#### TransitGatewayId

The ID of the transit gateway

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TagSet

The tags for the transit gateway multicast domain.

_Required_: No

_Type_: List of <a href="tagset.md">TagSet</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TransitGatewayMulticastDomainId

The ID of the transit gateway multicast domain.

