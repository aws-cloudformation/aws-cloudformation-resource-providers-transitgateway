# AWS::EC2::TransitGatewayPeeringAttachment PeeringTgwInfo

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>" : <i>String</i>,
    "<a href="#ownerid" title="OwnerId">OwnerId</a>" : <i>String</i>,
    "<a href="#region" title="Region">Region</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>: <i>String</i>
<a href="#ownerid" title="OwnerId">OwnerId</a>: <i>String</i>
<a href="#region" title="Region">Region</a>: <i>String</i>
</pre>

## Properties

#### TransitGatewayId

The ID of the transit gateway.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### OwnerId

The AWS account ID of the owner of the transit gateway.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Region

The Region of the transit gateway.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

