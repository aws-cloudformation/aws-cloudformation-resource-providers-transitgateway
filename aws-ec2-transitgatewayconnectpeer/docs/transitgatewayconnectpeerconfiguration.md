# AWS::EC2::TransitGatewayConnectPeer TransitGatewayConnectPeerConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#protocol" title="Protocol">Protocol</a>" : <i>String</i>,
    "<a href="#bgpconfigurations" title="BgpConfigurations">BgpConfigurations</a>" : <i><a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a></i>,
    "<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>" : <i>[ String, ... ]</i>,
    "<a href="#peeraddress" title="PeerAddress">PeerAddress</a>" : <i>String</i>,
    "<a href="#transitgatewayaddress" title="TransitGatewayAddress">TransitGatewayAddress</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#protocol" title="Protocol">Protocol</a>: <i>String</i>
<a href="#bgpconfigurations" title="BgpConfigurations">BgpConfigurations</a>: <i><a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a></i>
<a href="#insidecidrblocks" title="InsideCidrBlocks">InsideCidrBlocks</a>: <i>
      - String</i>
<a href="#peeraddress" title="PeerAddress">PeerAddress</a>: <i>String</i>
<a href="#transitgatewayaddress" title="TransitGatewayAddress">TransitGatewayAddress</a>: <i>String</i>
</pre>

## Properties

#### Protocol

The tunnel protocol.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BgpConfigurations

_Required_: No

_Type_: <a href="transitgatewayconnectpeerconfiguration.md">TransitGatewayConnectPeerConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InsideCidrBlocks

The range of interior BGP peer IP addresses.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PeerAddress

Connect peer IP address on the appliance side of the tunnel.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransitGatewayAddress

The Connect peer IP address on the transit gateway side of the tunnel.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

