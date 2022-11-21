node{
    stage("Check Connection"){
        sh "ansible --version"
    }
}