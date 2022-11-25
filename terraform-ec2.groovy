properties([
    parameters([
        choice(choices: ['apply', 'destroy'], description: 'Choose Terraform Action', name: 'action'),
        choice(choices: ['dev', 'qa', 'prod'], description: 'Choose an Environment', name: 'environment'),
        string(description: 'Enter AMI ID', name: 'ami_id', trim: true)
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

tfvars = """
s3_bucket = \"jenkins-terraform-evolvecybertraining\"
s3_folder_project = \"terraform_ec2\"
s3_folder_region = \"us-east-1\"
s3_folder_type = \"class\"
s3_tfstate_file = \"infrastructure.tfstate\"

environment = \"${params.environment}\"
region        = \"us-east-1\"
public_key    = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDXUI8Mt0W/65CPA5rnR4auE8qVb08c6qR9Ca0yQaz9xM6EuShYX8jmktYbrdCIkZTMXbRF58CkWID/NHjYX4ZWZHwLi5uf2RfQegF67+kv6yJ2cgG4AsxUmWqlznxvm9615r8tpzBkKgsya58H+4aPRKqLJmhRm3ZZCa7t2HE7S+RR7fq+WtaQ3BMaKog9AVfHSEP8Gp4Ho7WUv5YlLXu5hlYC+m2oxrSCqXRFIhDtDuyphkzS93gDy8EVBkWnJFkoXT2LbVydcJaNCpEdjB1YFEEc1kMOXCAZ0w5N8PiWgdlY0lPeRXdH1RLX+WCM5FVOT9ujrq8PTQSYIkl2pek3 ikambarov@Islams-MacBook-Pro.local\"	
ami_id        = \"${params.ami_id}\"
"""

node{
    stage("Pull Terraform Repo"){
        git 'https://github.com/ikambarov/terraform-ec2.git'
    }

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${region}"]) {
            stage("Init"){
                writeFile file: "${params.environment}.tfvars", text: "${tfvars}"
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