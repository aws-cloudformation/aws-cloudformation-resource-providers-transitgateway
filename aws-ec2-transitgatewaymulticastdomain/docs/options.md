# AWS::EC2::TransitGatewayMulticastDomain Options

The options for the transit gateway multicast domain.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#autoacceptsharedassociations" title="AutoAcceptSharedAssociations">AutoAcceptSharedAssociations</a>" : <i>String</i>,
    "<a href="#igmpv2support" title="Igmpv2Support">Igmpv2Support</a>" : <i>String</i>,
    "<a href="#staticsourcessupport" title="StaticSourcesSupport">StaticSourcesSupport</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#autoacceptsharedassociations" title="AutoAcceptSharedAssociations">AutoAcceptSharedAssociations</a>: <i>String</i>
<a href="#igmpv2support" title="Igmpv2Support">Igmpv2Support</a>: <i>String</i>
<a href="#staticsourcessupport" title="StaticSourcesSupport">StaticSourcesSupport</a>: <i>String</i>
</pre>

## Properties

#### AutoAcceptSharedAssociations

Indicates whether to automatically cross-account subnet associations that are associated with the transit gateway multicast domain. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Igmpv2Support

Indicates whether Internet Group Management Protocol (IGMP) version 2 is turned on for the transit gateway multicast domain. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StaticSourcesSupport

Indicates whether support for statically configuring transit gateway multicast group sources is turned on. Valid Values: enable | disable

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
