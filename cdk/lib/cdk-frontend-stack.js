"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CdkFrontendStack = void 0;
const cdk = require("aws-cdk-lib");
const s3 = require("aws-cdk-lib/aws-s3");
const s3deploy = require("aws-cdk-lib/aws-s3-deployment");
const cloudfront = require("aws-cdk-lib/aws-cloudfront");
const origins = require("aws-cdk-lib/aws-cloudfront-origins");
class CdkFrontendStack extends cdk.Stack {
    constructor(scope, id, props) {
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
exports.CdkFrontendStack = CdkFrontendStack;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiY2RrLWZyb250ZW5kLXN0YWNrLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiY2RrLWZyb250ZW5kLXN0YWNrLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiI7OztBQUFBLG1DQUFtQztBQUVuQyx5Q0FBeUM7QUFDekMsMERBQTBEO0FBQzFELHlEQUF5RDtBQUN6RCw4REFBOEQ7QUFFOUQsTUFBYSxnQkFBaUIsU0FBUSxHQUFHLENBQUMsS0FBSztJQUM3QyxZQUFZLEtBQWdCLEVBQUUsRUFBVSxFQUFFLEtBQXNCO1FBQzlELEtBQUssQ0FBQyxLQUFLLEVBQUUsRUFBRSxFQUFFLEtBQUssQ0FBQyxDQUFDO1FBRXhCLE1BQU0sVUFBVSxHQUFHLElBQUksRUFBRSxDQUFDLE1BQU0sQ0FBQyxJQUFJLEVBQUUsa0JBQWtCLEVBQUU7WUFDekQsb0JBQW9CLEVBQUUsWUFBWTtZQUNsQyxzQ0FBc0M7WUFDdEMsZ0JBQWdCLEVBQUUsSUFBSTtZQUN0QixpQkFBaUIsRUFBRSxJQUFJLEVBQUUsQ0FBQyxpQkFBaUIsQ0FBQztnQkFDMUMsZUFBZSxFQUFFLEtBQUs7Z0JBQ3RCLGdCQUFnQixFQUFFLEtBQUs7Z0JBQ3ZCLGlCQUFpQixFQUFFLEtBQUs7Z0JBQ3hCLHFCQUFxQixFQUFFLEtBQUs7YUFDN0IsQ0FBQztZQUNGLGFBQWEsRUFBRSxHQUFHLENBQUMsYUFBYSxDQUFDLE9BQU87WUFDeEMsaUJBQWlCLEVBQUUsSUFBSTtTQUN4QixDQUFDLENBQUM7UUFFSCxNQUFNLFlBQVksR0FBRyxJQUFJLFVBQVUsQ0FBQyxZQUFZLENBQUMsSUFBSSxFQUFFLGtCQUFrQixFQUFFO1lBQ3pFLGVBQWUsRUFBRTtnQkFDZixNQUFNLEVBQUUsSUFBSSxPQUFPLENBQUMscUJBQXFCLENBQUMsVUFBVSxDQUFDO2dCQUNyRCxvQkFBb0IsRUFBRSxVQUFVLENBQUMsb0JBQW9CLENBQUMsaUJBQWlCO2FBQ3hFO1lBQ0QsaUJBQWlCLEVBQUUsWUFBWTtTQUNoQyxDQUFDLENBQUM7UUFFSCxJQUFJLFFBQVEsQ0FBQyxnQkFBZ0IsQ0FBQyxJQUFJLEVBQUUsa0JBQWtCLEVBQUU7WUFDdEQsT0FBTyxFQUFFLENBQUMsUUFBUSxDQUFDLE1BQU0sQ0FBQyxLQUFLLENBQUMsa0JBQWtCLENBQUMsQ0FBQztZQUNwRCxpQkFBaUIsRUFBRSxVQUFVO1lBQzdCLGlCQUFpQixFQUFFLENBQUMsSUFBSSxDQUFDO1lBQ3pCLFlBQVksRUFBRSxZQUFZO1NBQzNCLENBQUMsQ0FBQztRQUlILElBQUksR0FBRyxDQUFDLFNBQVMsQ0FBQyxJQUFJLEVBQUUsZUFBZSxFQUFFO1lBQ3ZDLEtBQUssRUFBRSxZQUFZLENBQUMsc0JBQXNCO1lBQzFDLFdBQVcsRUFBRSwrQkFBK0I7WUFDNUMsVUFBVSxFQUFFLGVBQWU7U0FDNUIsQ0FBQyxDQUFDO0lBQ0wsQ0FBQztDQUNGO0FBekNELDRDQXlDQyIsInNvdXJjZXNDb250ZW50IjpbImltcG9ydCAqIGFzIGNkayBmcm9tICdhd3MtY2RrLWxpYic7XG5pbXBvcnQgeyBDb25zdHJ1Y3QgfSBmcm9tICdjb25zdHJ1Y3RzJztcbmltcG9ydCAqIGFzIHMzIGZyb20gJ2F3cy1jZGstbGliL2F3cy1zMyc7XG5pbXBvcnQgKiBhcyBzM2RlcGxveSBmcm9tICdhd3MtY2RrLWxpYi9hd3MtczMtZGVwbG95bWVudCc7XG5pbXBvcnQgKiBhcyBjbG91ZGZyb250IGZyb20gJ2F3cy1jZGstbGliL2F3cy1jbG91ZGZyb250JztcbmltcG9ydCAqIGFzIG9yaWdpbnMgZnJvbSAnYXdzLWNkay1saWIvYXdzLWNsb3VkZnJvbnQtb3JpZ2lucyc7XG5cbmV4cG9ydCBjbGFzcyBDZGtGcm9udGVuZFN0YWNrIGV4dGVuZHMgY2RrLlN0YWNrIHtcbiAgY29uc3RydWN0b3Ioc2NvcGU6IENvbnN0cnVjdCwgaWQ6IHN0cmluZywgcHJvcHM/OiBjZGsuU3RhY2tQcm9wcykge1xuICAgIHN1cGVyKHNjb3BlLCBpZCwgcHJvcHMpO1xuXG4gICAgY29uc3Qgc2l0ZUJ1Y2tldCA9IG5ldyBzMy5CdWNrZXQodGhpcywgJ1N0YXRpY1NpdGVCdWNrZXQnLCB7XG4gICAgICB3ZWJzaXRlSW5kZXhEb2N1bWVudDogJ2luZGV4Lmh0bWwnLFxuICAgICAgLy8gd2Vic2l0ZUVycm9yRG9jdW1lbnQ6ICdlcnJvci5odG1sJyxcbiAgICAgIHB1YmxpY1JlYWRBY2Nlc3M6IHRydWUsIFxuICAgICAgYmxvY2tQdWJsaWNBY2Nlc3M6IG5ldyBzMy5CbG9ja1B1YmxpY0FjY2Vzcyh7XG4gICAgICAgIGJsb2NrUHVibGljQWNsczogZmFsc2UsXG4gICAgICAgIGlnbm9yZVB1YmxpY0FjbHM6IGZhbHNlLFxuICAgICAgICBibG9ja1B1YmxpY1BvbGljeTogZmFsc2UsXG4gICAgICAgIHJlc3RyaWN0UHVibGljQnVja2V0czogZmFsc2UsXG4gICAgICB9KSxcbiAgICAgIHJlbW92YWxQb2xpY3k6IGNkay5SZW1vdmFsUG9saWN5LkRFU1RST1ksIFxuICAgICAgYXV0b0RlbGV0ZU9iamVjdHM6IHRydWUsXG4gICAgfSk7XG5cbiAgICBjb25zdCBkaXN0cmlidXRpb24gPSBuZXcgY2xvdWRmcm9udC5EaXN0cmlidXRpb24odGhpcywgJ1NpdGVEaXN0cmlidXRpb24nLCB7XG4gICAgICBkZWZhdWx0QmVoYXZpb3I6IHtcbiAgICAgICAgb3JpZ2luOiBuZXcgb3JpZ2lucy5TM1N0YXRpY1dlYnNpdGVPcmlnaW4oc2l0ZUJ1Y2tldCksXG4gICAgICAgIHZpZXdlclByb3RvY29sUG9saWN5OiBjbG91ZGZyb250LlZpZXdlclByb3RvY29sUG9saWN5LlJFRElSRUNUX1RPX0hUVFBTLCBcbiAgICAgIH0sXG4gICAgICBkZWZhdWx0Um9vdE9iamVjdDogJ2luZGV4Lmh0bWwnLFxuICAgIH0pO1xuXG4gICAgbmV3IHMzZGVwbG95LkJ1Y2tldERlcGxveW1lbnQodGhpcywgJ0RlcGxveVN0YXRpY1NpdGUnLCB7XG4gICAgICBzb3VyY2VzOiBbczNkZXBsb3kuU291cmNlLmFzc2V0KCcuLi93ZWItYXBwL2J1aWxkJyldLFxuICAgICAgZGVzdGluYXRpb25CdWNrZXQ6IHNpdGVCdWNrZXQsXG4gICAgICBkaXN0cmlidXRpb25QYXRoczogWycvKiddLCBcbiAgICAgIGRpc3RyaWJ1dGlvbjogZGlzdHJpYnV0aW9uLFxuICAgIH0pO1xuXG4gICAgXG5cbiAgICBuZXcgY2RrLkNmbk91dHB1dCh0aGlzLCAnQ2xvdWRGcm9udFVybCcsIHtcbiAgICAgIHZhbHVlOiBkaXN0cmlidXRpb24uZGlzdHJpYnV0aW9uRG9tYWluTmFtZSxcbiAgICAgIGRlc2NyaXB0aW9uOiAnVGhlIFVSTCBvZiB0aGUgc3RhdGljIHdlYnNpdGUnLFxuICAgICAgZXhwb3J0TmFtZTogJ0Nsb3VkRnJvbnRVcmwnXG4gICAgfSk7XG4gIH1cbn1cbiJdfQ==