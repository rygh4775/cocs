#!/bin/bash

# COCS Application Deployment Script
# This script builds the application and deploys it to the CDK infrastructure

set -e

# Configuration
ENVIRONMENT=${1:-dev}
AWS_REGION=${2:-us-east-1}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
INFRASTRUCTURE_ROOT="$PROJECT_ROOT/infrastructure"

echo "=== COCS Application Deployment ==="
echo "Environment: $ENVIRONMENT"
echo "AWS Region: $AWS_REGION"
echo "Project Root: $PROJECT_ROOT"
echo ""

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|staging|prod)$ ]]; then
    echo "Error: Invalid environment. Must be one of: dev, staging, prod"
    exit 1
fi

# Check prerequisites
echo "Checking prerequisites..."

# Check if AWS CLI is installed and configured
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is not installed"
    exit 1
fi

# Check if CDK is installed
if ! command -v cdk &> /dev/null; then
    echo "Error: AWS CDK is not installed"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed"
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "Error: Node.js is not installed"
    exit 1
fi

# Verify AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo "Error: AWS credentials not configured or invalid"
    exit 1
fi

echo "Prerequisites check passed!"
echo ""

# Build the application
echo "Building application..."
cd "$PROJECT_ROOT"

# Clean and build the Maven project
mvn clean package -DskipTests

# Check if WAR file was created
WAR_FILE="$PROJECT_ROOT/target/ROOT.war"
if [[ ! -f "$WAR_FILE" ]]; then
    echo "Error: WAR file not found at $WAR_FILE"
    exit 1
fi

echo "Application built successfully!"
echo ""

# Install CDK dependencies
echo "Installing CDK dependencies..."
cd "$INFRASTRUCTURE_ROOT"
npm install

echo "CDK dependencies installed!"
echo ""

# Deploy infrastructure
echo "Deploying infrastructure..."

# Bootstrap CDK if needed
echo "Bootstrapping CDK..."
cdk bootstrap --context environment=$ENVIRONMENT

# Deploy all stacks
echo "Deploying CDK stacks..."
cdk deploy --all --context environment=$ENVIRONMENT --require-approval never

# Get the S3 bucket name from CDK outputs
echo "Getting infrastructure outputs..."
BUCKET_NAME=$(aws cloudformation describe-stacks \
    --stack-name "cocs-$ENVIRONMENT-compute" \
    --region "$AWS_REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='ArtifactsBucketName'].OutputValue" \
    --output text)

if [[ -z "$BUCKET_NAME" ]]; then
    echo "Error: Could not retrieve S3 bucket name from CloudFormation outputs"
    exit 1
fi

echo "S3 Bucket: $BUCKET_NAME"
echo ""

# Upload application artifact to S3
echo "Uploading application artifact to S3..."
aws s3 cp "$WAR_FILE" "s3://$BUCKET_NAME/ROOT.war"

echo "Application artifact uploaded successfully!"
echo ""

# Trigger application deployment by updating Auto Scaling Group
echo "Triggering application deployment..."

# Get Auto Scaling Group name
ASG_NAME=$(aws cloudformation describe-stacks \
    --stack-name "cocs-$ENVIRONMENT-compute" \
    --region "$AWS_REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='AutoScalingGroupName'].OutputValue" \
    --output text 2>/dev/null || echo "")

if [[ -n "$ASG_NAME" ]]; then
    echo "Refreshing Auto Scaling Group: $ASG_NAME"
    
    # Start instance refresh to deploy new application version
    aws autoscaling start-instance-refresh \
        --auto-scaling-group-name "$ASG_NAME" \
        --preferences '{"InstanceWarmup": 300, "MinHealthyPercentage": 50}' \
        --region "$AWS_REGION"
    
    echo "Instance refresh started. New instances will be launched with the updated application."
else
    echo "Warning: Could not find Auto Scaling Group name. Manual instance refresh may be required."
fi

echo ""

# Get application URL
ALB_DNS=$(aws cloudformation describe-stacks \
    --stack-name "cocs-$ENVIRONMENT-compute" \
    --region "$AWS_REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDnsName'].OutputValue" \
    --output text)

if [[ -n "$ALB_DNS" ]]; then
    echo "=== Deployment Complete ==="
    echo "Application URL: http://$ALB_DNS"
    echo "Dashboard: https://$AWS_REGION.console.aws.amazon.com/cloudwatch/home?region=$AWS_REGION#dashboards:name=cocs-$ENVIRONMENT-dashboard"
    echo ""
    echo "Note: It may take a few minutes for the application to be fully available."
else
    echo "Warning: Could not retrieve application URL"
fi

echo ""
echo "Deployment completed successfully!"