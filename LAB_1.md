
## Lab 1 - Get the code and load Cartridge

**Create a github account**
If you don't have a github account, go to [github.com](https://github.com/) and create one.

**Fork the following repositories:**
- [doa17-cartridge](https://github.com/chuymarin/doa17-cartridge)
- [doa17-infrastructure](https://github.com/chuymarin/doa17-infrastructure)
- [doa17-application](https://github.com/chuymarin/doa17-application)

**Clone the repositories in your local machine:**
- Create the directories `/projects/doa17/` in your user directory
- Open GitBash and go to `/projects/doa17/` directory
- Clone the 3 repositories from your github account

**Review and modify infrastructure repository template 03**
- Open the file: `03-aws-devops-workshop-environment-setup.template` this tempalte contains
  - Mappings: matches a key to a corresponding set of named values, and use functions to return a named value based on a specified key.
  - Parameters: definded values sepcified at stack launch time.
  - Resources: definition of the resources to be launched.
  - Outputs: values of the created resources.
  
**Review and modify infrastructure repository create-project.json file**
- Open the file `create-project.json`
- Replace `{ENVIRONMENT_NAME}` with your name or academy user in lower case, example:
```
"name": "{ENVIRONMENT_NAME}-project",
"name": "academy01-project",
```
Note: ENVIRONMENT_NAME will be used in the next steps
- save the changes and update your remot repository

**Review and modify cartridge repository urls file**
- Open the file `doa17-cartrdige/src/urls.txt`, add the following content and replacete the `{ENVIRONMENT_NAME}` with yours:
```
https://github.com/{ENVIRONMENT_NAME}/doa17-infrastructure.git
https://github.com/{ENVIRONMENT_NAME}/doa17-application.git
```
- save the changes and update your remote repository

**Create Project in Jenkins ADOP Platform**
- URL and access will be provided in the lab
- Go to Jenkins/DevOps_Academy/Project_Management
- Open Generate_Project Job, click in Build with Parameters, fill the parameters as follow:
  - PROJECT_NAME: specified value for ENVIRONMENT_NAME
  - Leave the other values empty
  - Build the Job
- Go to Jenkins/DevOps_Academy/ and you will see your directory created
- Go to Jenkins/DevOps_Academy/{your-directory}/Cartridge_Management
- Open Load_Cartridge Job, click in Build with Parameters, fill the parameters as follow:
  - CARTRIDGE_CLONE_URL: https://github.com/{github-user}/doa17-cartridge.git
  - Leave the other values empty
  - Build the Job
- Go to Jenkins/DevOps_Academy/{your-directory} and you will see some Jobs and Views created, let's review them.
