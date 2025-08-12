# CDK Infrastructure Directory

This directory contains the AWS CDK infrastructure code for the COCS application.

## Structure

- `lib/` - CDK stack definitions
- `bin/` - CDK app entry point
- `test/` - Infrastructure tests
- `config/` - Environment-specific configurations

## Prerequisites

- Node.js 18+ 
- AWS CDK CLI v2
- AWS CLI configured with appropriate credentials

## Usage

```bash
# Install dependencies
npm install

# Deploy to development environment
npm run deploy:dev

# Deploy to production environment  
npm run deploy:prod

# Destroy infrastructure
npm run destroy:dev
```