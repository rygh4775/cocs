export interface EnvironmentConfig {
  account?: string;
  region: string;
  environment: string;
  
  // Application configuration
  applicationName: string;
  domainName?: string;
  certificateArn?: string;
  
  // Networking
  vpcCidr: string;
  availabilityZones: string[];
  
  // Compute
  instanceType: string;
  minCapacity: number;
  maxCapacity: number;
  desiredCapacity: number;
  
  // Database
  cassandraInstanceType: string;
  cassandraReplicationFactor: number;
  useAmazonKeyspaces: boolean;
  
  // Storage
  enableS3Logging: boolean;
  s3BucketRetentionDays: number;
  
  // Monitoring
  enableDetailedMonitoring: boolean;
  logRetentionDays: number;
  
  // Security
  enableWaf: boolean;
  allowedCidrBlocks: string[];
  
  // Tags
  tags: { [key: string]: string };
}

export const environments: { [key: string]: EnvironmentConfig } = {
  dev: {
    region: 'us-east-1',
    environment: 'dev',
    applicationName: 'cocs-dev',
    
    // Networking
    vpcCidr: '10.0.0.0/16',
    availabilityZones: ['us-east-1a', 'us-east-1b'],
    
    // Compute - smaller instances for dev
    instanceType: 't3.medium',
    minCapacity: 1,
    maxCapacity: 2,
    desiredCapacity: 1,
    
    // Database
    cassandraInstanceType: 't3.medium',
    cassandraReplicationFactor: 1,
    useAmazonKeyspaces: true, // Use managed service for dev
    
    // Storage
    enableS3Logging: true,
    s3BucketRetentionDays: 30,
    
    // Monitoring
    enableDetailedMonitoring: false,
    logRetentionDays: 7,
    
    // Security
    enableWaf: false,
    allowedCidrBlocks: ['0.0.0.0/0'], // Open for dev
    
    tags: {
      Environment: 'dev',
      Project: 'COCS',
      Owner: 'Development Team',
      CostCenter: 'Engineering'
    }
  },
  
  staging: {
    region: 'us-east-1',
    environment: 'staging',
    applicationName: 'cocs-staging',
    
    // Networking
    vpcCidr: '10.1.0.0/16',
    availabilityZones: ['us-east-1a', 'us-east-1b', 'us-east-1c'],
    
    // Compute
    instanceType: 't3.large',
    minCapacity: 2,
    maxCapacity: 4,
    desiredCapacity: 2,
    
    // Database
    cassandraInstanceType: 't3.large',
    cassandraReplicationFactor: 2,
    useAmazonKeyspaces: true,
    
    // Storage
    enableS3Logging: true,
    s3BucketRetentionDays: 90,
    
    // Monitoring
    enableDetailedMonitoring: true,
    logRetentionDays: 30,
    
    // Security
    enableWaf: true,
    allowedCidrBlocks: ['10.0.0.0/8', '172.16.0.0/12', '192.168.0.0/16'],
    
    tags: {
      Environment: 'staging',
      Project: 'COCS',
      Owner: 'Development Team',
      CostCenter: 'Engineering'
    }
  },
  
  prod: {
    region: 'us-east-1',
    environment: 'prod',
    applicationName: 'cocs-prod',
    domainName: 'cocs.example.com', // Update with actual domain
    
    // Networking
    vpcCidr: '10.2.0.0/16',
    availabilityZones: ['us-east-1a', 'us-east-1b', 'us-east-1c'],
    
    // Compute - production-ready instances
    instanceType: 'm5.xlarge',
    minCapacity: 3,
    maxCapacity: 10,
    desiredCapacity: 3,
    
    // Database
    cassandraInstanceType: 'm5.xlarge',
    cassandraReplicationFactor: 3,
    useAmazonKeyspaces: true,
    
    // Storage
    enableS3Logging: true,
    s3BucketRetentionDays: 365,
    
    // Monitoring
    enableDetailedMonitoring: true,
    logRetentionDays: 90,
    
    // Security
    enableWaf: true,
    allowedCidrBlocks: ['0.0.0.0/0'], // Public access for production
    
    tags: {
      Environment: 'prod',
      Project: 'COCS',
      Owner: 'Operations Team',
      CostCenter: 'Production',
      Backup: 'Required',
      Compliance: 'Required'
    }
  }
};

export function getEnvironmentConfig(environment: string): EnvironmentConfig {
  const config = environments[environment];
  if (!config) {
    throw new Error(`Environment configuration not found for: ${environment}`);
  }
  return config;
}