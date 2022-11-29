podName = "helm"

template = """
apiVersion: v1
kind: Pod
metadata:
  name: helm
spec:
  serviceAccount: deployer-sa
  containers:
  - image: ikambarov/k8s-tools
    name: ${podName}
"""

podTemplate(name: podName, label: podName, showRawYaml: false, yaml: template) {
    node(podName){
        container(podName){
            stage("Pull Repo"){
                git 'https://github.com/ikambarov/flaskex-chart.git'
            }

            stage("Helm Install"){
                sh "helm install myapp .  -n default "
            }            
        }
    }    
}