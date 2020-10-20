# AWS::EC2::TransitGatewayAttachment

Resource Type definition for AWS::EC2::TransitGatewayAttachment

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::EC2::TransitGatewayAttachment",
    "Properties" : {
        "<a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>" : <i>String</i>,
        "<a href="#vpcid" title="VpcId">VpcId</a>" : <i>String</i>,
        "<a href="#subnetids" title="SubnetIds">SubnetIds</a>" : <i>[ String, ... ]</i>,
        "<a href="#addsubnetids" title="AddSubnetIds">AddSubnetIds</a>" : <i>[ String, ... ]</i>,
        "<a href="#removesubnetids" title="RemoveSubnetIds">RemoveSubnetIds</a>" : <i>[ String, ... ]</i>,
        "<a href="#options" title="Options">Options</a>" : <i><a href="options.md">Options</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#filters" title="Filters">Filters</a>" : <i>[ <a href="filter.md">Filter</a>, ... ]</i>,
        "<a href="#maxresults" title="MaxResults">MaxResults</a>" : <i>Integer</i>,
        "<a href="#nexttoken" title="NextToken">NextToken</a>" : <i>String</i>,
        "<a href="#transitgatewayattachmentids" title="TransitGatewayAttachmentIds">TransitGatewayAttachmentIds</a>" : <i>[ String, ... ]</i>,
        "<a href="#vpcownerid" title="vpcOwnerId">vpcOwnerId</a>" : <i>String</i>,
        "<a href="#state" title="state">state</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::EC2::TransitGatewayAttachment
Properties:
    <a href="#transitgatewayid" title="TransitGatewayId">TransitGatewayId</a>: <i>String</i>
    <a href="#vpcid" title="VpcId">VpcId</a>: <i>String</i>
    <a href="#subnetids" title="SubnetIds">SubnetIds</a>: <i>
      - String</i>
    <a href="#addsubnetids" title="AddSubnetIds">AddSubnetIds</a>: <i>
      - String</i>
    <a href="#removesubnetids" title="RemoveSubnetIds">RemoveSubnetIds</a>: <i>
      - String</i>
    <a href="#options" title="Options">Options</a>: <i><a href="options.md">Options</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#filters" title="Filters">Filters</a>: <i>
      - <a href="filter.md">Filter</a></i>
    <a href="#maxresults" title="MaxResults">MaxResults</a>: <i>Integer</i>
    <a href="#nexttoken" title="NextToken">NextToken</a>: <i>String</i>
    <a href="#transitgatewayattachmentids" title="TransitGatewayAttachmentIds">TransitGatewayAttachmentIds</a>: <i>
      - String</i>
    <a href="#vpcownerid" title="vpcOwnerId">vpcOwnerId</a>: <i>String</i>
    <a href="#state" title="state">state</a>: <i>String</i>
</pre>

## Properties

#### TransitGatewayId

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### VpcId

_Required_: Yes

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### SubnetIds

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AddSubnetIds

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RemoveSubnetIds

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Options

_Required_: No

_Type_: <a href="options.md">Options</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Filters

_Required_: No

_Type_: List of <a href="filter.md">Filter</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxResults

The maximum number of results to return with a single call.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### NextToken

The token for the next page of results.

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TransitGatewayAttachmentIds

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### vpcOwnerId

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### state

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the TransitGatewayAttachmentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TransitGatewayAttachmentId

Id of Transit Gateway Attachment

