## Basic AI Service with Paywall

You want to sell AI products behind a paywall?

Here's a demo project implementing the whole stack, including calling AWS LLM models and user authentication and payments.

We use React frontend, AWS backend, and paypal integration.

## How to build

- Clone this project.
- To build frontend go into web-app folder and run `npm run build`
- To build backend code go into backend folder and run `mvn clean package`
- To build and deploy AWS infrastructure, go into cdk folder and run `npm run build` then `cdk deploy --all`

Now your site is live!

## Details

### Frontend React
You need to install npm and some dependencies.

```npm install @mui/material @emotion/react @emotion/styled @mui/icons-material @paypal/react-paypal-js```

You need to supply a `.env` file at the ROOT of folder with the following variables.`REACT_APP_PAYPAL_CLIENT_ID` and `REACT_APP_API_BASE_URL`

`REACT_APP_PAYPAL_CLIENT_ID` is your paypal client id. This can be a sandbox id. 

`REACT_APP_API_BASE_URL` is your backend API url. You can find this in the AWS APIGW console once you've deployed.

There may be more I've forgotten but your error messages will tell you what's missing. Then run `npm install @somePackage`

You also need a `firebaseConfig.js`. Log into Firebase console in settings. Look for firebaseConfig for javascript.

When you run `npm start` it demos your page.

When you run `npm run build` it creates a build folder with your deployable files.

### Java backend logic
You need to install maven.

All dependencies are declared in the `pom.xml`

You need to have a firebase account. You need to provide a firebase sdk json that you can download from the firebase console. Name it `src/resources/firebase-adminsdk.json`

When you run `mvn test` it runs the unit tests.

When you run `mvn clean packages` it creates target folder where `.jar` file is needed for deployment.


### AWS Backend Infrastructure
You need an AWS account. Then get the AWS CLI and run `aws configure`

You need a paypal account. You need the clientId and the secret. You can provide these as constants in the CDK code. See cdk-stack.ts. These will be used by java backend. Only provide them here IF THIS CODE WILL NOT BE PUBLIC. This method is used for development as the lowest effort way to supply credentials. Otherwise look into AWS credentials managers.

There's kind of a logic loop. The backend needs to know the URL of the frontend for CORS (this will happen automatically by cdk outputs). The frontend needs to know the URL of the backend. You need to manually supply this in the environment variables in your frontend. 

First run `npm run build` from the CDK folder. Then run `cdk deploy --all`. Then once a deployment finishes, go to AWS, then to API Gateway, the URL should be listed for your backend. Copy the URL and provide it as an environment variable `REACT_APP_API_BASE_URL` for your frontend. Then `npm run build` to re-build your frontend (from web-app folder). (*NOTE* when you run `npm run build` from cdk folder, it builds the AWS infrastructure code. when you run `npm run build` from the web-app folder, it builds the frontend code ... don't get it mixed up). From the cdk folder run again `cdk deploy --all`, and this completes the logic loop.

You need to populate some items in your paymentsTable. I re-used payments table to hold a few products, transactions, and access tokens. Look at `Backend/../Product.java` and `Backend/../ProductTest.java` to see what product entry should look like in the table. Create some products. The IDs need to match products on the `payments.js` page in `web-app` package.

## Notes

Sorry, I cannot support ongoing development for this. Good luck!

Debugging backend, go to CloudWatch then logs. Then find your backendApi.

Debugging frontend, go to console for logs.

