template = """
apiVersion: v1
kind: Pod
metadata:
  name: terraform
spec:
  containers:
  - args:
    - sleep
    - "100000"
    image: ikambarov/terraform:0.14
    name: "terraform"
"""

podTemplate(name: 'terraform', label: 'terraform', yaml: template) {
    node('terraform'){
        stage("Test"){
            container("terraform"){
                sh "terraform version"
            }
        }
    }
}