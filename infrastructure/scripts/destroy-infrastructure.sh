#!/bin/bash

# COCS Infrastructure Destruction Script
# This script safely destroys the CDK infrastructure

set -e

# Configuration
ENVIRONMENT=${1:-dev}
AWS_REGION=${2:-us-east-1}
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
INFRASTRUCTURE_ROOT="$PROJECT_ROOT/infrastructure"

echo "=== COCS Infrastructure Destruction ==="
echo "Environment: $ENVIRONMENT"
echo "AWS Region: $AWS_REGION"
echo ""

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|staging|prod)$ ]]; then
    echo "Error: Invalid environment. Must be one of: dev, staging, prod"
    exit 1
fi

# Safety check for production
if [[ "$ENVIRONMENT" == "prod" ]]; then
    echo "WARNING: You are about to destroy PRODUCTION infrastructure!"
    echo "This action is IRREVERSIBLE and will result in DATA LOSS!"
    echo ""
    read -p "Type 'DELETE PRODUCTION' to confirm: " confirmation
    if [[ "$confirmation" != "DELETE PRODUCTION" ]]; then
        echo "Destruction cancelled."
        exit 1
    fi
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

# Verify AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo "Error: AWS credentials not configured or invalid"
    exit 1
fi

echo "Prerequisites check passed!"
echo ""

# Change to infrastructure directory
cd "$INFRASTRUCTURE_ROOT"

# Install CDK dependencies if needed
if [[ ! -d "node_modules" ]]; then
    echo "Installing CDK dependencies..."
    npm install
fi

# Get S3 bucket name before destroying infrastructure
echo "Retrieving S3 bucket information..."
BUCKET_NAME=$(aws cloudformation describe-stacks \
    --stack-name "cocs-$ENVIRONMENT-compute" \
    --region "$AWS_REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='ArtifactsBucketName'].OutputValue" \
    --output text 2>/dev/null || echo "")

# Empty S3 bucket if it exists
if [[ -n "$BUCKET_NAME" ]]; then
    echo "Emptying S3 bucket: $BUCKET_NAME"
    aws s3 rm "s3://$BUCKET_NAME" --recursive || true
    
    # Remove all versions if versioning is enabled
    aws s3api list-object-versions \
        --bucket "$BUCKET_NAME" \
        --query 'Versions[].{Key:Key,VersionId:VersionId}' \
        --output text | while read key version; do
        if [[ -n "$key" && -n "$version" ]]; then
            aws s3api delete-object --bucket "$BUCKET_NAME" --key "$key" --version-id "$version" || true
        fi
    done
    
    # Remove delete markers
    aws s3api list-object-versions \
        --bucket "$BUCKET_NAME" \
        --query 'DeleteMarkers[].{Key:Key,VersionId:VersionId}' \
        --output text | while read key version; do
        if [[ -n "$key" && -n "$version" ]]; then
            aws s3api delete-object --bucket "$BUCKET_NAME" --key "$key" --version-id "$version" || true
        fi
    done
    
    echo "S3 bucket emptied successfully!"
else
    echo "No S3 bucket found or already destroyed."
fi

echo ""

# Destroy CDK stacks in reverse order
echo "Destroying CDK infrastructure..."
echo "This may take several minutes..."

# List of stacks in reverse dependency order
STACKS=(
    "cocs-$ENVIRONMENT-monitoring"
    "cocs-$ENVIRONMENT-compute"
    "cocs-$ENVIRONMENT-database"
    "cocs-$ENVIRONMENT-networking"
    "cocs-$ENVIRONMENT-security"
)

for stack in "${STACKS[@]}"; do
    echo "Destroying stack: $stack"
    
    # Check if stack exists
    if aws cloudformation describe-stacks --stack-name "$stack" --region "$AWS_REGION" &>/dev/null; then
        cdk destroy "$stack" --context environment=$ENVIRONMENT --force || {
            echo "Warning: Failed to destroy stack $stack. It may have already been destroyed or have dependencies."
        }
    else
        echo "Stack $stack does not exist or has already been destroyed."
    fi
done

echo ""

# Clean up any remaining resources
echo "Cleaning up remaining resources..."

# Remove log groups that might not be destroyed automatically
LOG_GROUPS=$(aws logs describe-log-groups \
    --log-group-name-prefix "/aws/ec2/cocs-$ENVIRONMENT" \
    --region "$AWS_REGION" \
    --query 'logGroups[].logGroupName' \
    --output text 2>/dev/null || echo "")

if [[ -n "$LOG_GROUPS" ]]; then
    for log_group in $LOG_GROUPS; do
        echo "Deleting log group: $log_group"
        aws logs delete-log-group --log-group-name "$log_group" --region "$AWS_REGION" || true
    done
fi

# Remove SSM parameters
echo "Cleaning up SSM parameters..."
aws ssm get-parameters-by-path \
    --path "/cocs-$ENVIRONMENT/" \
    --recursive \
    --region "$AWS_REGION" \
    --query 'Parameters[].Name' \
    --output text 2>/dev/null | tr '\t' '\n' | while read param; do
    if [[ -n "$param" ]]; then
        echo "Deleting SSM parameter: $param"
        aws ssm delete-parameter --name "$param" --region "$AWS_REGION" || true
    fi
done

echo ""
echo "=== Destruction Complete ==="
echo "All infrastructure for environment '$ENVIRONMENT' has been destroyed."
echo ""

# Final verification
echo "Verifying destruction..."
remaining_stacks=$(aws cloudformation list-stacks \
    --stack-status-filter CREATE_COMPLETE UPDATE_COMPLETE \
    --region "$AWS_REGION" \
    --query "StackSummaries[?contains(StackName, 'cocs-$ENVIRONMENT')].StackName" \
    --output text 2>/dev/null || echo "")

if [[ -n "$remaining_stacks" ]]; then
    echo "Warning: The following stacks may still exist:"
    echo "$remaining_stacks"
    echo "You may need to manually delete them from the AWS Console."
else
    echo "All stacks have been successfully destroyed."
fi

echo ""
echo "Infrastructure destruction completed!"