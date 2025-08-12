import * as cdk from 'aws-cdk-lib';
import * as cloudwatch from 'aws-cdk-lib/aws-cloudwatch';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as sns from 'aws-cdk-lib/aws-sns';
import * as snsSubscriptions from 'aws-cdk-lib/aws-sns-subscriptions';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as autoscaling from 'aws-cdk-lib/aws-autoscaling';
import { Construct } from 'constructs';
import { EnvironmentConfig } from '../config/environments';

export interface MonitoringStackProps extends cdk.StackProps {
  config: EnvironmentConfig;
  applicationLoadBalancer: elbv2.ApplicationLoadBalancer;
  autoScalingGroup: autoscaling.AutoScalingGroup;
}

export class MonitoringStack extends cdk.Stack {
  public readonly dashboard: cloudwatch.Dashboard;
  public readonly alertTopic: sns.Topic;

  constructor(scope: Construct, id: string, props: MonitoringStackProps) {
    super(scope, id, props);

    const { config, applicationLoadBalancer, autoScalingGroup } = props;

    // Create SNS topic for alerts
    this.alertTopic = new sns.Topic(this, 'CocsAlertTopic', {
      topicName: `${config.applicationName}-alerts`,
      displayName: `${config.applicationName} Alerts`,
    });

    // Create log groups
    const applicationLogGroup = new logs.LogGroup(this, 'ApplicationLogGroup', {
      logGroupName: `/aws/ec2/${config.applicationName}/tomcat`,
      retention: logs.RetentionDays.of(config.logRetentionDays),
      removalPolicy: config.environment === 'prod' 
        ? cdk.RemovalPolicy.RETAIN 
        : cdk.RemovalPolicy.DESTROY,
    });

    const accessLogGroup = new logs.LogGroup(this, 'AccessLogGroup', {
      logGroupName: `/aws/ec2/${config.applicationName}/access`,
      retention: logs.RetentionDays.of(config.logRetentionDays),
      removalPolicy: config.environment === 'prod' 
        ? cdk.RemovalPolicy.RETAIN 
        : cdk.RemovalPolicy.DESTROY,
    });

    // Create CloudWatch Dashboard
    this.dashboard = new cloudwatch.Dashboard(this, 'CocsDashboard', {
      dashboardName: `${config.applicationName}-dashboard`,
    });

    // Application Load Balancer metrics
    const albRequestCountMetric = applicationLoadBalancer.metricRequestCount({
      period: cdk.Duration.minutes(5),
    });

    const albTargetResponseTimeMetric = applicationLoadBalancer.metricTargetResponseTime({
      period: cdk.Duration.minutes(5),
    });

    const albHttpCodeTargetMetric = applicationLoadBalancer.metricHttpCodeTarget(
      elbv2.HttpCodeTarget.TARGET_2XX_COUNT,
      {
        period: cdk.Duration.minutes(5),
      }
    );

    const albHttpCodeElbMetric = applicationLoadBalancer.metricHttpCodeElb(
      elbv2.HttpCodeElb.ELB_5XX_COUNT,
      {
        period: cdk.Duration.minutes(5),
      }
    );

    // Auto Scaling Group metrics
    const asgCpuMetric = autoScalingGroup.metricCpuUtilization({
      period: cdk.Duration.minutes(5),
    });

    // Add widgets to dashboard
    this.dashboard.addWidgets(
      // First row - Request metrics
      new cloudwatch.GraphWidget({
        title: 'Request Count',
        left: [albRequestCountMetric],
        width: 12,
        height: 6,
      }),
      new cloudwatch.GraphWidget({
        title: 'Response Time',
        left: [albTargetResponseTimeMetric],
        width: 12,
        height: 6,
      }),
    );

    this.dashboard.addWidgets(
      // Second row - HTTP status codes
      new cloudwatch.GraphWidget({
        title: 'HTTP 2XX Responses',
        left: [albHttpCodeTargetMetric],
        width: 12,
        height: 6,
      }),
      new cloudwatch.GraphWidget({
        title: 'HTTP 5XX Errors',
        left: [albHttpCodeElbMetric],
        width: 12,
        height: 6,
      }),
    );

    this.dashboard.addWidgets(
      // Third row - Infrastructure metrics
      new cloudwatch.GraphWidget({
        title: 'CPU Utilization',
        left: [asgCpuMetric],
        width: 12,
        height: 6,
      }),
      new cloudwatch.SingleValueWidget({
        title: 'Healthy Hosts',
        metrics: [
          applicationLoadBalancer.metricHealthyHostCount({
            period: cdk.Duration.minutes(1),
          }),
        ],
        width: 12,
        height: 6,
      }),
    );

    // Create CloudWatch Alarms
    this.createAlarms(config, applicationLoadBalancer, autoScalingGroup);

    // Outputs
    new cdk.CfnOutput(this, 'DashboardUrl', {
      value: `https://${this.region}.console.aws.amazon.com/cloudwatch/home?region=${this.region}#dashboards:name=${this.dashboard.dashboardName}`,
      description: 'CloudWatch Dashboard URL',
      exportName: `${config.applicationName}-dashboard-url`,
    });

    new cdk.CfnOutput(this, 'AlertTopicArn', {
      value: this.alertTopic.topicArn,
      description: 'SNS Topic ARN for alerts',
      exportName: `${config.applicationName}-alert-topic-arn`,
    });
  }

  private createAlarms(
    config: EnvironmentConfig,
    applicationLoadBalancer: elbv2.ApplicationLoadBalancer,
    autoScalingGroup: autoscaling.AutoScalingGroup
  ) {
    // High CPU utilization alarm
    const highCpuAlarm = new cloudwatch.Alarm(this, 'HighCpuAlarm', {
      alarmName: `${config.applicationName}-high-cpu`,
      alarmDescription: 'High CPU utilization detected',
      metric: autoScalingGroup.metricCpuUtilization({
        period: cdk.Duration.minutes(5),
      }),
      threshold: 80,
      evaluationPeriods: 2,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    highCpuAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );

    // High response time alarm
    const highResponseTimeAlarm = new cloudwatch.Alarm(this, 'HighResponseTimeAlarm', {
      alarmName: `${config.applicationName}-high-response-time`,
      alarmDescription: 'High response time detected',
      metric: applicationLoadBalancer.metricTargetResponseTime({
        period: cdk.Duration.minutes(5),
      }),
      threshold: 5, // 5 seconds
      evaluationPeriods: 2,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    highResponseTimeAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );

    // HTTP 5XX errors alarm
    const http5xxAlarm = new cloudwatch.Alarm(this, 'Http5xxAlarm', {
      alarmName: `${config.applicationName}-http-5xx-errors`,
      alarmDescription: 'High number of HTTP 5XX errors detected',
      metric: applicationLoadBalancer.metricHttpCodeElb(
        elbv2.HttpCodeElb.ELB_5XX_COUNT,
        {
          period: cdk.Duration.minutes(5),
          statistic: 'Sum',
        }
      ),
      threshold: 10,
      evaluationPeriods: 2,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    http5xxAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );

    // Unhealthy hosts alarm
    const unhealthyHostsAlarm = new cloudwatch.Alarm(this, 'UnhealthyHostsAlarm', {
      alarmName: `${config.applicationName}-unhealthy-hosts`,
      alarmDescription: 'Unhealthy hosts detected',
      metric: applicationLoadBalancer.metricUnHealthyHostCount({
        period: cdk.Duration.minutes(1),
      }),
      threshold: 1,
      evaluationPeriods: 2,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    unhealthyHostsAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );

    // Low request count alarm (potential issue)
    if (config.environment === 'prod') {
      const lowRequestCountAlarm = new cloudwatch.Alarm(this, 'LowRequestCountAlarm', {
        alarmName: `${config.applicationName}-low-request-count`,
        alarmDescription: 'Unusually low request count detected',
        metric: applicationLoadBalancer.metricRequestCount({
          period: cdk.Duration.minutes(15),
          statistic: 'Sum',
        }),
        threshold: 10, // Less than 10 requests in 15 minutes
        evaluationPeriods: 2,
        comparisonOperator: cloudwatch.ComparisonOperator.LESS_THAN_THRESHOLD,
        treatMissingData: cloudwatch.TreatMissingData.BREACHING,
      });

      lowRequestCountAlarm.addAlarmAction(
        new cloudwatch.SnsAction(this.alertTopic)
      );
    }

    // Database connection errors (custom metric)
    const dbConnectionErrorsAlarm = new cloudwatch.Alarm(this, 'DbConnectionErrorsAlarm', {
      alarmName: `${config.applicationName}-db-connection-errors`,
      alarmDescription: 'Database connection errors detected',
      metric: new cloudwatch.Metric({
        namespace: 'COCS/Application',
        metricName: 'DatabaseConnectionErrors',
        dimensionsMap: {
          Environment: config.environment,
        },
        period: cdk.Duration.minutes(5),
        statistic: 'Sum',
      }),
      threshold: 5,
      evaluationPeriods: 1,
      comparisonOperator: cloudwatch.ComparisonOperator.GREATER_THAN_THRESHOLD,
      treatMissingData: cloudwatch.TreatMissingData.NOT_BREACHING,
    });

    dbConnectionErrorsAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );

    // Composite alarm for overall application health
    const applicationHealthAlarm = new cloudwatch.CompositeAlarm(this, 'ApplicationHealthAlarm', {
      alarmName: `${config.applicationName}-application-health`,
      alarmDescription: 'Overall application health check',
      compositeAlarmRule: cloudwatch.AlarmRule.anyOf(
        cloudwatch.AlarmRule.fromAlarm(highCpuAlarm, cloudwatch.AlarmState.ALARM),
        cloudwatch.AlarmRule.fromAlarm(highResponseTimeAlarm, cloudwatch.AlarmState.ALARM),
        cloudwatch.AlarmRule.fromAlarm(http5xxAlarm, cloudwatch.AlarmState.ALARM),
        cloudwatch.AlarmRule.fromAlarm(unhealthyHostsAlarm, cloudwatch.AlarmState.ALARM)
      ),
    });

    applicationHealthAlarm.addAlarmAction(
      new cloudwatch.SnsAction(this.alertTopic)
    );
  }
}