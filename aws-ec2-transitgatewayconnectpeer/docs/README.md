# AWS::EC2::TransitGatewayConnectPeer

The AWS::EC2::TransitGatewayConnectPeer type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayConnectPeer",
    "Properties" : {
        "<a href="#transitgatewayconnectpeerid" title="TransitGatewayConnectPeerId">TransitGatewayConnectPeerId</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#connectpeerconfiguration" title="ConnectPeerConfiguration">ConnectPeerConfiguration</a>" : <i><a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a></i>,
        "<a href="#bgpoptions" title="BgpOptions">BgpOptions</a>" : <i><a href="transitgatewayconnectrequestbgpoptions.md">TransitGatewayConnectRequestBgpOptions</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayConnectPeer
Properties:
    <a href="#transitgatewayconnectpeerid" title="TransitGatewayConnectPeerId">TransitGatewayConnectPeerId</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#connectpeerconfiguration" title="ConnectPeerConfiguration">ConnectPeerConfiguration</a>: <i><a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a></i>
    <a href="#bgpoptions" title="BgpOptions">BgpOptions</a>: <i><a href="transitgatewayconnectrequestbgpoptions.md">TransitGatewayConnectRequestBgpOptions</a></i>
</pre>

## Properties

#### TransitGatewayConnectPeerId

The ID of the ConnectPeer.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags for the attachment.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConnectPeerConfiguration

_Required_: No

_Type_: <a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BgpOptions

_Required_: No

_Type_: <a href="transitgatewayconnectrequestbgpoptions.md">TransitGatewayConnectRequestBgpOptions</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the TransitGatewayAttachmentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TransitGatewayAttachmentId

The ID of the Connect attachment.

#### State

The state of the attachment.

#### CreationTime

The creation time.

#### TransitGatewayId

Returns the <code>TransitGatewayId</code> value.

