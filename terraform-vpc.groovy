properties([
    parameters([
        choice(choices: ['apply', 'destroy'], description: 'Choose Terraform Action', name: 'action'),
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an Environment', name: 'environment')
    ])
])

if(params.environment == "dev"){
    region = "us-east-1"
}
else if(params.environment == "qa"){
    region = "us-east-2"
}
else if(params.environment == "prod"){
    region = "us-west-2"
}

node{
    stage("Pull Terraform Repo"){
        git 'https://github.com/ikambarov/terraform-vpc.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_DEFAULT_REGION=${region}"]) {
            stage("Init"){
                sh """
                    #!/bin/bash
                    source ./setenv.sh ${params.environment}.tfvars
                    terraform init
                """
            }

            if(params.action == "apply"){
                stage("Apply"){
                    sh """
                        terraform apply -auto-approve -var-file ${params.environment}.tfvars
                    """
                }
            }

            if(params.action == "destroy"){
                stage("Destoy"){
                    sh """
                        terraform destroy -auto-approve -var-file ${params.environment}.tfvars
                    """
                }
            }
        }
    }    
}