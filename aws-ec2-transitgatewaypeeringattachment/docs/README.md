# AWS::EC2::TransitGatewayPeeringAttachment

The AWS::EC2::TransitGatewayPeeringAttachment type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayPeeringAttachment",
    "Properties" : {
        "<a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>" : <i>String</i>,
        "<a href="#peertransitgatewayid" title="PeerTransitGatewayId">PeerTransitGatewayId</a>" : <i>String</i>,
        "<a href="#peeraccountid" title="PeerAccountId">PeerAccountId</a>" : <i>String</i>,
        "<a href="#peerregion" title="PeerRegion">PeerRegion</a>" : <i>String</i>,
        "<a href="#status" title="Status">Status</a>" : <i><a href="peeringattachmentstatus.md">PeeringAttachmentStatus</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayPeeringAttachment
Properties:
    <a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>: <i>String</i>
    <a href="#peertransitgatewayid" title="PeerTransitGatewayId">PeerTransitGatewayId</a>: <i>String</i>
    <a href="#peeraccountid" title="PeerAccountId">PeerAccountId</a>: <i>String</i>
    <a href="#peerregion" title="PeerRegion">PeerRegion</a>: <i>String</i>
    <a href="#status" title="Status">Status</a>: <i><a href="peeringattachmentstatus.md">PeeringAttachmentStatus</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### TransitGatewayId

The ID of the transit gateway.

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerTransitGatewayId

The ID of the peer transit gateway.

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerAccountId

The ID of the peer account

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerRegion

Peer Region

_Required_: Yes

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Status

_Required_: No

_Type_: <a href="peeringattachmentstatus.md">PeeringAttachmentStatus</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags for the transit gateway peering attachment.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the TransitGatewayAttachmentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TransitGatewayAttachmentId

The ID of the transit gateway peering attachment.

#### Status

Returns the <code>Status</code> value.

#### State

The state of the transit gateway peering attachment. Note that the initiating state has been deprecated.

#### CreationTime

The time the transit gateway peering attachment was created.

