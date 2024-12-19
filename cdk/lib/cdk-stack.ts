import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as logs from 'aws-cdk-lib/aws-logs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';

const PAYPAL_SANDBOX = SeeREADME;
const PAYPAL_SANDBOX_SECRET = SeeREADME;

export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const userTable = new dynamodb.Table(this, 'UserTable', {
      partitionKey: { name: 'uid', type: dynamodb.AttributeType.STRING },
      tableName: 'UserTable',
      removalPolicy: cdk.RemovalPolicy.RETAIN,
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST
    });

    const paymentsTable = new dynamodb.Table(this, 'PaymentsTable', {
      partitionKey: { name: 'paymentProvider', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'uidOrderId', type: dynamodb.AttributeType.STRING },  
      tableName: 'PaymentsTable',
      removalPolicy: cdk.RemovalPolicy.RETAIN, 
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST,
    });

    const cfnTable = paymentsTable.node.defaultChild as dynamodb.CfnTable;
    cfnTable.addPropertyOverride('TimeToLiveSpecification', {
      AttributeName: 'tokenTTL',
      Enabled: true,
    });

    const javaLambda = new lambda.Function(this, 'resumeAIBackend', {
      runtime: lambda.Runtime.JAVA_17, 
      handler: 'com.resumeai.LambdaHandler::handleRequest', 
      code: lambda.Code.fromAsset('../backend/target/backend-1.0-SNAPSHOT.jar'),
      memorySize: 2000, 
      timeout: cdk.Duration.seconds(30),
      logRetention: logs.RetentionDays.ONE_YEAR,
      environment: {
        USER_TABLE: userTable.tableName, 
        PRODUCT_TABLE: paymentsTable.tableName, 
        PAYPAL_APIKEY: PAYPAL_SANDBOX,
        PAYPAL_SECRET: PAYPAL_SANDBOX_SECRET,
      },
    });

    userTable.grantReadWriteData(javaLambda);
    paymentsTable.grantReadWriteData(javaLambda);

    javaLambda.addToRolePolicy(new iam.PolicyStatement({
      actions: ['dynamodb:BatchGetItem', 'dynamodb:BatchWriteItem'],
      resources: [userTable.tableArn, paymentsTable.tableArn],
    }));

    javaLambda.addToRolePolicy(new iam.PolicyStatement({
      actions: ['bedrock:InvokeModel'],
      resources: ['*'],
    }));
    
    const backendApi = new apigateway.RestApi(this, 'backendApi', {
      restApiName: 'backendApi',
      cloudWatchRole: true
    });


    const integration = new apigateway.LambdaIntegration(javaLambda);

    const usersApi = backendApi.root.addResource('users');
    usersApi.addMethod('POST', integration);
    usersApi.addMethod("GET", integration);

    const productsApi = backendApi.root.addResource('products');
    productsApi.addMethod('POST', integration);

    const productsCaptureApi = backendApi.root.addResource('products-capture');
    productsCaptureApi.addMethod('POST', integration);

    backendApi.addUsagePlan('UsagePlan',
      {
        name: 'DefaultThrottling',
        throttle: {
          rateLimit: 1000, // average requests per second over an extended period of time
          burstLimit: 2500 // Limit over a few seconds
        }
      }
    )

    const cloudFrontUrl = 'https://' + cdk.Fn.importValue('CloudFrontUrl');
    const corsOptions: apigateway.CorsOptions = {
      allowOrigins: [ 
        cloudFrontUrl,
      ], 
      allowMethods: ['GET', 'POST', 'OPTIONS'],
      allowHeaders: ['Content-Type', 'Authorization', 'Access-Control-Allow-Origin', 'Access-Control-Allow-Credentials']
    };
    usersApi.addCorsPreflight(corsOptions);
    productsApi.addCorsPreflight(corsOptions);
    productsCaptureApi.addCorsPreflight(corsOptions);

    javaLambda.grantInvoke(new iam.ServicePrincipal('apigateway.amazonaws.com'));
  }
}
