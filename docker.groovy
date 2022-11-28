podName = "podman"

template = """
apiVersion: v1
kind: Pod
metadata:
  name: podman
spec:
  volumes:
  - name: containerd-path
    hostPath:
      path: /tmp/csivvv
  containers:
  - image: quay.io/podman/stable
    name: ${podName}
    args:
    - sleep
    - "100000"
    volumeMounts:
    - name: containerd-path
      mountPath: /var/lib/containers
    securityContext:
      privileged: true
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