## Lab 2 - Update Environment Pipeline Code

**Modify DOA17_Launch_Environment Job**

- Open the file: `/jenkins/jobs/dsl/doa17-environment-pipeline.groovy` this file contains the environment pipeline and jobs
- Modify parameters in Job DOA17_Launch_Environment Code:
```
# FROM:
  parameters{
    stringParam("KEY", "Value", 'Description')
  }
# TO:
  parameters{
    stringParam("AWS_REGION",'us-east-1',"Default AWS Region")
    stringParam("ENVIRONMENT_NAME",'',"Name of your Environment")
    stringParam("WEB_APP_PROFILE",'',"Web App Instance Profile from DevOps-Workshop-Networking stack")
    stringParam("WEB_APP_SG",'',"Web App SG from DevOps-Workshop-Networking stack")
    stringParam("PUBLIC_SUBNET",'',"Public Subnet from DevOps-Workshop-Networking stack")
    stringParam("CODE_DEPLOY_ARN",'',"IAM Role ARN from DevopsWorkshop-raem-roles stack")
  }
```
- Add scm step:
```
  ...
  label("docker")
  scm{
    git{
      remote{
        url(infrastructureRepository)
        credentials("adop-jenkins-master")
      }
      branch("*/master")
    }
  }
  steps {
  ...
```
- Add the following code to the shell step:
```
...
      shell('''
set +x

export AWS_DEFAULT_REGION=$AWS_REGION
echo "[INFO] Default region is set to $AWS_DEFAULT_REGION"

echo "[INFO] Creating DevopsWorkshop-${ENVIRONMENT_NAME} Stack"
aws cloudformation create-stack --stack-name DevopsWorkshop-${ENVIRONMENT_NAME} --template-body file:///${WORKSPACE}/03-aws-devops-workshop-environment-setup.template --capabilities CAPABILITY_IAM \
--parameters  ParameterKey=EnvironmentName,ParameterValue=$ENVIRONMENT_NAME \
              ParameterKey=WebAppInstanceProfile,ParameterValue=$WEB_APP_PROFILE \
              ParameterKey=WebAppSG,ParameterValue=$WEB_APP_SG \
              ParameterKey=publicSubnet01,ParameterValue=$PUBLIC_SUBNET

echo "[INFO] Wating for DevopsWorkshop-${ENVIRONMENT_NAME} Stack"
aws cloudformation wait stack-create-complete --stack-name DevopsWorkshop-${ENVIRONMENT_NAME}
echo "[INFO] DevopsWorkshop-${ENVIRONMENT_NAME} Stack Created"

echo "[INFO] Creating Code Build Project"
aws codebuild create-project --cli-input-json file://${WORKSPACE}/create-project.json

set -x'''.stripMargin()
    )
...
```
- This will create a new stack using the `03-aws-devops-workshop-environment-setup.template` file using AWS CloudFormation and will create the build project using AWS CodeBuild

**Modify DOA17_Create_Application Job**

- Modify the parameters same as first job, but this time the first parameter default value should be empty:
```
...
stringParam("AWS_REGION",'',"Default AWS Region")
...
```
- Add the following code to the shell step:
...
      shell('''
set +x

export AWS_DEFAULT_REGION=$AWS_REGION
echo "[INFO] Default region is set to $AWS_DEFAULT_REGION"

echo "[INFO] Creating Code Deploy Application ${ENVIRONMENT_NAME}-WebApp"
aws deploy create-application --application-name ${ENVIRONMENT_NAME}-WebApp

set -x'''.stripMargin()
      )
...
```
- This will crate an Application is AWS CodeDeploy

**Modify DOA17_Create_Development_Group Job**

- Modify the parameters same as the previous job
- Add the following code to the shell step:
```
...
    shell('''
set +x

export AWS_DEFAULT_REGION=$AWS_REGION
echo "[INFO] Default region is set to $AWS_DEFAULT_REGION"

echo "[INFO] Creating Code Deploy Deployment Group ${ENVIRONMENT_NAME}-DevWebApp"
aws deploy create-deployment-group --application-name ${ENVIRONMENT_NAME}-WebApp  --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name ${ENVIRONMENT_NAME}-DevWebApp --ec2-tag-filters Key=Name,Value=${ENVIRONMENT_NAME}-DevWebApp,Type=KEY_AND_VALUE --service-role-arn ${CODE_DEPLOY_ARN}

echo "[INFO] Creating Code Build Project"
aws codebuild create-project --cli-input-json file://${WORKSPACE}/create-project.json

set -x'''.stripMargin()
    )
...
```
- This will create Deployment Group for Development Environment in AWS CodeDeploy

**Modify DOA17_Create_Production_Group Job**

- Modify the parameters same as the previous job
- Add the following code to the shell step:
```
...
    shell('''
set +x

export AWS_DEFAULT_REGION=$AWS_REGION
echo "[INFO] Default region is set to $AWS_DEFAULT_REGION"

echo "[INFO] Creating Code Deploy Deployment Group ${ENVIRONMENT_NAME}-ProdWebApp"
aws deploy create-deployment-group --application-name ${ENVIRONMENT_NAME}-WebApp  --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name ${ENVIRONMENT_NAME}-ProdWebApp --ec2-tag-filters Key=Name,Value=${ENVIRONMENT_NAME}-DevWebApp,Type=KEY_AND_VALUE --service-role-arn ${CODE_DEPLOY_ARN}

set -x'''.stripMargin()
    )
...
```
- This will create Deployment Group for Production Environment in AWS CodeDeploy
