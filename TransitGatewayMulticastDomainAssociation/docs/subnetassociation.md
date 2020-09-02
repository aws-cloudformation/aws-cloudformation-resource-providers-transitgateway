# AWS::EC2::TransitGatewayMulticastDomainAssociation SubnetAssociation

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#state" title="state">state</a>" : <i>String</i>,
    "<a href="#subnetid" title="subnetId">subnetId</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#state" title="state">state</a>: <i>String</i>
<a href="#subnetid" title="subnetId">subnetId</a>: <i>String</i>
</pre>

## Properties

#### state

The state of the subnet association

_Required_: No

_Type_: String

_Allowed Values_: <code>associating</code> | <code>associated</code> | <code>disassociating</code> | <code>disassociated</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### subnetId

The ID of the subnet

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

