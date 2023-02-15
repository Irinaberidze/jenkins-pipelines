# checks the ansible version


node{
    stage("check connection"){
        sh "ansible --version"
    }
}