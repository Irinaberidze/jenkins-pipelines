podName = "podman"

template = """
apiVersion: v1
kind: Pod
metadata:
  name: podman
spec:
  containers:
  - image: ikambarov/podman
    name: ${podName}
"""
podTemplate(name: podName, label: podName, showRawYaml: false, yaml: template) {
    node(podName){
        container(podName){
            stage("Pull Repo"){
                git 'https://github.com/ikambarov/Flaskex-docker.git'
            }

            stage("Build"){
                sh "podman build -t ikambarov/flaskex ."
            }

            stage("Get Images"){
                sh "podman images"
            }
        }
    }    
}