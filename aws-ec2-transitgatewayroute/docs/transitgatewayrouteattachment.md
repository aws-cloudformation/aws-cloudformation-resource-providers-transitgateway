# AWS::EC2::TransitGatewayRoute TransitGatewayRouteAttachment

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#resourceid" title="ResourceId">ResourceId</a>" : <i>String</i>,
    "<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>" : <i>String</i>,
    "<a href="#resourcetype" title="ResourceType">ResourceType</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#resourceid" title="ResourceId">ResourceId</a>: <i>String</i>
<a href="#transitgatewayattachmentid" title="TransitGatewayAttachmentId">TransitGatewayAttachmentId</a>: <i>String</i>
<a href="#resourcetype" title="ResourceType">ResourceType</a>: <i>String</i>
</pre>

## Properties

#### ResourceId

The ID of the resource.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransitGatewayAttachmentId

The ID of the attachment.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResourceType

The resource type. Note that the tgw-peering resource type has been deprecated.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

