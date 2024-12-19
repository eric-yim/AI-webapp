import * as cdk from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import * as Cdk from '../lib/cdk-stack';
import * as CdkFrontend from '../lib/cdk-frontend-stack';


test('Backend has expected resources', () => {
  const app = new cdk.App();
  const stack = new Cdk.CdkStack(app, 'MyTestStack');
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::Lambda::Function',{});
  template.hasResourceProperties('AWS::ApiGateway::RestApi',{});
  template.hasResourceProperties('AWS::DynamoDB::Table',{});
});

test('Frontend has expected resources', () => {
  const app = new cdk.App();
  const frontendStack = new CdkFrontend.CdkFrontendStack(app, 'MyTestFrontendStack');
  const template = Template.fromStack(frontendStack);

  template.hasResource('AWS::CloudFront::Distribution', {});
  template.hasResourceProperties('AWS::S3::Bucket', {});
  template.hasResourceProperties('Custom::CDKBucketDeployment', {});
});


