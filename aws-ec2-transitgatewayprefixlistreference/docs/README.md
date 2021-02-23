# AWS::EC2::TransitGatewayPrefixListReference

The AWS::EC2::TransitGatewayPrefixListReference type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayPrefixListReference",
    "Properties" : {
        "<a href="#transitgatewayroutetableid" title="TransitGatewayRouteTableId">TransitGatewayRouteTableId</a>" : <i>String</i>,
        "<a href="#prefixlistid" title="PrefixListId">PrefixListId</a>" : <i>String</i>,
        "<a href="#blackhole" title="Blackhole">Blackhole</a>" : <i>Boolean</i>,
        "<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayPrefixListReference
Properties:
    <a href="#transitgatewayroutetableid" title="TransitGatewayRouteTableId">TransitGatewayRouteTableId</a>: <i>String</i>
    <a href="#prefixlistid" title="PrefixListId">PrefixListId</a>: <i>String</i>
    <a href="#blackhole" title="Blackhole">Blackhole</a>: <i>Boolean</i>
    <a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>: <i>String</i>
</pre>

## Properties

#### TransitGatewayRouteTableId

The ID of the transit gateway route table.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PrefixListId

The ID of the prefix list.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Blackhole

Indicates whether traffic that matches this route is dropped.

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransitGatewayAttachmentId

Information about the transit gateway attachment.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the PrefixListId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### PrefixListOwnerId

The ID of the prefix list owner.

#### State

The state of the prefix list reference.

#### TransitGatewayAttachment

Returns the <code>TransitGatewayAttachment</code> value.

