# AWS::EC2::TransitGatewayConnect

The AWS::EC2::TransitGatewayConnect type

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayConnect",
    "Properties" : {
        "<a href="#transporttransitgatewayattachmentid" title="TransportTransitGatewayAttachmentId">TransportTransitGatewayAttachmentId</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#options" title="Options">Options</a>" : <i><a href="transitgatewayconnectoptions.md">TransitGatewayConnectOptions</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayConnect
Properties:
    <a href="#transporttransitgatewayattachmentid" title="TransportTransitGatewayAttachmentId">TransportTransitGatewayAttachmentId</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#options" title="Options">Options</a>: <i><a href="transitgatewayconnectoptions.md">TransitGatewayConnectOptions</a></i>
</pre>

## Properties

#### TransportTransitGatewayAttachmentId

The ID of the attachment from which the Connect attachment was created.

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

The tags for the attachment.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Options

_Required_: Yes

_Type_: <a href="transitgatewayconnectoptions.md">TransitGatewayConnectOptions</a>

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

The ID of the transit gateway.

