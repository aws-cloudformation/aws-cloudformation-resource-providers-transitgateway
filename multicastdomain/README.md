## AWS::EC2::TransitGatewayMulticastDomain

### Getting Started
1. Copy the commands from bash_commands.sh to your .bash_profile or .zshrc 
2. Setup sam-tests
    - cd to this directory and run `sam local start-lambda`
    - Open a new terminal window and run `cp -rf sam-test-examples sam-test`
    - Update the credentials section of each json file with your AWS credentials
    - Update the TransitGatewayId and TransitGatewayMultiCastDomainId with IDs from your AWS Console account
        - You actually need to create a transit gateway in your personal account and use the tgw-someid id that it creates
    - Run `cfn_test create` | `cfn_test list` | `cfn_test read` | `cfn_test delete` | `cfn_test update`
        - cfn_test is provided as a bash function in bash_commands.sh - Feel free to use it or implement it on your own
        - cfn_test_pretty gives less verbose output put pretty prints the final json response
         