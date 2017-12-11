## Lab 3 - Update CI/CD Pipeline Code

**Modify DOA17_Code_Build Job**

- Open the file: `/jenkins/jobs/dsl/doa17-ci-cd-pipeline.groovy` this file contains the ci/cd pipeline and jobs
- Modify parameters in Job DOA17_Code_Build Code:
```
# FROM:
  parameters{
    stringParam("KEY", "Value", 'Description')
  }
# TO:
  parameters{
    stringParam("AWS_REGION",'us-east-1',"Default AWS Region")
    stringParam("ENVIRONMENT_NAME",'',"Name of your Environment")
    stringParam("S3_BUCKET",'',"Web App Instance Profile from DevOps-Workshop-Networking stack")
  }
```
- Add scm step:
```
  ...
  label("docker")
  scm{
    git{
      remote{
        url(applicationRepository)
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

echo "[INFO] Building Application Code"
aws codebuild start-build --project-name ${ENVIRONMENT_NAME}-project
sleep 35s

echo "[INFO] Getting Code Build eTAG"
BUILD_ETAG=$(aws s3api head-object --bucket doa17-chuymarin --key WebAppOutputArtifact.zip --query \'ETag\' --output text)
echo "BUILD_ETAG=$BUILD_ETAG" >> properties_file.txt

echo "[INFO] Registering Revision for eTAG ${BUILD_ETAG}"
aws deploy register-application-revision --application-name ${ENVIRONMENT_NAME}-WebApp --description "Revison ${BUILD_NUMBER}" --s3-location bucket=${S3_BUCKET},key=WebAppOutputArtifact.zip,bundleType=zip,eTag=${BUILD_ETAG}

set -x'''.stripMargin()
    )
...
```
- This will create an application build and a revision for the build

**Modify DOA17_Code_Deploy_Development Job**

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

echo "[INFO] Deploying Application to ${ENVIRONMENT_NAME}-DevWebApp"
aws deploy create-deployment --application-name ${ENVIRONMENT_NAME}-WebApp --deployment-group-name ${ENVIRONMENT_NAME}-DevWebApp --description "Applicacion Build ${BUILD_NUMBER}" --s3-location bucket=${S3_BUCKET},key=WebAppOutputArtifact.zip,bundleType=zip,eTag=${BUILD_ETAG}

sleep 30s

set -x'''.stripMargin()
    )
...
```
- This will create a new deployment for the Development Environment

**Modify DOA17_Code_Deploy_Production Job**

- Modify the parameters same as previous job
- Add the following code to the shell step:
```
...
      shell('''
set +x

export AWS_DEFAULT_REGION=$AWS_REGION
echo "[INFO] Default region is set to $AWS_DEFAULT_REGION"

echo "[INFO] Deploying Application to ${ENVIRONMENT_NAME}-ProdWebApp"
aws deploy create-deployment --application-name ${ENVIRONMENT_NAME}-WebApp --deployment-group-name ${ENVIRONMENT_NAME}-ProdWebApp --description "Applicacion Build ${BUILD_NUMBER}" --s3-location bucket=${S3_BUCKET},key=WebAppOutputArtifact.zip,bundleType=zip,eTag=${BUILD_ETAG}

sleep 30s

set -x'''.stripMargin()
    )
...
```
- This will create a new deployment for the Development Environment

**Reload Cartridge**
- Go to Jenkins/DevOps_Academy/{your-directory}/Cartridge_Management
- Open Load_Cartridge Job, click in Build with Parameters, fill the parameters as follow:
  - CARTRIDGE_CLONE_URL: https://github.com/{github-user}/doa17-cartridge.git
  - Leave the other values empty
- Build the Job
- Repeat this step each time you make changes to the cartridge repository

**Executing the CI/CD Pipeline**
- Go to Jenkins/DevOps_Academy/{your-directory}/
- Open on DOA17_CI_CD_Pipeline Job, click in Build with Parameters link, and fill the values as follow:
  - AWS_REGION: us-east-1
  - ENVIRONMENT_NAME: your selected name for your environment used in previous steps
  - S3_BUCKET: S3Bucket value from your DevopsWorkshop-{ENVIRONMENT_NAME} stack
- Execute the Job
- You can see the pipeline process in: Jenkins/DevOps_Academy/{your-directory}/DOA17_CI_CD_Pipeline

**Verify your Deployments**
- If your pipeline ends successfully go to the AWS Console and verify the following
- In AWS CodeDeploy, select your application
- Verify the status of your deployment groups, this should be **Success**

**Troubleshooting**
- If your CI/CD Pipeline fails, check the code for missing values, if problem persist ask your Instructor
- If your CI/CD Pipeline success but your deployment fails, ask your Instructor for troubleshooting
