// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"

// Variables
def applicationRepository = "ssh://jenkins@gerrit:29418/${PROJECT_NAME}/doa17-application"

// Views
def doa17CICDPipeline = buildPipelineView(projectFolderName + "/DOA17_CI_CD_Pipeline")

// Jobs DOA17_CI_CD_Pipeline
def doa17ApplicationRepository = freeStyleJob(projectFolderName + "/DOA17_Application_Repository")
def doa17CodeBuild = freeStyleJob(projectFolderName + "/DOA17_Code_Build")
def doa17CodeRevision = freeStyleJob(projectFolderName + "/DOA17_Code_Revision")
def doa17CodeDeployDevelopment = freeStyleJob(projectFolderName + "/DOA17_Code_Deploy_Development")
def doa17ApplicationTest = freeStyleJob(projectFolderName + "/DOA17_Application_Test")
def doa17CodeDeployProduction = freeStyleJob(projectFolderName + "/DOA17_Code_Deploy_Production")

// DOA17_CI_CD_Pipeline
doa17EnvironmentPipeline.with{
    title('DOA17 CI CD Pipeline')
    displayedBuilds(5)
    selectedJob(projectFolderName + "/DOA17_Application_Repository")
    showPipelineDefinitionHeader()
    alwaysAllowManualTrigger()
    refreshFrequency(5)
}

// Job DOA17_Application_Repository
doa17ApplicationRepository.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
  publishers{
    downstreamParameterized{
      trigger(projectFolderName + "/DOA17_Code_Build"){
        condition("UNSTABLE_OR_BETTER")
        parameters{
          currentBuild()
        }
      }
    }
  }
}

// Job DOA17_Code_Build
doa17CodeBuild.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
  publishers{
    downstreamParameterized{
      trigger(projectFolderName + "/DOA17_Code_Revision"){
        condition("UNSTABLE_OR_BETTER")
        parameters{
          currentBuild()
        }
      }
    }
  }
}

// Job DOA17_Code_Revision
doa17CodeRevision.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
  publishers{
    downstreamParameterized{
      trigger(projectFolderName + "/DOA17_Code_Deploy_Development"){
        condition("UNSTABLE_OR_BETTER")
        parameters{
          currentBuild()
        }
      }
    }
  }
}

// Job DOA17_Code_Deploy_Development
doa17CodeDeployDevelopment.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
  publishers{
    downstreamParameterized{
      trigger(projectFolderName + "/DOA17_Application_Test"){
        condition("UNSTABLE_OR_BETTER")
        parameters{
          currentBuild()
        }
      }
    }
  }
}

// Job DOA17_Application_Test
doa17ApplicationTest.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
  publishers{
    downstreamParameterized{
      trigger(projectFolderName + "/DOA17_Code_Deploy_Production"){
        condition("UNSTABLE_OR_BETTER")
        parameters{
          currentBuild()
        }
      }
    }
  }
}

// Job DOA17_Code_Deploy_Production
doa17CodeDeployProduction.with{
  description("Job Description")
  environmentVariables {
    env('WORKSPACE_NAME', workspaceFolderName)
    env('PROJECT_NAME', projectFolderName)
  }
  parameters{
    stringParam("KEY",'Description',"Value")
  }
  wrappers {
    preBuildCleanup()
    maskPasswords()
  }
  label("docker")
    steps {
    shell('''
set +x

set -x'''.stripMargin()
    )
  }
}