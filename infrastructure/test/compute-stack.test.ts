import * as cdk from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import { ComputeStack } from '../lib/compute-stack';
import { NetworkingStack } from '../lib/networking-stack';
import { getEnvironmentConfig } from '../config/environments';

describe('ComputeStack', () => {
  let app: cdk.App;
  let networkingStack: NetworkingStack;
  let computeStack: ComputeStack;
  let template: Template;

  beforeEach(() => {
    app = new cdk.App();
    const config = getEnvironmentConfig('dev');
    const env = { account: '123456789012', region: 'us-east-1' };

    networkingStack = new NetworkingStack(app, 'TestNetworkingStack', {
      config,
      env,
    });

    computeStack = new ComputeStack(app, 'TestComputeStack', {
      config,
      env,
      vpc: networkingStack.vpc,
      securityGroups: {
        applicationSg: networkingStack.applicationSecurityGroup,
        albSg: networkingStack.albSecurityGroup,
      },
      keyspaceEndpoint: 'cassandra.us-east-1.amazonaws.com:9142',
    });

    template = Template.fromStack(computeStack);
  });

  test('creates S3 bucket for artifacts', () => {
    template.hasResourceProperties('AWS::S3::Bucket', {
      VersioningConfiguration: {
        Status: 'Enabled',
      },
      PublicAccessBlockConfiguration: {
        BlockPublicAcls: true,
        BlockPublicPolicy: true,
        IgnorePublicAcls: true,
        RestrictPublicBuckets: true,
      },
    });
  });

  test('creates Application Load Balancer', () => {
    template.hasResourceProperties('AWS::ElasticLoadBalancingV2::LoadBalancer', {
      Type: 'application',
      Scheme: 'internet-facing',
    });
  });

  test('creates target group with health check', () => {
    template.hasResourceProperties('AWS::ElasticLoadBalancingV2::TargetGroup', {
      Port: 8080,
      Protocol: 'HTTP',
      TargetType: 'instance',
      HealthCheckPath: '/health',
      HealthCheckProtocol: 'HTTP',
      HealthCheckPort: '8080',
    });
  });

  test('creates HTTP listener', () => {
    template.hasResourceProperties('AWS::ElasticLoadBalancingV2::Listener', {
      Port: 80,
      Protocol: 'HTTP',
    });
  });

  test('creates launch template', () => {
    template.hasResourceProperties('AWS::EC2::LaunchTemplate', {
      LaunchTemplateData: {
        InstanceType: 't3.medium',
        ImageId: {
          Ref: expect.stringMatching(/^SsmParameterValue/),
        },
      },
    });
  });

  test('creates Auto Scaling Group', () => {
    template.hasResourceProperties('AWS::AutoScaling::AutoScalingGroup', {
      MinSize: '1',
      MaxSize: '2',
      DesiredCapacity: '1',
      HealthCheckType: 'ELB',
      HealthCheckGracePeriod: 300,
    });
  });

  test('creates scaling policies', () => {
    // CPU-based scaling policy
    template.hasResourceProperties('AWS::AutoScaling::ScalingPolicy', {
      PolicyType: 'TargetTrackingScaling',
      TargetTrackingConfiguration: {
        TargetValue: 70,
        PredefinedMetricSpecification: {
          PredefinedMetricType: 'ASGAverageCPUUtilization',
        },
      },
    });
  });

  test('creates IAM role for instances', () => {
    template.hasResourceProperties('AWS::IAM::Role', {
      AssumeRolePolicyDocument: {
        Statement: [
          {
            Effect: 'Allow',
            Principal: {
              Service: 'ec2.amazonaws.com',
            },
            Action: 'sts:AssumeRole',
          },
        ],
      },
      ManagedPolicyArns: [
        'arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore',
        'arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy',
      ],
    });
  });

  test('creates outputs', () => {
    template.hasOutput('LoadBalancerDnsName', {});
    template.hasOutput('LoadBalancerArn', {});
    template.hasOutput('ArtifactsBucketName', {});
  });
});