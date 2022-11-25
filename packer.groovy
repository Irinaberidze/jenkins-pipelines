properties([
    parameters([
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
}