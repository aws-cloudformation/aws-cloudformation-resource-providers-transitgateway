# AWS::EC2::TransitGateway

Resource Type definition for AWS::EC2::TransitGateway

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGateway",
    "Properties" : {
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#defaultroutetablepropagation" title="DefaultRouteTablePropagation">DefaultRouteTablePropagation</a>" : <i>String</i>,
        "<a href="#autoacceptsharedattachments" title="AutoAcceptSharedAttachments">AutoAcceptSharedAttachments</a>" : <i>String</i>,
        "<a href="#defaultroutetableassociation" title="DefaultRouteTableAssociation">DefaultRouteTableAssociation</a>" : <i>String</i>,
        "<a href="#vpnecmpsupport" title="VpnEcmpSupport">VpnEcmpSupport</a>" : <i>String</i>,
        "<a href="#dnssupport" title="DnsSupport">DnsSupport</a>" : <i>String</i>,
        "<a href="#multicastsupport" title="MulticastSupport">MulticastSupport</a>" : <i>String</i>,
        "<a href="#amazonsideasn" title="AmazonSideAsn">AmazonSideAsn</a>" : <i>Integer</i>,
        "<a href="#associationdefaultroutetableid" title="associationDefaultRouteTableId">associationDefaultRouteTableId</a>" : <i>String</i>,
        "<a href="#propagationdefaultroutetableid" title="propagationDefaultRouteTableId">propagationDefaultRouteTableId</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGateway
Properties:
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#defaultroutetablepropagation" title="DefaultRouteTablePropagation">DefaultRouteTablePropagation</a>: <i>String</i>
    <a href="#autoacceptsharedattachments" title="AutoAcceptSharedAttachments">AutoAcceptSharedAttachments</a>: <i>String</i>
    <a href="#defaultroutetableassociation" title="DefaultRouteTableAssociation">DefaultRouteTableAssociation</a>: <i>String</i>
    <a href="#vpnecmpsupport" title="VpnEcmpSupport">VpnEcmpSupport</a>: <i>String</i>
    <a href="#dnssupport" title="DnsSupport">DnsSupport</a>: <i>String</i>
    <a href="#multicastsupport" title="MulticastSupport">MulticastSupport</a>: <i>String</i>
    <a href="#amazonsideasn" title="AmazonSideAsn">AmazonSideAsn</a>: <i>Integer</i>
    <a href="#associationdefaultroutetableid" title="associationDefaultRouteTableId">associationDefaultRouteTableId</a>: <i>String</i>
    <a href="#propagationdefaultroutetableid" title="propagationDefaultRouteTableId">propagationDefaultRouteTableId</a>: <i>String</i>
</pre>

## Properties

#### Description

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DefaultRouteTablePropagation

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AutoAcceptSharedAttachments

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DefaultRouteTableAssociation

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### VpnEcmpSupport

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DnsSupport

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MulticastSupport

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AmazonSideAsn

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### associationDefaultRouteTableId

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### propagationDefaultRouteTableId

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the TransitGatewayId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TransitGatewayId

Returns the <code>TransitGatewayId</code> value.

#### TransitGatewayArn

Returns the <code>TransitGatewayArn</code> value.

