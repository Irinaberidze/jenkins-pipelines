node{
    stage("Check Connection"){
        withCredentials([sshUserPrivateKey(credentialsId: 'ansible-sshkey', keyFileVariable: 'ANSIBLE_KEY', usernameVariable: 'ANSIBLE_USER')]) {
            sh """
                export ANSIBLE_HOST_KEY_CHECKING=False
                ansible -i '167.172.136.103,' --private-key=$ANSIBLE_KEY all -m ping
            """
        }
    }
}