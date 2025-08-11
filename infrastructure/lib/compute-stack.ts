import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as autoscaling from 'aws-cdk-lib/aws-autoscaling';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3deploy from 'aws-cdk-lib/aws-s3-deployment';
import * as logs from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { EnvironmentConfig } from '../config/environments';

export interface ComputeStackProps extends cdk.StackProps {
  config: EnvironmentConfig;
  vpc: ec2.Vpc;
  securityGroups: {
    applicationSg: ec2.SecurityGroup;
    albSg: ec2.SecurityGroup;
  };
  keyspaceEndpoint: string;
}

export class ComputeStack extends cdk.Stack {
  public readonly applicationLoadBalancer: elbv2.ApplicationLoadBalancer;
  public readonly autoScalingGroup: autoscaling.AutoScalingGroup;
  public readonly artifactsBucket: s3.Bucket;

  constructor(scope: Construct, id: string, props: ComputeStackProps) {
    super(scope, id, props);

    const { config, vpc, securityGroups, keyspaceEndpoint } = props;

    // S3 bucket for application artifacts
    this.artifactsBucket = new s3.Bucket(this, 'CocsArtifactsBucket', {
      bucketName: `${config.applicationName}-artifacts-${this.account}-${this.region}`,
      versioned: true,
      encryption: s3.BucketEncryption.S3_MANAGED,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
      lifecycleRules: [
        {
          id: 'DeleteOldVersions',
          enabled: true,
          noncurrentVersionExpiration: cdk.Duration.days(config.s3BucketRetentionDays),
        },
      ],
      removalPolicy: config.environment === 'prod' 
        ? cdk.RemovalPolicy.RETAIN 
        : cdk.RemovalPolicy.DESTROY,
    });

    // Application Load Balancer
    this.applicationLoadBalancer = new elbv2.ApplicationLoadBalancer(this, 'CocsApplicationLoadBalancer', {
      vpc,
      internetFacing: true,
      securityGroup: securityGroups.albSg,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PUBLIC,
      },
      deletionProtection: config.environment === 'prod',
    });

    // Target Group
    const targetGroup = new elbv2.ApplicationTargetGroup(this, 'CocsTargetGroup', {
      vpc,
      port: 8080,
      protocol: elbv2.ApplicationProtocol.HTTP,
      targetType: elbv2.TargetType.INSTANCE,
      healthCheck: {
        enabled: true,
        healthyHttpCodes: '200',
        path: '/health',
        protocol: elbv2.Protocol.HTTP,
        port: '8080',
        interval: cdk.Duration.seconds(30),
        timeout: cdk.Duration.seconds(5),
        healthyThresholdCount: 2,
        unhealthyThresholdCount: 3,
      },
      deregistrationDelay: cdk.Duration.seconds(30),
    });

    // HTTP Listener (redirect to HTTPS in production)
    const httpListener = this.applicationLoadBalancer.addListener('HttpListener', {
      port: 80,
      protocol: elbv2.ApplicationProtocol.HTTP,
      defaultAction: config.environment === 'prod'
        ? elbv2.ListenerAction.redirect({
            protocol: 'HTTPS',
            port: '443',
            permanent: true,
          })
        : elbv2.ListenerAction.forward([targetGroup]),
    });

    // HTTPS Listener (if certificate is provided)
    if (config.certificateArn) {
      this.applicationLoadBalancer.addListener('HttpsListener', {
        port: 443,
        protocol: elbv2.ApplicationProtocol.HTTPS,
        certificates: [
          elbv2.ListenerCertificate.fromArn(config.certificateArn),
        ],
        defaultAction: elbv2.ListenerAction.forward([targetGroup]),
      });
    }

    // Create user data script for application instances
    const userData = this.createUserData(config, keyspaceEndpoint);

    // Launch Template
    const launchTemplate = new ec2.LaunchTemplate(this, 'CocsLaunchTemplate', {
      instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.of(config.instanceType.split('.')[0]),
        ec2.InstanceSize.of(config.instanceType.split('.')[1])
      ),
      machineImage: ec2.MachineImage.latestAmazonLinux2(),
      userData,
      securityGroup: securityGroups.applicationSg,
      role: this.createInstanceRole(config),
      blockDevices: [
        {
          deviceName: '/dev/xvda',
          volume: ec2.BlockDeviceVolume.ebs(20, {
            volumeType: ec2.EbsDeviceVolumeType.GP3,
            encrypted: true,
          }),
        },
      ],
      detailedMonitoring: config.enableDetailedMonitoring,
    });

    // Auto Scaling Group
    this.autoScalingGroup = new autoscaling.AutoScalingGroup(this, 'CocsAutoScalingGroup', {
      vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS,
      },
      launchTemplate,
      minCapacity: config.minCapacity,
      maxCapacity: config.maxCapacity,
      desiredCapacity: config.desiredCapacity,
      healthCheck: autoscaling.HealthCheck.elb({
        grace: cdk.Duration.minutes(5),
      }),
      updatePolicy: autoscaling.UpdatePolicy.rollingUpdate({
        maxBatchSize: 1,
        minInstancesInService: config.minCapacity,
        pauseTime: cdk.Duration.minutes(5),
        waitOnResourceSignals: true,
        signalTimeout: cdk.Duration.minutes(10),
      }),
    });

    // Attach Auto Scaling Group to Target Group
    this.autoScalingGroup.attachToApplicationTargetGroup(targetGroup);

    // Auto Scaling Policies
    this.autoScalingGroup.scaleOnCpuUtilization('CpuScaling', {
      targetUtilizationPercent: 70,
      scaleInCooldown: cdk.Duration.minutes(5),
      scaleOutCooldown: cdk.Duration.minutes(3),
    });

    // Scale on request count
    this.autoScalingGroup.scaleOnRequestCount('RequestCountScaling', {
      targetRequestsPerMinute: 1000,
    });

    // Outputs
    new cdk.CfnOutput(this, 'LoadBalancerDnsName', {
      value: this.applicationLoadBalancer.loadBalancerDnsName,
      description: 'Application Load Balancer DNS name',
      exportName: `${config.applicationName}-alb-dns-name`,
    });

    new cdk.CfnOutput(this, 'LoadBalancerArn', {
      value: this.applicationLoadBalancer.loadBalancerArn,
      description: 'Application Load Balancer ARN',
      exportName: `${config.applicationName}-alb-arn`,
    });

    new cdk.CfnOutput(this, 'ArtifactsBucketName', {
      value: this.artifactsBucket.bucketName,
      description: 'S3 bucket for application artifacts',
      exportName: `${config.applicationName}-artifacts-bucket-name`,
    });
  }

  private createInstanceRole(config: EnvironmentConfig): iam.Role {
    const role = new iam.Role(this, 'CocsInstanceRole', {
      assumedBy: new iam.ServicePrincipal('ec2.amazonaws.com'),
      description: `IAM role for ${config.applicationName} application instances`,
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonSSMManagedInstanceCore'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('CloudWatchAgentServerPolicy'),
      ],
    });

    // Add permissions for S3 artifacts bucket
    role.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        's3:GetObject',
        's3:ListBucket',
      ],
      resources: [
        this.artifactsBucket.bucketArn,
        `${this.artifactsBucket.bucketArn}/*`,
      ],
    }));

    // Add permissions for SSM parameters
    role.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'ssm:GetParameter',
        'ssm:GetParameters',
        'ssm:GetParametersByPath',
      ],
      resources: [
        `arn:aws:ssm:${this.region}:${this.account}:parameter/${config.applicationName}/*`,
      ],
    }));

    // Add permissions for Secrets Manager
    role.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'secretsmanager:GetSecretValue',
        'secretsmanager:DescribeSecret',
      ],
      resources: [
        `arn:aws:secretsmanager:${this.region}:${this.account}:secret:${config.applicationName}/*`,
      ],
    }));

    // Add permissions for Amazon Keyspaces (if using managed Cassandra)
    if (config.useAmazonKeyspaces) {
      role.addToPolicy(new iam.PolicyStatement({
        effect: iam.Effect.ALLOW,
        actions: [
          'cassandra:Select',
          'cassandra:Insert',
          'cassandra:Update',
          'cassandra:Delete',
        ],
        resources: [
          `arn:aws:cassandra:${this.region}:${this.account}:keyspace/${config.applicationName.replace(/-/g, '_')}`,
          `arn:aws:cassandra:${this.region}:${this.account}:keyspace/${config.applicationName.replace(/-/g, '_')}/*`,
        ],
      }));
    }

    return role;
  }

  private createUserData(config: EnvironmentConfig, keyspaceEndpoint: string): ec2.UserData {
    const userData = ec2.UserData.forLinux();
    
    userData.addCommands(
      '#!/bin/bash',
      'yum update -y',
      
      // Install Java 8 and Tomcat
      'yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel',
      'yum install -y tomcat tomcat-webapps tomcat-admin-webapps',
      
      // Install AWS CLI and CloudWatch agent
      'yum install -y awscli amazon-cloudwatch-agent',
      
      // Create application directories
      'mkdir -p /opt/cocs/config',
      'mkdir -p /opt/cocs/logs',
      'mkdir -p /var/lib/tomcat/temp/upload',
      'chown -R tomcat:tomcat /var/lib/tomcat/temp',
      'chown -R tomcat:tomcat /opt/cocs',
      
      // Download application WAR from S3
      `aws s3 cp s3://${this.artifactsBucket.bucketName}/ROOT.war /var/lib/tomcat/webapps/ROOT.war`,
      
      // Create application configuration from SSM parameters and Secrets Manager
      'cat > /opt/cocs/create-config.sh << \'EOF\'',
      '#!/bin/bash',
      `REGION=${this.region}`,
      `APP_NAME=${config.applicationName}`,
      '',
      '# Get configuration from SSM Parameter Store',
      'aws ssm get-parameters-by-path --region $REGION --path "/$APP_NAME/" --recursive --query "Parameters[*].[Name,Value]" --output text | while read name value; do',
      '  key=$(echo $name | sed "s|/$APP_NAME/||")',
      '  echo "$key=$value" >> /opt/cocs/config/application.properties',
      'done',
      '',
      '# Get secrets from Secrets Manager',
      `SECRET_VALUE=$(aws secretsmanager get-secret-value --region $REGION --secret-id "$APP_NAME/application-secrets" --query SecretString --output text)`,
      'echo "$SECRET_VALUE" | jq -r "to_entries|map(\"\\(.key)=\\(.value|tostring)\")|.[]" >> /opt/cocs/config/application.properties',
      '',
      '# Add database endpoint',
      `echo "cassandra.endpoint=${keyspaceEndpoint}" >> /opt/cocs/config/application.properties`,
      '',
      '# Set proper permissions',
      'chown tomcat:tomcat /opt/cocs/config/application.properties',
      'chmod 600 /opt/cocs/config/application.properties',
      'EOF',
      '',
      'chmod +x /opt/cocs/create-config.sh',
      '/opt/cocs/create-config.sh'
    );

    return userData;
  }
}