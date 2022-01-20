# AWS::EC2::TransitGatewayRouteTablePropagation

AWS::EC2::TransitGatewayRouteTablePropagation Type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayRouteTablePropagation",
    "Properties" : {
        "<a href="#transitgatewayroutetableid" title="TransitGatewayRouteTableId">TransitGatewayRouteTableId</a>" : <i>String</i>,
        "<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>" : <i>String</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayRouteTablePropagation
Properties:
    <a href="#transitgatewayroutetableid" title="TransitGatewayRouteTableId">TransitGatewayRouteTableId</a>: <i>String</i>
    <a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>: <i>String</i>
</pre>

## Properties

#### TransitGatewayRouteTableId

The ID of transit gateway route table.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TransitGatewayAttachmentId

The ID of transit gateway attachment.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ResourceId

The ID of transit gateway attachment resource.

#### ResourceType

The type of transit gateway attachment resource

#### State

The state of the transit gateway route table propagation.
