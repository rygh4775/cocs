#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { NetworkingStack } from '../lib/networking-stack';
import { DatabaseStack } from '../lib/database-stack';
import { ComputeStack } from '../lib/compute-stack';
import { MonitoringStack } from '../lib/monitoring-stack';
import { SecurityStack } from '../lib/security-stack';
import { getEnvironmentConfig } from '../config/environments';

const app = new cdk.App();

// Get environment from context
const environment = app.node.tryGetContext('environment') || 'dev';
const config = getEnvironmentConfig(environment);

// Define the AWS environment
const env = {
  account: config.account || process.env.CDK_DEFAULT_ACCOUNT,
  region: config.region
};

// Create stack name prefix
const stackPrefix = `${config.applicationName}`;

// Security Stack (foundational)
const securityStack = new SecurityStack(app, `${stackPrefix}-security`, {
  env,
  config,
  description: `Security resources for ${config.applicationName} - ${config.environment}`
});

// Networking Stack (foundational)
const networkingStack = new NetworkingStack(app, `${stackPrefix}-networking`, {
  env,
  config,
  description: `Networking resources for ${config.applicationName} - ${config.environment}`
});

// Database Stack
const databaseStack = new DatabaseStack(app, `${stackPrefix}-database`, {
  env,
  config,
  vpc: networkingStack.vpc,
  securityGroups: {
    databaseSg: networkingStack.databaseSecurityGroup
  },
  description: `Database resources for ${config.applicationName} - ${config.environment}`
});

// Compute Stack
const computeStack = new ComputeStack(app, `${stackPrefix}-compute`, {
  env,
  config,
  vpc: networkingStack.vpc,
  securityGroups: {
    applicationSg: networkingStack.applicationSecurityGroup,
    albSg: networkingStack.albSecurityGroup
  },
  keyspaceEndpoint: databaseStack.keyspaceEndpoint,
  description: `Compute resources for ${config.applicationName} - ${config.environment}`
});

// Monitoring Stack
const monitoringStack = new MonitoringStack(app, `${stackPrefix}-monitoring`, {
  env,
  config,
  applicationLoadBalancer: computeStack.applicationLoadBalancer,
  autoScalingGroup: computeStack.autoScalingGroup,
  description: `Monitoring resources for ${config.applicationName} - ${config.environment}`
});

// Add dependencies
databaseStack.addDependency(networkingStack);
databaseStack.addDependency(securityStack);
computeStack.addDependency(networkingStack);
computeStack.addDependency(databaseStack);
computeStack.addDependency(securityStack);
monitoringStack.addDependency(computeStack);

// Add common tags to all stacks
const commonTags = {
  ...config.tags,
  StackType: 'Infrastructure',
  ManagedBy: 'CDK'
};

Object.entries(commonTags).forEach(([key, value]) => {
  cdk.Tags.of(securityStack).add(key, value);
  cdk.Tags.of(networkingStack).add(key, value);
  cdk.Tags.of(databaseStack).add(key, value);
  cdk.Tags.of(computeStack).add(key, value);
  cdk.Tags.of(monitoringStack).add(key, value);
});