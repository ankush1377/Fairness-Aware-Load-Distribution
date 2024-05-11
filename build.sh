kubectl delete -f deployment.yaml
mvn clean install -DskipTests
docker build -t fairness-aware-load-distribution:0.0.1 .
minikube image rm fairness-aware-load-distribution:0.0.1
minikube image load fairness-aware-load-distribution:0.0.1
kubectl apply -f deployment.yaml

minikube service fairness-aware-load-distribution