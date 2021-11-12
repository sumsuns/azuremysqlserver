#!groovy



TARGET_EMAIL = 'xxx.yyy@outlook.com'



ORG_NAME          = "sumsuns"
PROJECT_NAME     = "azuremysqlserver"


GIT_URL          = "github.com"
GITHUB_URL       = "${GIT_URL}:ORG_NAME/${PROJECT_NAME}"


TERRAFORM_DIR    = "terraform/services/back"
SUBS_ID           = "azuresubid"
CLIENT_ID          = "azuresubclientid"
CLIENT_SECRET       = "azuresubclientsecret"
TENANT_ID       = "azuresubclienttenantid"

GITHUB_TOKEN      = "githubtokenxxx"
GITHUB_CREDENTIAL = "GithubToken-02"
GITHUB_USERNAME   = "GITHUB_USERNAME"

// SCM Checkout
// -------------------------------------------------------------------------
def checkoutCode() {
  def REPOSITORY = "https://${GITHUB_URL}"

  checkout scm
  withCredentials([usernamePassword(credentialsId: GITHUB_CREDENTIAL, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    sh "echo \"https://${USERNAME}:${PASSWORD}@${GIT_URL}\" > ~/.git-credentials"
  }
  sh "git config --global credential.helper 'store --file ~/.git-credentials'"
  sh "git config --global user.name \"Run Team\" && git config --global user.email \"${PROJECT_NAME}@engie.com\""
  sh "git config --global push.default matching"
}


// Pipieline definition
// --------------------------------------------------
pipeline {
  agent { label "agent-infra" }

 

stages {

      stage('azure_login') {
           steps {
             script {
                 container('toolbox') {
                  
                    withCredentials([azureServicePrincipal(credentialsId: 'credentials_id',
                                    subscriptionIdVariable: 'SUBS_ID',
                                    clientIdVariable: 'CLIENT_ID',
                                    clientSecretVariable: 'CLIENT_SECRET',
                                    tenantIdVariable: 'TENANT_ID')]) {
    sh 'az login --service-principal -u $CLIENT_ID -p $CLIENT_SECRET -t $TENANT_ID'
}
                 }
              }
            }

         }

        stage('Checkout code') {
          steps {
            script {
              checkoutCode()
            }
          }
        }
        stage('Init'){
          steps {
            script {
              dir("${TERRAFORM_DIR}"){
                sh 'terraform init -backend-config=prod.backend.tfvars -reconfigure -var-file=prod.terraform.tfvars'
              }
            }
          }
        }
        stage('Plan'){
          steps {
            script {
              dir("${TERRAFORM_DIR}"){

                sh 'terraform plan -var-file=dev.terraform.tfvars'
              }
            }
          }
        }
        stage('Apply'){
          steps {
            script {
              dir("${TERRAFORM_DIR}"){
                echo("Do Build for ${ENV_AWS} - ${SELECTED_ENV};'")
                sh 'terraform apply -var-file=prod.terraform.tfvars'
              }
            }
          }
        }
      }
    }
  }

  post {
    success {
      script {
        currentBuild.result = 'SUCCESS'
        sendMailResult('success', TARGET_EMAIL)
        echo "The Build result is : ${currentBuild.result}."
      }
    }

    failure {
      script {
        currentBuild.result = 'FAILURE'
        sendMailResult('success', TARGET_EMAIL)
        echo "The Build result is : ${currentBuild.result}."
      }
    }

    aborted {
      script {
        currentBuild.result = 'FAILURE'
        sendMailResult('success', TARGET_EMAIL)
        echo "The Build result is : ${currentBuild.result}."
      }
    }
  }
}
