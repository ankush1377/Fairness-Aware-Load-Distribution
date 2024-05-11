# Fairness Aware Load Distribution

## Prerequisites
- Java 17
- Maven
- Docker
- Minikube

## Running Locally

```shell
cd /path/to/project/folder
mvn clean install -DskipTests
docker build -t fairness-aware-load-distribution:0.0.1 .
minikube image rm fairness-aware-load-distribution:0.0.1
minikube image load fairness-aware-load-distribution:0.0.1
kubectl apply -f deployment.yaml
minikube service fairness-aware-load-distribution
```