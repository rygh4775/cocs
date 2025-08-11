import * as cdk from 'aws-cdk-lib';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as ssm from 'aws-cdk-lib/aws-ssm';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as kms from 'aws-cdk-lib/aws-kms';
import { Construct } from 'constructs';
import { EnvironmentConfig } from '../config/environments';

export interface SecurityStackProps extends cdk.StackProps {
  config: EnvironmentConfig;
}

export class SecurityStack extends cdk.Stack {
  public readonly applicationRole: iam.Role;
  public readonly kmsKey: kms.Key;
  public readonly applicationSecrets: secretsmanager.Secret;

  constructor(scope: Construct, id: string, props: SecurityStackProps) {
    super(scope, id, props);

    const { config } = props;

    // KMS Key for encryption
    this.kmsKey = new kms.Key(this, 'CocsKmsKey', {
      description: `KMS key for ${config.applicationName} encryption`,
      enableKeyRotation: true,
      removalPolicy: config.environment === 'prod' 
        ? cdk.RemovalPolicy.RETAIN 
        : cdk.RemovalPolicy.DESTROY,
    });

    // KMS Key Alias
    new kms.Alias(this, 'CocsKmsKeyAlias', {
      aliasName: `alias/${config.applicationName}-key`,
      targetKey: this.kmsKey,
    });

    // IAM Role for EC2 instances
    this.applicationRole = new iam.Role(this, 'CocsApplicationRole', {
      assumedBy: new iam.ServicePrincipal('ec2.amazonaws.com'),
      description: `IAM role for ${config.applicationName} application instances`,
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonSSMManagedInstanceCore'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('CloudWatchAgentServerPolicy'),
      ],
    });

    // Add permissions for application-specific resources
    this.applicationRole.addToPolicy(new iam.PolicyStatement({
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
    this.applicationRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'secretsmanager:GetSecretValue',
        'secretsmanager:DescribeSecret',
      ],
      resources: [
        `arn:aws:secretsmanager:${this.region}:${this.account}:secret:${config.applicationName}/*`,
      ],
    }));

    // Add permissions for KMS
    this.applicationRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'kms:Decrypt',
        'kms:DescribeKey',
      ],
      resources: [this.kmsKey.keyArn],
    }));

    // Add permissions for Amazon Keyspaces (if using managed Cassandra)
    if (config.useAmazonKeyspaces) {
      this.applicationRole.addToPolicy(new iam.PolicyStatement({
        effect: iam.Effect.ALLOW,
        actions: [
          'cassandra:Select',
          'cassandra:Insert',
          'cassandra:Update',
          'cassandra:Delete',
          'cassandra:Create',
          'cassandra:Alter',
          'cassandra:Drop',
        ],
        resources: [
          `arn:aws:cassandra:${this.region}:${this.account}:keyspace/${config.applicationName}`,
          `arn:aws:cassandra:${this.region}:${this.account}:keyspace/${config.applicationName}/*`,
        ],
      }));
    }

    // Add S3 permissions for application artifacts and logs
    this.applicationRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        's3:GetObject',
        's3:PutObject',
        's3:DeleteObject',
      ],
      resources: [
        `arn:aws:s3:::${config.applicationName}-*/*`,
      ],
    }));

    this.applicationRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        's3:ListBucket',
      ],
      resources: [
        `arn:aws:s3:::${config.applicationName}-*`,
      ],
    }));

    // Instance Profile for EC2
    new iam.CfnInstanceProfile(this, 'CocsInstanceProfile', {
      roles: [this.applicationRole.roleName],
      instanceProfileName: `${config.applicationName}-instance-profile`,
    });

    // Secrets Manager secret for sensitive application configuration
    this.applicationSecrets = new secretsmanager.Secret(this, 'CocsApplicationSecrets', {
      secretName: `${config.applicationName}/application-secrets`,
      description: `Sensitive configuration for ${config.applicationName}`,
      encryptionKey: this.kmsKey,
      generateSecretString: {
        secretStringTemplate: JSON.stringify({
          'smtp.userId': 'cocs.cloudofclouds',
          'smtp.password': 'CHANGE_ME',
          'google.client.id': 'CHANGE_ME',
          'google.client.secret': 'CHANGE_ME',
          'dropbox.client.id': 'CHANGE_ME',
          'dropbox.client.secret': 'CHANGE_ME',
          'facebook.client.id': 'CHANGE_ME',
          'facebook.client.secret': 'CHANGE_ME',
          'twitter.client.id': 'CHANGE_ME',
          'twitter.client.secret': 'CHANGE_ME',
          'naver.client.id': 'CHANGE_ME',
          'naver.client.secret': 'CHANGE_ME',
        }),
        generateStringKey: 'dummy', // Required but not used
        excludeCharacters: '"@/\\',
      },
    });

    // SSM Parameters for non-sensitive configuration
    const ssmParameters = {
      'cassandra.keyspace.name': 'cocs',
      'cassandra.columnfamily.name': 'user',
      'repository.upload.max.size': '2000000000',
      'smtp.address': 'smtp.gmail.com',
      'smtp.port': '465',
      'smtp.senderName': 'COCS',
      'google.authorize.url': 'https://accounts.google.com/o/oauth2/auth',
      'google.token.url': 'https://accounts.google.com/o/oauth2/token',
      'dropbox.authorize.url': 'https://www.dropbox.com/1/oauth2/authorize',
      'dropbox.token.url': 'https://api.dropbox.com/1/oauth2/token',
      'facebook.authorize.url': 'https://www.facebook.com/dialog/oauth',
      'facebook.token.url': 'https://graph.facebook.com/oauth/access_token',
      'facebook.debug.token.url': 'https://graph.facebook.com/debug_token',
      'twitter.authorize.url': 'https://api.twitter.com/oauth/authorize',
      'twitter.token.url': 'https://api.twitter.com/oauth/access_token',
      'twitter.revokeToken.url': 'https://api.twitter.com/oauth2/invalidate_token',
      'naver.authorize.url': 'https://nid.naver.com/oauth2.0/authorize',
      'naver.token.url': 'https://nid.naver.com/oauth2.0/token',
    };

    Object.entries(ssmParameters).forEach(([key, value]) => {
      new ssm.StringParameter(this, `SsmParam${key.replace(/\./g, '')}`, {
        parameterName: `/${config.applicationName}/${key}`,
        stringValue: value,
        description: `Configuration parameter for ${key}`,
        tier: ssm.ParameterTier.STANDARD,
      });
    });

    // Environment-specific redirect URIs (will need to be updated after ALB is created)
    const redirectUriBase = config.domainName 
      ? `https://${config.domainName}` 
      : `https://${config.applicationName}-alb.${this.region}.elb.amazonaws.com`;

    const redirectUris = {
      'google.redirect.uri': `${redirectUriBase}/api/google/token.do`,
      'dropbox.redirect.uri': `${redirectUriBase}/api/dropbox/token.do`,
      'facebook.redirect.uri': `${redirectUriBase}/api/facebook/token.do`,
      'facebook.redirect.signin.uri': `${redirectUriBase}/doSignin.do?oauth_provider=facebook`,
      'twitter.redirect.uri': `${redirectUriBase}/api/twitter/token.do`,
      'naver.redirect.signin.uri': `${redirectUriBase}/doSignin.do?oauth_provider=naver`,
    };

    Object.entries(redirectUris).forEach(([key, value]) => {
      new ssm.StringParameter(this, `SsmParam${key.replace(/\./g, '')}`, {
        parameterName: `/${config.applicationName}/${key}`,
        stringValue: value,
        description: `Redirect URI for ${key}`,
        tier: ssm.ParameterTier.STANDARD,
      });
    });

    // Outputs
    new cdk.CfnOutput(this, 'ApplicationRoleArn', {
      value: this.applicationRole.roleArn,
      description: 'ARN of the application IAM role',
      exportName: `${config.applicationName}-application-role-arn`,
    });

    new cdk.CfnOutput(this, 'KmsKeyId', {
      value: this.kmsKey.keyId,
      description: 'KMS Key ID for encryption',
      exportName: `${config.applicationName}-kms-key-id`,
    });

    new cdk.CfnOutput(this, 'SecretsArn', {
      value: this.applicationSecrets.secretArn,
      description: 'ARN of the application secrets',
      exportName: `${config.applicationName}-secrets-arn`,
    });
  }
}