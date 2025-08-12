import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as cassandra from 'aws-cdk-lib/aws-cassandra';
import * as autoscaling from 'aws-cdk-lib/aws-autoscaling';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as logs from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { EnvironmentConfig } from '../config/environments';

export interface DatabaseStackProps extends cdk.StackProps {
  config: EnvironmentConfig;
  vpc: ec2.Vpc;
  securityGroups: {
    databaseSg: ec2.SecurityGroup;
  };
}

export class DatabaseStack extends cdk.Stack {
  public readonly keyspaceEndpoint: string;
  public readonly cassandraCluster?: autoscaling.AutoScalingGroup;

  constructor(scope: Construct, id: string, props: DatabaseStackProps) {
    super(scope, id, props);

    const { config, vpc, securityGroups } = props;

    if (config.useAmazonKeyspaces) {
      this.setupAmazonKeyspaces(config);
    } else {
      this.setupSelfManagedCassandra(config, vpc, securityGroups.databaseSg);
    }
  }

  private setupAmazonKeyspaces(config: EnvironmentConfig) {
    // Create Amazon Keyspaces keyspace
    const keyspace = new cassandra.CfnKeyspace(this, 'CocsKeyspace', {
      keyspaceName: config.applicationName.replace(/-/g, '_'), // Keyspace names can't have hyphens
      replicationSpecification: {
        replicationStrategy: 'SINGLE_REGION',
        regionList: [config.region],
      },
      tags: [
        {
          key: 'Environment',
          value: config.environment,
        },
        {
          key: 'Application',
          value: config.applicationName,
        },
      ],
    });

    // Create the user table
    const userTable = new cassandra.CfnTable(this, 'CocsUserTable', {
      keyspaceName: keyspace.keyspaceName!,
      tableName: 'user',
      partitionKeyColumns: [
        {
          columnName: 'user_id',
          columnType: 'text',
        },
      ],
      regularColumns: [
        {
          columnName: 'email',
          columnType: 'text',
        },
        {
          columnName: 'password',
          columnType: 'text',
        },
        {
          columnName: 'created_at',
          columnType: 'timestamp',
        },
        {
          columnName: 'updated_at',
          columnType: 'timestamp',
        },
        {
          columnName: 'oauth_providers',
          columnType: 'map<text, text>',
        },
        {
          columnName: 'cloud_accounts',
          columnType: 'map<text, text>',
        },
      ],
      billingMode: {
        mode: 'ON_DEMAND', // Use on-demand billing for flexibility
      },
      pointInTimeRecoveryEnabled: config.environment === 'prod',
      encryptionSpecification: {
        encryptionType: 'AWS_OWNED_KMS_KEY', // Use AWS managed keys
      },
      tags: [
        {
          key: 'Environment',
          value: config.environment,
        },
        {
          key: 'Application',
          value: config.applicationName,
        },
      ],
    });

    userTable.addDependency(keyspace);

    // Set the keyspace endpoint for Amazon Keyspaces
    this.keyspaceEndpoint = `cassandra.${config.region}.amazonaws.com:9142`;

    // Outputs for Amazon Keyspaces
    new cdk.CfnOutput(this, 'KeyspaceName', {
      value: keyspace.keyspaceName!,
      description: 'Amazon Keyspaces keyspace name',
      exportName: `${config.applicationName}-keyspace-name`,
    });

    new cdk.CfnOutput(this, 'KeyspaceEndpoint', {
      value: this.keyspaceEndpoint,
      description: 'Amazon Keyspaces endpoint',
      exportName: `${config.applicationName}-keyspace-endpoint`,
    });
  }

  private setupSelfManagedCassandra(
    config: EnvironmentConfig,
    vpc: ec2.Vpc,
    databaseSg: ec2.SecurityGroup
  ) {
    // Create launch template for Cassandra nodes
    const cassandraUserData = ec2.UserData.forLinux();
    cassandraUserData.addCommands(
      '#!/bin/bash',
      'yum update -y',
      'yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel',
      
      // Install Cassandra
      'echo "[cassandra]" > /etc/yum.repos.d/cassandra.repo',
      'echo "name=Apache Cassandra" >> /etc/yum.repos.d/cassandra.repo',
      'echo "baseurl=https://downloads.apache.org/cassandra/redhat/40x/" >> /etc/yum.repos.d/cassandra.repo',
      'echo "gpgcheck=1" >> /etc/yum.repos.d/cassandra.repo',
      'echo "repo_gpgcheck=1" >> /etc/yum.repos.d/cassandra.repo',
      'echo "gpgkey=https://downloads.apache.org/cassandra/KEYS" >> /etc/yum.repos.d/cassandra.repo',
      
      'rpm --import https://downloads.apache.org/cassandra/KEYS',
      'yum install -y cassandra',
      
      // Configure Cassandra
      'sed -i "s/cluster_name: \'Test Cluster\'/cluster_name: \'COCS Cluster\'/" /etc/cassandra/conf/cassandra.yaml',
      'sed -i "s/num_tokens: 256/num_tokens: 256/" /etc/cassandra/conf/cassandra.yaml',
      'sed -i "s/authenticator: AllowAllAuthenticator/authenticator: PasswordAuthenticator/" /etc/cassandra/conf/cassandra.yaml',
      'sed -i "s/authorizer: AllowAllAuthorizer/authorizer: CassandraAuthorizer/" /etc/cassandra/conf/cassandra.yaml',
      
      // Set up data directories
      'mkdir -p /var/lib/cassandra/data',
      'mkdir -p /var/lib/cassandra/commitlog',
      'mkdir -p /var/lib/cassandra/saved_caches',
      'chown -R cassandra:cassandra /var/lib/cassandra',
      
      // Configure JVM options
      'echo "-Xms2G" >> /etc/cassandra/conf/jvm.options',
      'echo "-Xmx2G" >> /etc/cassandra/conf/jvm.options',
      
      // Start Cassandra
      'systemctl enable cassandra',
      'systemctl start cassandra',
      
      // Wait for Cassandra to start and create keyspace
      'sleep 60',
      `cqlsh -e "CREATE KEYSPACE IF NOT EXISTS ${config.applicationName.replace(/-/g, '_')} WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': ${config.cassandraReplicationFactor}};"`,
      `cqlsh -e "USE ${config.applicationName.replace(/-/g, '_')}; CREATE TABLE IF NOT EXISTS user (user_id text PRIMARY KEY, email text, password text, created_at timestamp, updated_at timestamp, oauth_providers map<text, text>, cloud_accounts map<text, text>);"`,
      
      // Install CloudWatch agent
      'yum install -y amazon-cloudwatch-agent',
      
      // Signal completion
      `/opt/aws/bin/cfn-signal -e $? --stack ${this.stackName} --resource CassandraAutoScalingGroup --region ${this.region}`
    );

    const launchTemplate = new ec2.LaunchTemplate(this, 'CassandraLaunchTemplate', {
      instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.of(config.cassandraInstanceType.split('.')[0]),
        ec2.InstanceSize.of(config.cassandraInstanceType.split('.')[1])
      ),
      machineImage: ec2.MachineImage.latestAmazonLinux2(),
      userData: cassandraUserData,
      securityGroup: databaseSg,
      role: new iam.Role(this, 'CassandraInstanceRole', {
        assumedBy: new iam.ServicePrincipal('ec2.amazonaws.com'),
        managedPolicies: [
          iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonSSMManagedInstanceCore'),
          iam.ManagedPolicy.fromAwsManagedPolicyName('CloudWatchAgentServerPolicy'),
        ],
      }),
      blockDevices: [
        {
          deviceName: '/dev/xvda',
          volume: ec2.BlockDeviceVolume.ebs(100, {
            volumeType: ec2.EbsDeviceVolumeType.GP3,
            encrypted: true,
          }),
        },
        {
          deviceName: '/dev/xvdf',
          volume: ec2.BlockDeviceVolume.ebs(500, {
            volumeType: ec2.EbsDeviceVolumeType.GP3,
            encrypted: true,
          }),
        },
      ],
    });

    // Create Auto Scaling Group for Cassandra cluster
    this.cassandraCluster = new autoscaling.AutoScalingGroup(this, 'CassandraAutoScalingGroup', {
      vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PRIVATE_ISOLATED,
      },
      launchTemplate,
      minCapacity: config.cassandraReplicationFactor,
      maxCapacity: config.cassandraReplicationFactor * 2,
      desiredCapacity: config.cassandraReplicationFactor,
      healthCheck: autoscaling.HealthCheck.ec2({
        grace: cdk.Duration.minutes(10),
      }),
      updatePolicy: autoscaling.UpdatePolicy.rollingUpdate({
        maxBatchSize: 1,
        minInstancesInService: config.cassandraReplicationFactor - 1,
        pauseTime: cdk.Duration.minutes(10),
      }),
    });

    // Add CloudWatch monitoring
    if (config.enableDetailedMonitoring) {
      this.cassandraCluster.scaleOnMetric('CassandraScaling', {
        metric: this.cassandraCluster.metricCpuUtilization(),
        scalingSteps: [
          { upper: 30, change: -1 },
          { lower: 70, change: +1 },
        ],
        adjustmentType: autoscaling.AdjustmentType.CHANGE_IN_CAPACITY,
      });
    }

    // Set endpoint for self-managed Cassandra (will be internal load balancer)
    this.keyspaceEndpoint = 'cassandra-internal.local:9042';

    // Outputs for self-managed Cassandra
    new cdk.CfnOutput(this, 'CassandraClusterArn', {
      value: this.cassandraCluster.autoScalingGroupArn,
      description: 'Cassandra cluster Auto Scaling Group ARN',
      exportName: `${config.applicationName}-cassandra-cluster-arn`,
    });

    new cdk.CfnOutput(this, 'CassandraEndpoint', {
      value: this.keyspaceEndpoint,
      description: 'Cassandra cluster endpoint',
      exportName: `${config.applicationName}-cassandra-endpoint`,
    });
  }
}