properties([
    parameters([
        string(description: 'Enter VM IP Address', name: 'IP', trim: true)
    ])
])

node{
    stage("Pull Repo"){
        git 'https://github.com/ikambarov/ansible-melodi.git'
    }

    stage("Deploy App"){
        withCredentials([sshUserPrivateKey(credentialsId: 'ansible-sshkey', keyFileVariable: 'ANSIBLE_KEY', usernameVariable: 'ANSIBLE_USER')]) {
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible-playbook -i '${params.IP},' --private-key=$ANSIBLE_KEY main.yml
            """
        }
    }
}