# AWS::EC2::TransitGatewayAttachment Options

The options for the transit gateway vpc attachment.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#dnssupport" title="DnsSupport">DnsSupport</a>" : <i>String</i>,
    "<a href="#ipv6support" title="Ipv6Support">Ipv6Support</a>" : <i>String</i>,
    "<a href="#appliancemodesupport" title="ApplianceModeSupport">ApplianceModeSupport</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#dnssupport" title="DnsSupport">DnsSupport</a>: <i>String</i>
<a href="#ipv6support" title="Ipv6Support">Ipv6Support</a>: <i>String</i>
<a href="#appliancemodesupport" title="ApplianceModeSupport">ApplianceModeSupport</a>: <i>String</i>
</pre>

## Properties

#### DnsSupport

Indicates whether to enable DNS Support for Vpc Attachment. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Ipv6Support

Indicates whether to enable Ipv6 Support for Vpc Attachment. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ApplianceModeSupport

Indicates whether to enable Ipv6 Support for Vpc Attachment. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

