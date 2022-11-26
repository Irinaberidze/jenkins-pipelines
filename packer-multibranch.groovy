if(env.BRANCH_NAME == "dev"){
    environment = "dev"
    region = "us-east-1"
}
else if(env.BRANCH_NAME == "qa"){
    environment = "qa"
    region = "us-east-2"
}
else if(env.BRANCH_NAME == "master"){
    environment = "prod"
    region = "us-west-2"
}

node{
    stage("Pull Packer Repo"){
        git 'https://github.com/ikambarov/packer.git'
    }

    ami_name = "apache-${UUID.randomUUID().toString()}"

    withCredentials([usernamePassword(credentialsId: 'aws-creds', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${region}", "PACKER_AMI_NAME=${ami_name}"]) {
            stage("Validate"){
                sh 'packer validate apache.json'
            }

            stage("Build"){
                sh 'packer build apache.json'
            }
        }
    }

    build job: 'terraform-ec2-by-ami-name', parameters: [string(name: 'action', value: 'apply'), string(name: 'environment', value: "${environment}"), string(name: 'ami_name', value: "${ami_name}")]
}