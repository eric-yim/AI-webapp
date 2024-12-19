import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3deploy from 'aws-cdk-lib/aws-s3-deployment';
import * as cloudfront from 'aws-cdk-lib/aws-cloudfront';
import * as origins from 'aws-cdk-lib/aws-cloudfront-origins';

export class CdkFrontendStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const siteBucket = new s3.Bucket(this, 'StaticSiteBucket', {
      websiteIndexDocument: 'index.html',
      // websiteErrorDocument: 'error.html',
      publicReadAccess: true, 
      blockPublicAccess: new s3.BlockPublicAccess({
        blockPublicAcls: false,
        ignorePublicAcls: false,
        blockPublicPolicy: false,
        restrictPublicBuckets: false,
      }),
      removalPolicy: cdk.RemovalPolicy.DESTROY, 
      autoDeleteObjects: true,
    });

    const distribution = new cloudfront.Distribution(this, 'SiteDistribution', {
      defaultBehavior: {
        origin: new origins.S3StaticWebsiteOrigin(siteBucket),
        viewerProtocolPolicy: cloudfront.ViewerProtocolPolicy.REDIRECT_TO_HTTPS, 
      },
      defaultRootObject: 'index.html',
    });

    new s3deploy.BucketDeployment(this, 'DeployStaticSite', {
      sources: [s3deploy.Source.asset('../web-app/build')],
      destinationBucket: siteBucket,
      distributionPaths: ['/*'], 
      distribution: distribution,
    });

    

    new cdk.CfnOutput(this, 'CloudFrontUrl', {
      value: distribution.distributionDomainName,
      description: 'The URL of the static website',
      exportName: 'CloudFrontUrl'
    });
  }
}
