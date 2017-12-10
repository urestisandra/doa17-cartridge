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
```
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
aws deploy create-deployment-group --application-name ${ENVIRONMENT_NAME}-WebApp  --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name ${ENVIRONMENT_NAME}-ProdWebApp --ec2-tag-filters Key=Name,Value=${ENVIRONMENT_NAME}-ProdWebApp,Type=KEY_AND_VALUE --service-role-arn ${CODE_DEPLOY_ARN}

set -x'''.stripMargin()
    )
...
```
- This will create Deployment Group for Production Environment in AWS CodeDeploy
- save the changes and update your remote repository

**Reload Cartridge**
- Go to Jenkins/DevOps_Academy/{your-directory}/Cartridge_Management
- Open Load_Cartridge Job, click in Build with Parameters, fill the parameters as follow:
  - CARTRIDGE_CLONE_URL: https://github.com/{github-user}/doa17-cartridge.git
  - Leave the other values empty
- Build the Job

**Open the AWS Console and go to**
- Go to the AWS Console and access with your provided AWS User, use the region: N. Virginia (us-east-1)
- Go to CloudFormation service and click in stack and see the outputs from the following stacks:
  - DevOps-Workshop-Networking
  - DevopsWorkshop-raem-roles
- You will use these values while executing the pipeline

**Execute Pipeline**
- Go to Jenkins/DevOps_Academy/{your-directory}/
- Open on DOA17_Environment_Pipeline Job, click in Build with Parameters link, and fill the values as follow:
  - AWS_REGION: us-east-1
  - ENVIRONMENT_NAME: your selected name for your environment used in previous steps
  - WEB_APP_PROFILE: WebAppInstanceProfile value from DevOps-Workshop-Networking outputs
  - WEB_APP_SG: WebAppSG value from DevOps-Workshop-Networking outputs
  - PUBLIC_SUBNET: publicSubnet01 value from DevOps-Workshop-Networking outputs
  - CODE_DEPLOY_ARN: DeployRoleArn value from DevopsWorkshop-raem-roles outputs
- Execute the Job
- You can see the pipeline process in: Jenkins/DevOps_Academy/{your-directory}/DOA17_Environment_Pipeline

**Resources Created**
- If your pipeline ends successfully go to the AWS Console and verify the following
- In AWS EC2 you should have 2 Instances created with the names {ENVIRONMENT_NAME}-DevWebApp {ENVIRONMENT_NAME}-ProdWebApp
- In AWS S3 you should see a bucket named doa17-{ENVIRONMENT_NAME}
- In AWS CodeBuild you should see a project named {ENVIRONMENT_NAME}-project
- In AWS CodeDeploy you should see an application named {ENVIRONMENT_NAME}-WebApp and two deployment groups
  - {ENVIRONMENT_NAME}-DevWebApp
  - {ENVIRONMENT_NAME}-ProdApp
- If you do not have permissions to see these services, ask your instructor

**Troubleshooting**
- If one of the jobs from the pipeline fails, check if you missed some steps and change accordingly, if the problem persists ask your Instructor for a solution.
