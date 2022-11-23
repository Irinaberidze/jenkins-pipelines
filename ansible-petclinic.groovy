properties([
    parameters([
        string(description: 'Enter VM IP Address', name: 'IP', trim: true)
    ])
])

node{
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-spring-petclinic.git'
    }
    
    withCredentials([sshUserPrivateKey(credentialsId: 'ansible-sshkey', keyFileVariable: 'ANSIBLE_KEY', usernameVariable: 'ANSIBLE_USER')]) {
        stage("Install Java"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i '${params.IP},' --private-key=$ANSIBLE_KEY install_packages.yml
            """
        }

        stage("Pull App Repo"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i '${params.IP},' --private-key=$ANSIBLE_KEY clone_repo.yml
            """
        }

        stage("Build"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i '${params.IP},' --private-key=$ANSIBLE_KEY build.yml
            """
        }

        stage("Start App"){
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i '${params.IP},' --private-key=$ANSIBLE_KEY start_app.yml
            """
        }
    }
}