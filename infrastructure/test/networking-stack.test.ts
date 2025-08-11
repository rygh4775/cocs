import * as cdk from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import { NetworkingStack } from '../lib/networking-stack';
import { getEnvironmentConfig } from '../config/environments';

describe('NetworkingStack', () => {
  let app: cdk.App;
  let stack: NetworkingStack;
  let template: Template;

  beforeEach(() => {
    app = new cdk.App();
    const config = getEnvironmentConfig('dev');
    stack = new NetworkingStack(app, 'TestNetworkingStack', {
      config,
      env: { account: '123456789012', region: 'us-east-1' },
    });
    template = Template.fromStack(stack);
  });

  test('creates VPC with correct CIDR', () => {
    template.hasResourceProperties('AWS::EC2::VPC', {
      CidrBlock: '10.0.0.0/16',
      EnableDnsHostnames: true,
      EnableDnsSupport: true,
    });
  });

  test('creates public and private subnets', () => {
    // Should have public subnets
    template.hasResourceProperties('AWS::EC2::Subnet', {
      MapPublicIpOnLaunch: true,
    });

    // Should have private subnets
    template.hasResourceProperties('AWS::EC2::Subnet', {
      MapPublicIpOnLaunch: false,
    });
  });

  test('creates internet gateway', () => {
    template.hasResourceProperties('AWS::EC2::InternetGateway', {});
  });

  test('creates NAT gateway', () => {
    template.hasResourceProperties('AWS::EC2::NatGateway', {});
  });

  test('creates security groups', () => {
    // ALB Security Group
    template.hasResourceProperties('AWS::EC2::SecurityGroup', {
      GroupDescription: 'Security group for Application Load Balancer',
    });

    // Application Security Group
    template.hasResourceProperties('AWS::EC2::SecurityGroup', {
      GroupDescription: 'Security group for COCS application instances',
    });

    // Database Security Group
    template.hasResourceProperties('AWS::EC2::SecurityGroup', {
      GroupDescription: 'Security group for Cassandra database',
    });
  });

  test('creates VPC endpoints', () => {
    // S3 Gateway Endpoint
    template.hasResourceProperties('AWS::EC2::VPCEndpoint', {
      ServiceName: 'com.amazonaws.us-east-1.s3',
      VpcEndpointType: 'Gateway',
    });

    // SSM Interface Endpoint
    template.hasResourceProperties('AWS::EC2::VPCEndpoint', {
      ServiceName: 'com.amazonaws.us-east-1.ssm',
      VpcEndpointType: 'Interface',
    });
  });

  test('configures security group rules correctly', () => {
    // ALB should allow HTTP and HTTPS
    template.hasResourceProperties('AWS::EC2::SecurityGroupIngress', {
      IpProtocol: 'tcp',
      FromPort: 80,
      ToPort: 80,
      CidrIp: '0.0.0.0/0',
    });

    template.hasResourceProperties('AWS::EC2::SecurityGroupIngress', {
      IpProtocol: 'tcp',
      FromPort: 443,
      ToPort: 443,
      CidrIp: '0.0.0.0/0',
    });

    // Database should allow Cassandra port from application
    template.hasResourceProperties('AWS::EC2::SecurityGroupIngress', {
      IpProtocol: 'tcp',
      FromPort: 9042,
      ToPort: 9042,
    });
  });

  test('creates outputs', () => {
    template.hasOutput('VpcId', {});
    template.hasOutput('VpcCidr', {});
    template.hasOutput('PublicSubnetIds', {});
    template.hasOutput('PrivateSubnetIds', {});
  });
});